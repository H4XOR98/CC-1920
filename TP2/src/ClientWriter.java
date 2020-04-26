import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;
    private AtomicBoolean permission;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection, AtomicBoolean permission) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
    }

    @Override
    public void run() {
        byte[] reply;
        try{
	    while(true){
            	if(this.permission.get()){
			System.out.println("aqui cliente");
                	reply = this.cloud.getReply(this.connection.getClientAddress());
                	if(reply != null){
                    		this.connection.getOut().write(reply);
                    		this.connection.getOut().flush();
                    		this.permission.set(false);
                	}
		}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
