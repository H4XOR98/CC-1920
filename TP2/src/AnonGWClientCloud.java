import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class AnonGWClientCloud {

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

    public synchronized void insertClient(Socket socket) throws IOException {
        TCPConnection tcpConnection = new TCPConnection(socket);
        if(tcpConnection.getIPAddress() != null && !this.clients.containsKey(tcpConnection.getIPAddress())){
            this.clients.put(tcpConnection.getIPAddress(),CLIENTID);
            this.requests.put(CLIENTID, new Packets());
            this.replys.put(CLIENTID, new Packets());

            Random randomize = new Random();
            new Thread(new ClientReader(this, tcpConnection, CLIENTID)).start();
            new Thread(new ClientWriter(this,tcpConnection)).start();
            InetAddress overlayPeer = overlayPeers.get(randomize.nextInt(overlayPeers.size()));
            new Thread(new ClientSender(this,this.udpConnection,CLIENTID,overlayPeer)).start();
            System.out.println("[client " + CLIENTID + "] inserted in AnonGWClientCloud");
            CLIENTID++;
        }
    }

    /*
        Request Business
    */
    
    // insert request from TCP
    public synchronized void insertRequest(int clientId, byte[] request) {
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),false,Constants.ToServer,request);
            this.requests.get(clientId).addPacket(packet);
            //System.out.println("[client " + clientId + "] request inserted in AnonGWClientCloud");
        }
    }

    // insert final packet to sinal that all requests were read
    public synchronized void readComplete(int clientId){
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),true,Constants.ToServer,"Last".getBytes());
            packets.addPacket(packet);
            packets.complete();
            System.out.println("[client " + clientId + "] all requests inserted in AnonGWClientCloud");
        }
    }

    // get request to send through UDP
    public synchronized Packet getRequestPacket(int clientId){
        Packet packet = null;
        if(this.requests.containsKey(clientId)){
            packet = this.requests.get(clientId).pollPacket();
            if(packet != null){
                if(packet.isLast()) {
                    this.requests.remove(clientId);
                }
            }
        }
        return packet;
    }

    /*
        Reply Business
    */

    // insert reply from UDP
    public synchronized void insertReply(Packet packet) {
        if(packet != null && packet.getData() != null && this.replys.containsKey(packet.getId())){
            Packets packets = this.replys.get(packet.getId());
            packets.addPacket(packet);
            //System.out.println("[client " + packet.getId() + "] reply inserted in AnonGWClientCloud");
            if(packet.isLast()){
                packets.complete();
                System.out.println("[client " + packet.getId() + "] all replies inserted in AnonGWClientCloud");
            }
        }
    }

    // get reply to send through TCP
    public synchronized Packet getReplyPacket(String clientAddress){
        Packet packet = null;
        if(clientAddress != null && this.clients.containsKey(clientAddress)){
            int clientId = this.clients.get(clientAddress);
            if(this.replys.containsKey(clientId)){
                Packets packets = this.replys.get(clientId);
                packet = packets.pollPacket();
                if(packet != null){
                    if(packet.isLast()) {
                        this.replys.remove(clientId);
                        this.clients.remove(clientAddress);
                    }
                }
            }
        }
        return packet;
    }
}