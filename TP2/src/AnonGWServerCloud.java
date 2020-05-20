import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AnonGWServerCloud {
    ReentrantLock clientsLock = new ReentrantLock();
    ReentrantLock requestsLock = new ReentrantLock();
    ReentrantLock replysLock = new ReentrantLock();

    UDPConnection udpConnection;
    InetAddress targetServerIP;
    private Map<Integer, InetAddress> clientsOverlayPeer; // SessionId, overlayPeer
    private Map<Integer, Integer> serverClients;          // SessionId, ClientId
    private Map<Integer, Packets> requests;               // SessionId, All requests packets
    private Map<Integer, Packets> replys;                 // SessionId, All replys packets

    private static int SESSIONID = 0;

    public AnonGWServerCloud(InetAddress targetServerIP, UDPConnection udpConnection) {
        this.udpConnection = udpConnection;
        this.targetServerIP = targetServerIP;
        this.clientsOverlayPeer = new HashMap<>();
        this.serverClients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }

    private int insertClient(int clientId, InetAddress overlayPeer) throws IOException {
        this.clientsLock.lock();
        int sessionId = SESSIONID++;
        this.serverClients.put(sessionId, clientId);
        this.clientsOverlayPeer.put(sessionId, overlayPeer);
        this.clientsLock.unlock();

        this.requestsLock.lock();
        this.requests.put(sessionId, new Packets());
        this.requestsLock.unlock();

        this.replysLock.lock();
        this.replys.put(sessionId, new Packets());
        this.replysLock.unlock();

        try {
            TCPConnection tcpConnection = new TCPConnection(new Socket(targetServerIP, Constants.TCPPort));
            new Thread(new ServerWriter(this, tcpConnection, sessionId)).start();
            new Thread(new ServerReader(this, tcpConnection, sessionId)).start();
            new Thread(new ServerSender(this, this.udpConnection, sessionId, overlayPeer)).start();

            System.out.println("[client " + clientId + "] inserted in AnonGWServerCloud with sessionId = " + sessionId);
        }catch (IOException e){
            this.clientsLock.lock();
            this.serverClients.remove(sessionId);
            this.clientsOverlayPeer.remove(sessionId);
            this.clientsLock.unlock();

            this.requestsLock.lock();
            this.requests.remove(sessionId);
            this.requestsLock.unlock();

            this.replysLock.lock();
            this.replys.remove(sessionId);
            this.replysLock.unlock();

            throw new IOException(e);
        }
        return sessionId;
    }

    /*
        Request Business
    */

    // insert request from UDP
    public void insertRequest(Packet packet, InetAddress overlayPeer) throws IOException {
        if (packet != null && overlayPeer != null) {
            int sessionIdentifier = -1;
            boolean exists = false;
            this.clientsLock.lock();
            for(int sessionId : this.serverClients.keySet()){
                if(this.serverClients.get(sessionId) == packet.getId() && this.clientsOverlayPeer.get(sessionId).equals(overlayPeer)){
                    exists = true;
                    sessionIdentifier = sessionId;
                }
            }
            this.clientsLock.unlock();

            if(!exists) {
                sessionIdentifier = this.insertClient(packet.getId(), overlayPeer);
            }

            this.clientsLock.lock();
            int clientId = this.serverClients.get(sessionIdentifier);
            this.clientsLock.unlock();
            if(sessionIdentifier != -1 && packet.getId() == clientId){
                packet.setId(sessionIdentifier);
                this.requestsLock.lock();
                this.requests.get(sessionIdentifier).addPacket(packet);
                this.requestsLock.unlock();
                //System.out.println("[session " + sessionIdentifier + "] request inserted in AnonGWServerCloud");
            }
        }
    }

    // get request to send through TCP
    public Packet getRequestPacket(int sessionId) {
        Packet packet = null;
        this.requestsLock.lock();
        if(this.requests.containsKey(sessionId)){
            packet = this.requests.get(sessionId).pollPacket();
            if(packet != null) {
                if (packet.isLast()) {
                    this.requests.remove(sessionId);
                }
            }
        }
        this.requestsLock.unlock();
        return packet;
    }

    /*
        Reply Business
    */

    // insert reply from TCP
    public synchronized void insertReply(int sessionId, byte[] reply) {
        this.replysLock.lock();
        if(this.replys.containsKey(sessionId)){
            Packets packets = this.replys.get(sessionId);
            this.clientsLock.lock();
            int clientId = this.serverClients.get(sessionId);
            this.clientsLock.unlock();
            Packet packet = new Packet(clientId,packets.getSequenceNum(),false,Constants.ToClient, reply);
            packets.addPacket(packet);
            //System.out.println("[session " + sessionId + "] reply inserted in AnonGWServerCloud");
        }
        this.replysLock.unlock();
    }

    // insert final packet to sinal that all replys were read
    public synchronized void readComplete(int sessionId){
        this.replysLock.lock();
        if(this.replys.containsKey(sessionId)){
            Packets packets = this.replys.get(sessionId);
            this.clientsLock.lock();
            int clientId =this.serverClients.get(sessionId);
            this.clientsLock.unlock();
            Packet packet = new Packet(clientId, packets.getSequenceNum(), true, Constants.ToClient, "Last".getBytes());
            packets.addPacket(packet);
            packets.complete();
            System.out.println("[session " + sessionId + "] all replies inserted in AnonGWServerCloud");
        }
        this.replysLock.unlock();
    }

    // get reply to send through UDP
    public synchronized Packet getReplyPacket(int sessionId){
        Packet packet = null;
        this.replysLock.lock();
        if(this.replys.containsKey(sessionId)){
            packet = this.replys.get(sessionId).pollPacket();
            if(packet != null){
                this.requestsLock.lock();
                boolean existsRequests = this.requests.containsKey(sessionId);
                this.requestsLock.unlock();
                if(packet.isLast() && !existsRequests) {
                    this.clientsLock.lock();
                    this.serverClients.remove(sessionId);
                    this.clientsOverlayPeer.remove(sessionId);
                    this.clientsLock.unlock();
                    this.replys.remove(sessionId);
                }
            }
        }
        this.replysLock.unlock();
        return packet;
    }
}