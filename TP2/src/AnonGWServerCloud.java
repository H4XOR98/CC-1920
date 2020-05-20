import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AnonGWServerCloud {
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

    private synchronized int insertClient(int clientId, InetAddress overlayPeer) throws IOException {
        this.serverClients.put(SESSIONID, clientId);
        this.clientsOverlayPeer.put(SESSIONID, overlayPeer);
        this.requests.put(SESSIONID, new Packets());
        this.replys.put(SESSIONID, new Packets());

        TCPConnection tcpConnection = new TCPConnection(new Socket(targetServerIP, Constants.TCPPort));
        new Thread(new ServerWriter(this, tcpConnection, SESSIONID)).start();
        new Thread(new ServerReader(this, tcpConnection,SESSIONID)).start();
        new Thread(new ServerSender(this, this.udpConnection,SESSIONID,overlayPeer)).start();

        System.out.println("[client " + clientId + "] inserted in AnonGWServerCloud with sessionId = " + SESSIONID);
        return SESSIONID++;
    }

    /*
        Request Business
    */

    // insert request from UDP
    public synchronized void insertRequest(Packet packet, InetAddress overlayPeer) throws IOException {
        if (packet != null && overlayPeer != null) {
            int sessionIdentifier = -1;
            boolean exists = false;
            for(int sessionId : this.serverClients.keySet()){
                if(this.serverClients.get(sessionId) == packet.getId() && this.clientsOverlayPeer.get(sessionId).equals(overlayPeer)){
                    exists = true;
                    sessionIdentifier = sessionId;
                }
            }
            if(!exists) {
                sessionIdentifier = this.insertClient(packet.getId(), overlayPeer);
            }

            if(sessionIdentifier != -1 && packet.getId() == this.serverClients.get(sessionIdentifier)){
                packet.setId(sessionIdentifier);
                this.requests.get(sessionIdentifier).addPacket(packet);
                //System.out.println("[session " + sessionIdentifier + "] request inserted in AnonGWServerCloud");
            }
        }
    }

    // get request to send through TCP
    public synchronized Packet getRequestPacket(int sessionId) {
        Packet packet = null;
        if(this.requests.containsKey(sessionId)){
            packet = this.requests.get(sessionId).pollPacket();
            if(packet != null) {
                if (packet.isLast()) {
                    this.requests.remove(sessionId);
                }
            }
        }
        return packet;
    }

    /*
        Reply Business
    */

    // insert reply from TCP
    public synchronized void insertReply(int sessionId, byte[] reply) {
        if(this.replys.containsKey(sessionId)){
            Packets packets = this.replys.get(sessionId);
            Packet packet = new Packet(this.serverClients.get(sessionId),packets.getSequenceNum(),false,Constants.ToClient, reply);
            packets.addPacket(packet);
            //System.out.println("[session " + sessionId + "] reply inserted in AnonGWServerCloud");
        }
    }

    // insert final packet to sinal that all replys were read
    public synchronized void readComplete(int sessionId){
        if(this.replys.containsKey(sessionId)){
            Packets packets = this.replys.get(sessionId);
            Packet packet = new Packet(this.serverClients.get(sessionId), packets.getSequenceNum(), true, Constants.ToClient, "Last".getBytes());
            packets.addPacket(packet);
            packets.complete();
            System.out.println("[session " + sessionId + "] all replies inserted in AnonGWServerCloud");
        }
    }

    // get reply to send through UDP
    public synchronized Packet getReplyPacket(int sessionId){
        Packet packet = null;
        if(this.replys.containsKey(sessionId)){
            packet = this.replys.get(sessionId).pollPacket();
            if(packet != null){
                if(packet.isLast() && !this.requests.containsKey(sessionId)) {
                    this.serverClients.remove(sessionId);
                    this.clientsOverlayPeer.remove(sessionId);
                    this.replys.remove(sessionId);
                }
            }
        }
        return packet;
    }
}