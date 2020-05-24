import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AnonGWServerCloud {
    private ReentrantLock clientsLock = new ReentrantLock();
    private ReentrantLock requestsLock = new ReentrantLock();
    private ReentrantLock repliesLock = new ReentrantLock();
    private ReentrantLock permissionsLock = new ReentrantLock();

    private UDPConnection udpConnection;
    private InetAddress targetServerIP;
    private Map<Integer, InetAddress> clientsOverlayPeer;     // SessionId, overlayPeer
    private Map<Integer, Integer> serverClients;              // SessionId, ClientId
    private Map<Integer, Packets> requests;                   // SessionId, All requests packets
    private Map<Integer, Packets> replies;                    // SessionId, All replies packets
    private Map<Integer, ServerCloudPermissions> permissions; // SessionId

    private static int SESSIONID = 0;

    public AnonGWServerCloud(InetAddress targetServerIP, UDPConnection udpConnection) {
        this.udpConnection = udpConnection;
        this.targetServerIP = targetServerIP;
        this.clientsOverlayPeer = new HashMap<>();
        this.serverClients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replies = new HashMap<>();
        this.permissions = new HashMap<>();
    }

    private int insertClient(int clientId, InetAddress overlayPeer) throws IOException {
        boolean exists = false;
        int sessionId = -1;
        this.clientsLock.lock();
        try {
            for (int sessionIdentifier : this.serverClients.keySet()) {
                if (this.serverClients.get(sessionIdentifier) == clientId && this.clientsOverlayPeer.get(sessionIdentifier).equals(overlayPeer)) {
                    exists = true;
                    sessionId = sessionIdentifier;
                }
            }
            if (!exists) {
                sessionId = SESSIONID++;
                this.serverClients.put(sessionId, clientId);
                this.clientsOverlayPeer.put(sessionId, overlayPeer);

                ServerCloudPermissions permissions = new ServerCloudPermissions();
                this.permissionsLock.lock();
                this.permissions.put(sessionId, permissions);
                this.permissionsLock.unlock();

                this.requestsLock.lock();
                this.requests.put(sessionId, new Packets());
                this.requestsLock.unlock();

                this.repliesLock.lock();
                this.replies.put(sessionId, new Packets());
                this.repliesLock.unlock();

                TCPConnection tcpConnection = new TCPConnection(new Socket(targetServerIP, Constants.TCPPort));
                new Thread(new ServerWriter(this, tcpConnection, permissions.getServerWriterPermission(), sessionId)).start();
                new Thread(new ServerReader(this, tcpConnection, permissions.getServerReaderPermission(), sessionId)).start();
                new Thread(new ServerSender(this, this.udpConnection, permissions.getServerSenderPermission(), sessionId, overlayPeer)).start();

                System.out.println("[client " + clientId + "] inserted in AnonGWServerCloud with sessionId = " + sessionId);

            }
        } catch (IOException e) {
            this.serverClients.remove(sessionId);
            this.clientsOverlayPeer.remove(sessionId);

            this.permissionsLock.lock();
            this.permissions.remove(sessionId);
            this.permissionsLock.unlock();

            this.requestsLock.lock();
            this.requests.remove(sessionId);
            this.requestsLock.unlock();

            this.repliesLock.lock();
            this.replies.remove(sessionId);
            this.repliesLock.unlock();

            throw new IOException(e);
        }finally {
            this.clientsLock.unlock();
        }
        return sessionId;
    }


    /*
        Request Business
    */

    // insert request from UDP
    public void insertRequest(Packet packet, InetAddress overlayPeer) throws IOException {
        if (packet != null && overlayPeer != null) {

            int sessionId = insertClient(packet.getId(), overlayPeer);

            if(sessionId != -1){
                packet.setId(sessionId);
                this.requestsLock.lock();
                this.requests.get(sessionId).addPacket(packet);
                this.requestsLock.unlock();

                this.permissionsLock.lock();
                ServerCloudPermissions serverPermissions = this.permissions.get(sessionId);
                if(!serverPermissions.getServerWriterPermission().get()) serverPermissions.aproveServerWriterPermission();
                this.permissionsLock.unlock();

                //System.out.println("[session " + sessionIdentifier + "] request inserted in AnonGWServerCloud");
            }
        }
    }

    // get request to send through TCP
    public Packet getRequestPacket(int sessionId) {
        Packet packet = null;
        this.requestsLock.lock();
        if(this.requests.containsKey(sessionId)){

            this.permissionsLock.lock();
            ServerCloudPermissions serverPermissions = this.permissions.get(sessionId);
            if(!serverPermissions.getServerReaderPermission().get()) serverPermissions.aproveServerReaderPermission();
            this.permissionsLock.unlock();

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
        this.repliesLock.lock();
        if(this.replies.containsKey(sessionId)){
            Packets packets = this.replies.get(sessionId);
            this.clientsLock.lock();
            Packet packet = new Packet(this.serverClients.get(sessionId),packets.getSequenceNum(),false,Constants.ToClient, reply);
            this.clientsLock.unlock();
            packets.addPacket(packet);

            this.permissionsLock.lock();
            ServerCloudPermissions serverPermissions = this.permissions.get(sessionId);
            if(!serverPermissions.getServerSenderPermission().get()) serverPermissions.aproveServerSenderPermission();
            this.permissionsLock.unlock();

            //System.out.println("[session " + sessionId + "] reply inserted in AnonGWServerCloud");
        }
        this.repliesLock.unlock();
    }

    // insert final packet to sinal that all replys were read
    public synchronized void readComplete(int sessionId){
        this.repliesLock.lock();
        if(this.replies.containsKey(sessionId)){
            Packets packets = this.replies.get(sessionId);
            this.clientsLock.lock();
            Packet packet = new Packet(this.serverClients.get(sessionId), packets.getSequenceNum(), true, Constants.ToClient, "Last".getBytes());
            this.clientsLock.unlock();
            packets.addPacket(packet);
            System.out.println("[session " + sessionId + "] all replies inserted in AnonGWServerCloud");
        }
        this.repliesLock.unlock();
    }

    // get reply to send through UDP
    public synchronized Packet getReplyPacket(int sessionId){
        Packet packet = null;
        this.repliesLock.lock();
        if(this.replies.containsKey(sessionId)){
            packet = this.replies.get(sessionId).pollPacket();
            if(packet != null){
                this.requestsLock.lock();
                if(packet.isLast() && !this.requests.containsKey(sessionId)) {
                    this.clientsLock.lock();
                    this.serverClients.remove(sessionId);
                    this.clientsOverlayPeer.remove(sessionId);
                    this.clientsLock.unlock();

                    this.permissionsLock.lock();
                    this.permissions.remove(sessionId);
                    this.permissionsLock.unlock();

                    this.replies.remove(sessionId);
                }
                this.requestsLock.unlock();
            }
        }
        this.repliesLock.unlock();
        return packet;
    }
}