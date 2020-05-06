import java.util.HashMap;
import java.util.Map;

public class AnonGWCloud {
    private Map<String,Integer> clients;
    private Map<Integer,Packages> requests;
    private Map<Integer,Packages> replies;

    private  static int clientId = 0;

    public AnonGWCloud() {
        this.clients = new HashMap<>();
        this.requests = new HashMap<>();
        this.replies = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress){
        int result = -1;
        if(!this.clients.containsKey(clientAddress)){
            this.clients.put(clientAddress,clientId);
            result = clientId++;
            System.out.println("Cliente com IP " + clientAddress + " ligou-se e tem id " + result);
        }
        return result;
    }

    public synchronized void insertRequest(String clientAddress, byte[] request) {
        if (clientAddress != null && request != null && this.clients.containsKey(clientAddress)) {
            int id = this.clients.get(clientAddress);
            Packages packages;
            if (!this.requests.containsKey(id)) {
                packages = new Packages();
                this.requests.put(id, packages);
            }
            packages = this.requests.get(id);
            packages.addPackage(request);
        }
    }

    public synchronized byte[] getRequest(int id) {
        byte[] request = null;
        if (this.clients.containsValue(id) && this.requests.containsKey(id)) {
            request = this.requests.get(id).getPackage();
        }
        return request;
    }


    public synchronized void insertReply(int id, byte[] content) {
        if(content != null && this.clients.containsValue(id)){
            Packages packages;
            if (!this.replies.containsKey(id)) {
                packages = new Packages();
                this.replies.put(id, packages);
            }
            packages = this.replies.get(id);
            packages.addPackage(content);
        }
    }

    public synchronized byte[] getReply(String clientAddress){
        byte[] content = null;
        if(this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(this.replies.containsKey(id)){
                content = this.replies.get(id).getPackage();
            }
        }
        return content;
    }

    public synchronized void serverReadComplete(int id){
        if (this.clients.containsValue(id) && this.replies.containsKey(id)){
            Packages packages = this.replies.get(id);
            if (packages != null) {
                packages.complete();
            }
        }
    }


    public synchronized boolean removeClient(String clientAddress){
        boolean result = false;
        if (this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if (this.replies.containsKey(id)){
                Packages packages = this.replies.get(id);
                if (packages != null && packages.isComplete() && packages.size() == 0){
                    this.replies.remove(id);
                    this.clients.remove(clientAddress);
                    result = true;
                }
            }
        }
        return result;
    }
}
