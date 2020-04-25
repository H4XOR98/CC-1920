import java.util.HashMap;
import java.util.Map;

public class AnonGWCloud {
    private Map<String,Integer> clients;
    private Map<Integer, WritePermission> writePermissions;
    private Map<Integer,byte[]> requests;
    private Map<Integer,byte[]> replys;

    private  static int clientId = 0;

    public AnonGWCloud() {
        this.clients = new HashMap<>();
        this.writePermissions = new HashMap<>();
        this.requests = new HashMap<>();
        this.replys = new HashMap<>();
    }

    public synchronized int insertClient(String clientAddress, WritePermission writePermission){
        int result = -1;
        if(!this.clients.containsKey(clientAddress)){
            this.clients.put(clientAddress,clientId);
            this.writePermissions.put(clientId, writePermission);
            result = clientId++;
            System.out.println("Cliente com IP " + clientAddress + " ligou-se e tem id " + result);
        }
        return result;
    }

    public synchronized void insertRequest(String clientAddress, byte[] request) {
        if (clientAddress != null && request != null && this.clients.containsKey(clientAddress)) {
            int id = this.clients.get(clientAddress);
            if (!this.requests.containsKey(id) && this.writePermissions.containsKey(id)) {
                this.requests.put(id, request);
                this.writePermissions.get(id).getServerWritePermission().set(true);
                System.out.println("Request introduzido com sucesso ? " + this.requests.containsKey(id) + " request " + this.requests.get(id));
            }
        }
    }

    public synchronized byte[] getRequest(int id) {
        byte[] request = null;
        if (this.clients.containsValue(id) && this.requests.containsKey(id)) {
            request = this.requests.get(id);
            this.requests.remove(id);
            System.out.println("Request :" + request);
        }
        return request;
    }


    public synchronized void insertReply(int id, byte[] content) {
        if(content != null && this.clients.containsValue(id) && !this.replys.containsKey(id) && this.writePermissions.containsKey(id)){
            this.replys.put(id,content);
            this.writePermissions.get(id).getClientWritePermission().set(true);
            System.out.println("Permissao " + this.writePermissions.get(id).getClientWritePermission().get());
            System.out.println("Reply introduzida com sucesso? " + this.replys.containsKey(id));
        }
    }

    public synchronized byte[] getReply(String clientAddress){
        byte[] content = null;
        if(this.clients.containsKey(clientAddress)){
            int id = this.clients.get(clientAddress);
            if(this.replys.containsKey(id) && this.writePermissions.containsKey(id)){
                content = this.replys.get(id).clone();
                this.replys.remove(id);
                this.writePermissions.remove(id);
                this.clients.remove(clientAddress);
                System.out.println("Reply :" + content);
            }
        }
        return content;
    }

}
