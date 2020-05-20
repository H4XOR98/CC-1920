import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AnonGWClientCloud {
    private ReentrantLock clientsLock = new ReentrantLock();
    private ReentrantLock requestsLock = new ReentrantLock();
    private ReentrantLock replysLock = new ReentrantLock();


    private UDPConnection udpConnection;
    private List<InetAddress> overlayPeers;
    private Map<String, Integer> clients;
    private Map<Integer, Packets> requests;
    private Map<Integer, Packets> replys;

    private static int CLIENTID = 0;

    public AnonGWClientCloud(UDPConnection udpConnection, List<InetAddress> overlayPeers) {
        this.udpConnection = udpConnection;
        this.overlayPeers = overlayPeers;
        this.clients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }

    public void insertClient(Socket socket) throws IOException {
        TCPConnection tcpConnection = new TCPConnection(socket);
        this.clientsLock.lock();
        boolean clientExists = this.clients.containsKey(tcpConnection.getIPAddress());
        this.clientsLock.unlock();

        if(tcpConnection.getIPAddress() != null && !clientExists){
            this.clientsLock.lock();
            int clientId = CLIENTID++;
            this.clients.put(tcpConnection.getIPAddress(),clientId);
            this.clientsLock.unlock();

            this.requestsLock.lock();
            this.requests.put(clientId, new Packets());
            this.requestsLock.unlock();

            this.replysLock.lock();
            this.replys.put(clientId, new Packets());
            this.replysLock.unlock();

            Random randomize = new Random();
            new Thread(new ClientReader(this, tcpConnection, clientId)).start();
            new Thread(new ClientWriter(this,tcpConnection)).start();
            InetAddress overlayPeer = overlayPeers.get(randomize.nextInt(overlayPeers.size()));
            new Thread(new ClientSender(this,this.udpConnection,clientId,overlayPeer)).start();
            System.out.println("[client " + clientId + "] inserted in AnonGWClientCloud");
        }
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
            packets.complete();
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
        this.replysLock.lock();
        if(packet != null && packet.getData() != null && this.replys.containsKey(packet.getId())){
            Packets packets = this.replys.get(packet.getId());
            packets.addPacket(packet);
            //System.out.println("[client " + packet.getId() + "] reply inserted in AnonGWClientCloud");
            if(packet.isLast()){
                packets.complete();
                System.out.println("[client " + packet.getId() + "] all replies inserted in AnonGWClientCloud");
            }
        }
        this.replysLock.unlock();
    }

    // get reply to send through TCP
    public Packet getReplyPacket(String clientAddress){
        Packet packet = null;
        int clientId = -1;
        this.clientsLock.lock();
        if(this.clients.containsKey(clientAddress)){
           clientId = this.clients.get(clientAddress);
        }
        this.clientsLock.unlock();

        this.replysLock.lock();
        if(clientId != -1 && clientAddress != null && this.replys.containsKey(clientId)){
            Packets packets = this.replys.get(clientId);
            packet = packets.pollPacket();
            if(packet != null){
                if(packet.isLast()) {
                    this.replys.remove(clientId);
                    this.clientsLock.lock();
                    this.clients.remove(clientAddress);
                    this.clientsLock.unlock();
                }
            }
        }
        this.replysLock.unlock();
        return packet;
    }
}