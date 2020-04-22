import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class AnonGWCloud {
    private Map<String,Integer> clients;
    private Map<Integer,String> requests;
    private Map<Integer,byte[]> replys;

    private static int clientId = 0;

    public AnonGWCloud(){
        this.clients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress){
        int result = -1;
        if(!this.clients.containsKey(clientAddress)){
            this.clients.put(clientAddress,clientId);
            result = clientId++;
        }
        return result;
    }

    public synchronized void insertRequest(String clientAddress, String request){
        if(clientAddress != null && request != null && this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(!this.requests.containsKey(id)){
                this.requests.put(id, request);
            }
        }
    }

    public synchronized void insertReply(int id, byte[] content){
        if(content != null && this.clients.containsValue(id) && !this.replys.containsKey(id)){
            this.replys.put(id,content);
        }
    }

    public synchronized String getRequest(int id) {
        String request = null;
        if (this.clients.containsValue(id) && this.requests.containsKey(id)) {
            request = this.requests.get(id);
            this.requests.remove(id);
        }
        return request;
    }

    public synchronized byte[] getReply(String clientAddress){
        byte[] content = null;
        if(this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(this.replys.containsKey(id)){
                content = this.replys.get(id).clone();
                this.replys.remove(id);
                this.clients.remove(clientAddress);
            }
        }
        return content;
    }
}