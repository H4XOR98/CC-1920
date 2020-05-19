import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AnonGWServerCloud {
    UDPConnection udpConnection;
    InetAddress targetServerIP;
    private Map<Integer, Integer> serverClients; //ClientId, SessionId
    private Map<Integer, Packets> requests; //SessionId, All requests packets
    private Map<Integer, Packets> replys;

    private static int SESSIONID = 0;

    public AnonGWServerCloud(InetAddress targetServerIP, UDPConnection udpConnection) {
        this.udpConnection = udpConnection;
        this.targetServerIP = targetServerIP;
        this.serverClients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }


    /*
        Request Business
     */

    private synchronized void insertClient(int clientId, InetAddress overlayPeer) throws IOException {
        if(!this.serverClients.containsKey(clientId)){
            this.serverClients.put(clientId, SESSIONID);
            this.requests.put(SESSIONID, new Packets());
            this.replys.put(SESSIONID, new Packets());

            TCPConnection tcpConnection = new TCPConnection(new Socket(targetServerIP, Constants.TCPPort));
            new Thread(new ServerWriter(this, tcpConnection, SESSIONID)).start();
            new Thread(new ServerReader(this, tcpConnection,SESSIONID)).start();
            new Thread(new ServerSender(this, this.udpConnection,clientId,overlayPeer)).start();

            System.out.println("[AnonGWServerCloud -> insertClient]  :  " + clientId + " : " + SESSIONID);
            SESSIONID++;
        }
    }

    public synchronized void insertRequest(Packet packet, InetAddress overlayPeer) throws IOException {
        if (packet != null && overlayPeer != null) {
            if (!this.serverClients.containsKey(packet.getId())) {
                this.insertClient(packet.getId(), overlayPeer);
            }
            int sessionId = this.serverClients.get(packet.getId());

            packet.setId(sessionId);

            this.requests.get(sessionId).addPacket(packet);
            System.out.println("[AnonGWServerCloud -> insertRequest]  :  " + packet.getSequenceNum() + " : " );
        }
    }

    public synchronized Packet getRequestPacket(int sessionId) {
        Packet packet = null;
        if(this.requests.containsKey(sessionId)){
            packet = this.requests.get(sessionId).pollPacket();
            if(packet != null) {
                System.out.println("[AnonGWServerCloud -> getRequestPacket]  :  " + packet.getSequenceNum() + " : ");
                if (packet.isLast()) {
                    this.requests.remove(sessionId);
                    System.out.println("Ultimo Request");
                }
            }
        }
        return packet;
    }


    /*
        Reply Business
     */


    public synchronized void insertReply(int sessionId, byte[] reply) {
        if(this.replys.containsKey(sessionId)){
	    System.out.println("Tudo fdd");
            int clientIdentifier = -1;
            for(int clientId : this.serverClients.keySet()){
                if(sessionId == this.serverClients.get(clientId)){
                    clientIdentifier = clientId;
                    break;
                }
            }

            if(clientIdentifier != -1){
                Packets packets = this.replys.get(sessionId);
                Packet packet = new Packet(clientIdentifier,packets.getSequenceNum(),false,Constants.ToClient, reply);
                packets.addPacket(packet);
                System.out.println("[AnonGWClientCloud -> insertReply]  :  " + sessionId);
            }
        }
    }


    public synchronized void readComplete(int sessionId){
        if(this.replys.containsKey(sessionId)){
            Packets packets = this.replys.get(sessionId);
            int clientIdentifier = -1;
            for (int clientId : this.serverClients.keySet()){
                if (sessionId == this.serverClients.get(clientId)){
                    clientIdentifier = clientId;
                    break;
                }
            }
            if(clientIdentifier != -1){
                Packet packet = new Packet(clientIdentifier, packets.getSequenceNum(), true, Constants.ToClient, "Last".getBytes());
                packets.addPacket(packet);
                packets.complete();
                System.out.println("[AnonGWServerCloud -> readComplete]  :  " + clientIdentifier);
            }
        }
    }


    public synchronized Packet getReplyPacket(int sessionId){
        Packet packet = null;
        if(this.replys.containsKey(sessionId)){
            packet = this.replys.get(sessionId).pollPacket();
            if(packet != null){
                System.out.println("[AnonGWClientCloud -> getReplyPacket]  :  " + sessionId);
                if(packet.isLast()) {
                    this.serverClients.remove(packet.getId());
                    this.replys.remove(sessionId);
                    System.out.println("Ultima Reply");
                }
            }
        }
        return packet;
    }



}




















