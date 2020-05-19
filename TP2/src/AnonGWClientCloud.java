import java.util.HashMap;
import java.util.Map;

public class AnonGWClientCloud {
    private Map<String, Integer> clients;
    private Map<Integer, Packets> requests;
    private Map<Integer, Packets> replys;

    private static int CLIENTID = 0;

    public AnonGWClientCloud() {
        this.clients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress) {
        int clientId = -1;
        if(clientAddress != null && !this.clients.containsKey(clientAddress)){
           this.clients.put(clientAddress,CLIENTID);
           this.requests.put(CLIENTID, new Packets());
           this.replys.put(CLIENTID, new Packets());
           clientId = CLIENTID;
           CLIENTID++;
        }
        return clientId;
    }


    /*
        Request Business
     */
    public synchronized void insertRequest(int clientId, byte[] request) {
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),false,Constants.ToServer,request);
            this.requests.get(clientId).addPacket(packet);
            System.out.println("[AnonGWClientCloud -> insertRequest]  :  " + clientId);
        }
    }


    public synchronized void readComplete(int clientId){
        if(this.requests.containsKey(clientId)){
            Packets packets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,packets.getSequenceNum(),true,Constants.ToServer,"Last".getBytes());
            packets.addPacket(packet);
            packets.complete();
            System.out.println("[AnonGWClientCloud -> readComplete]  :  " + clientId);
        }
    }

    public synchronized Packet getRequestPacket(int clientId){
        Packet packet = null;
        if(this.requests.containsKey(clientId)){
            packet = this.requests.get(clientId).pollPacket();
            if(packet != null){
                System.out.println("[AnonGWClientCloud -> getRequestPacket]  :  " + clientId);
                if(packet.isLast()) {
                    this.requests.remove(clientId);
                    System.out.println("Ultimo Request");
                }
            }
        }
        return packet;
    }


    /*
        Reply Business
     */
    public synchronized void insertReply(Packet packet) {
        if(packet != null && packet.getData() != null && this.replys.containsKey(packet.getId())){
            Packets packets = this.replys.get(packet.getId());
            packets.addPacket(packet);
            if(packet.isLast()){
                packets.complete();
            }
        }
    }

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
