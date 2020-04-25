import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;
    private AtomicBoolean writePermission;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection, AtomicBoolean writePermission) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
        this.writePermission = writePermission;
    }

    @Override
    public void run() {
        byte[] reply;
        try{
            while(writePermission.get()){
                reply = this.cloud.getReply(this.connection.getClientAddress());
                if(reply != null){
                    this.connection.getOut().write(reply);
                    this.connection.getOut().flush();;
                    this.writePermission.set(false);
                    this.connection.close();
                    Thread.currentThread().join();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
