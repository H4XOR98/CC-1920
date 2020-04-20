import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class AnonGWCloud {
    private Map<String,Integer> clients;
    private Map<Integer,String> clientsRequests;
    private Map<Integer,byte[]> serversReplys;
    private Queue<Integer> requestsQueue;

    private static int requestId = 0;
    private final String serverAddress;
    private final String localAnonGWAddress;

    public AnonGWCloud(String serverAddress, String localAnonGWAddress){
        this.clients = new HashMap<>();
        this.clientsRequests = new HashMap<>();
        this.serversReplys = new HashMap<>();
        this.requestsQueue = new PriorityQueue<>();
        this.serverAddress = serverAddress;
        this.localAnonGWAddress = localAnonGWAddress;
    }

    public synchronized void insertRequest(String clientAddress, String request){
        System.out.println(clientAddress + " " + request + " " + clients.containsKey(clientAddress));
        if(clientAddress != null && request != null && !clients.containsKey(clientAddress)){
            this.clients.put(clientAddress,requestId);
            this.clientsRequests.put(requestId,request.replace(serverAddress, localAnonGWAddress));
            requestsQueue.add(requestId);
            System.out.println(clients.containsKey(clientAddress));
            requestId++;
        }
    }

    public synchronized void insertReply(int id, byte[] file){
        if(clients.containsKey(id) && file != null){
            this.serversReplys.put(requestId,file);
        }
    }

    public synchronized Pair<Integer,String> getRequest(){
        Pair<Integer,String> request = null;
        if(this.requestsQueue.size() > 0){
            int id = this.requestsQueue.poll();
            if(this.clientsRequests.containsKey(id)){
                request = new Pair(id,this.clientsRequests.get(id));
                this.clientsRequests.remove(id);
            }
        }
        return request;
    }

    public synchronized byte[] getReply(String clientAddress){
        byte[] file = null;

        if(this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(this.serversReplys.containsKey(id)){
                file = this.serversReplys.get(id);
                this.serversReplys.remove(id);
            }
        }
        return file;
    }
}