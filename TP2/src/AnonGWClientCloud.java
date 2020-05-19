import java.util.HashMap;
import java.util.Map;

public class AnonGWClientCloud {
    private Map<String, Integer> clients;
    private Map<Integer, Packets> requests;

    private static int CLIENTID = 0;

    public AnonGWClientCloud() {
        this.clients = new HashMap<>();
        this.requests = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress) {
        int clientId = -1;
        if(clientAddress != null && !this.clients.containsKey(clientAddress)){
           this.clients.put(clientAddress,CLIENTID);
           this.requests.put(CLIENTID, new Packets());
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
            Packets clientPackets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,clientPackets.getSequenceNum(),false,Constants.ToServer,request);
            this.requests.get(clientId).addPacket(packet);
            System.out.println("[AnonGWClientCloud -> insertRequest]  :  " + clientId);
        }
    }


    public synchronized void readComplete(int clientId){
        if(this.requests.containsKey(clientId)){
            Packets clientPackets = this.requests.get(clientId);
            Packet packet = new Packet(clientId,clientPackets.getSequenceNum(),true,Constants.ToServer,"Last".getBytes());
            clientPackets.addPacket(packet);
            clientPackets.complete();
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
}
