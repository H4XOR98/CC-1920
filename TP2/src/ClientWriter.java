import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;
    private AtomicBoolean writePermission;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection, AtomicBoolean writePermission) {
        this.cloud = cloud;
        this.connection = connection;
        this.writePermission = writePermission;
    }

    @Override
    public void run() {
        byte[] reply;
        try{
            while(this.writePermission.get()){
                reply = this.cloud.getReply(this.connection.getClientAddress());
                if(reply != null){
                    this.connection.getOut().write(reply);
                    this.connection.getOut().flush();
                    this.writePermission.set(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
