import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AnonGWClientCloud {
    private ReentrantLock clientsLock = new ReentrantLock();
    private ReentrantLock requestsLock = new ReentrantLock();
    private ReentrantLock repliesLock = new ReentrantLock();
    private ReentrantLock permissionsLock = new ReentrantLock();


    private UDPConnection udpConnection;
    private List<InetAddress> overlayPeers;
    private Map<String, Integer> clients;
    private Map<Integer, ClientCloudPermissions> permissions;
    private Map<Integer, Packets> requests;
    private Map<Integer, Packets> replies;

    private static int CLIENTID = 0;

    public AnonGWClientCloud(UDPConnection udpConnection, List<InetAddress> overlayPeers) {
        this.udpConnection = udpConnection;
        this.overlayPeers = overlayPeers;
        this.clients = new HashMap<>();
        this.permissions = new HashMap<>();
        this.requests = new HashMap<>();
        this.replies = new HashMap<>();
    }

    public void insertClient(Socket socket) throws IOException {
        TCPConnection tcpConnection = new TCPConnection(socket);
        this.clientsLock.lock();
        if(tcpConnection.getIPAddress() != null && !this.clients.containsKey(tcpConnection.getIPAddress())){
            int clientId = CLIENTID++;
            this.clients.put(tcpConnection.getIPAddress(),clientId);

            ClientCloudPermissions permissions = new ClientCloudPermissions();
            this.permissionsLock.lock();
            this.permissions.put(clientId, permissions);
            this.permissionsLock.unlock();

            this.requestsLock.lock();
            this.requests.put(clientId, new Packets());
            this.requestsLock.unlock();

            this.repliesLock.lock();
            this.replies.put(clientId, new Packets());
            this.repliesLock.unlock();

            Random randomize = new Random();
            new Thread(new ClientReader(this, tcpConnection, clientId)).start();
            new Thread(new ClientWriter(this, tcpConnection, permissions.getClientWriterPermission())).start();
            InetAddress overlayPeer = overlayPeers.get(randomize.nextInt(overlayPeers.size()));
            new Thread(new ClientSender(this, this.udpConnection, permissions.getClientSenderPermission(), clientId, overlayPeer)).start();
            System.out.println("[client " + clientId + "] inserted in AnonGWClientCloud");
        }
        this.clientsLock.unlock();
    }

    /*
        Request Business
    */
    
    // insert request from TCP
    public void insertRequest(int clientId, byte[] request) {
        this.requestsLock.lock();
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),false,Constants.ToServer,request);
            this.requests.get(clientId).addPacket(packet);
            this.permissionsLock.lock();
            ClientCloudPermissions clientPermissions = this.permissions.get(clientId);
            if(!clientPermissions.getClientSenderPermission().get()) clientPermissions.aproveClientSenderPermssion();
            this.permissionsLock.unlock();
        }
        this.requestsLock.unlock();
    }

    // insert final packet to sinal that all requests were read
    public void readComplete(int clientId){
        this.requestsLock.lock();
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),true,Constants.ToServer,"Last".getBytes());
            packets.addPacket(packet);
            System.out.println("[client " + clientId + "] all requests inserted in AnonGWClientCloud");
        }
        this.requestsLock.unlock();
    }

    // get request to send through UDP
    public Packet getRequestPacket(int clientId){
        Packet packet = null;
        this.requestsLock.lock();
        if(this.requests.containsKey(clientId)){
            packet = this.requests.get(clientId).pollPacket();
            if(packet != null){
                if(packet.isLast()) {
                    this.requests.remove(clientId);
                }
            }
        }
        this.requestsLock.unlock();
        return packet;
    }

    /*
        Reply Business
    */

    // insert reply from UDP
    public void insertReply(Packet packet) {
        this.repliesLock.lock();
        if(packet != null && packet.getData() != null && this.replies.containsKey(packet.getId())){
            Packets packets = this.replies.get(packet.getId());
            packets.addPacket(packet);
            //System.out.println("[client " + packet.getId() + "] reply inserted in AnonGWClientCloud");
            if(packet.isLast()){
                System.out.println("[client " + packet.getId() + "] all replies inserted in AnonGWClientCloud");
            }
            this.permissionsLock.lock();
            ClientCloudPermissions clientPermissions = this.permissions.get(packet.getId());
            if(!clientPermissions.getClientWriterPermission().get()) clientPermissions.aproveClientWriterPermission();
            this.permissionsLock.unlock();
        }
        this.repliesLock.unlock();
    }

    // get reply to send through TCP
    public Packet getReplyPacket(String clientAddress){
        Packet packet = null;
        this.clientsLock.lock();
        if(this.clients.containsKey(clientAddress)){
            int clientId = this.clients.get(clientAddress);
            this.repliesLock.lock();
            if(clientAddress != null && this.replies.containsKey(clientId)){
                Packets packets = this.replies.get(clientId);
                packet = packets.pollPacket();
                if(packet != null){
                    if(packet.isLast()) {
                        this.replies.remove(clientId);

                        this.permissionsLock.lock();
                        this.permissions.remove(clientId);
                        this.permissionsLock.unlock();

                        this.clients.remove(clientAddress);
                    }
                }
            }
            this.repliesLock.unlock();
        }
        this.clientsLock.unlock();
        return packet;
    }
}