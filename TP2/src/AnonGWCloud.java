import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnonGWCloud {
    private Map<String,Integer> clients;
    private Map<Integer,WriterPermission> writerPermissions;
    private Map<Integer,byte[]> requests;
    private Map<Integer,Reply> replies;

    private  static int clientId = 0;

    public AnonGWCloud() {
        this.clients = new HashMap<>();
        this.writerPermissions = new HashMap<>();
        this.requests = new HashMap<>();
        this.replies = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress, WriterPermission wp){
        int result = -1;
        if(!this.clients.containsKey(clientAddress)){
            this.clients.put(clientAddress,clientId);
            this.writerPermissions.put(clientId, wp);
            result = clientId++;
            System.out.println("Cliente com IP " + clientAddress + " ligou-se e tem id " + result);
        }
        return result;
    }

    public synchronized void insertRequest(String clientAddress, byte[] request) {
        if (clientAddress != null && request != null && this.clients.containsKey(clientAddress)) {
            int id = this.clients.get(clientAddress);
            if (!this.requests.containsKey(id)) {
                this.requests.put(id, request);
            }
            this.writerPermissions.get(id).getServerWriterPermission().set(true);
        }
    }

    public synchronized byte[] getRequest(int id) {
        byte[] request = null;
        if (this.clients.containsValue(id) && this.requests.containsKey(id)) {
            request = this.requests.get(id);
            this.requests.remove(id);
        }
        return request;
    }


    public synchronized void insertReply(int id, byte[] content) {
        if(content != null && this.clients.containsValue(id)){
            Reply reply;
            if (!this.replies.containsKey(id)) {
                reply = new Reply();
                this.replies.put(id, reply);
            }
            reply = this.replies.get(id);
            reply.addReply(content);
        }
        this.writerPermissions.get(id).getClientWriterPermission().set(true);
    }

    public synchronized byte[] getReply(String clientAddress){
        byte[] content = null;
        if(this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(this.replies.containsKey(id)){
                content = this.replies.get(id).getReply();
            }
        }
        return content;
    }

    public synchronized void serverReadComplete(int id){
        if (this.clients.containsValue(id) && this.replies.containsKey(id)){
            Reply reply = this.replies.get(id);
            if (reply != null) {
                reply.complete();
            }
        }
    }

    public synchronized boolean removeClient(String clientAddress){
        boolean result = false;
        if (this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if (this.writerPermissions.containsKey(id) && this.replies.containsKey(id)){
                Reply reply = this.replies.get(id);
                if (reply != null && reply.isComplete() && reply.size() == 0){
                    this.replies.remove(id);
                    this.writerPermissions.remove(id);
                    this.clients.remove(clientAddress);
                    result = true;
                }
            }
        }
        return result;
    }
}
