import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] reply;
        try {
	        while(true) {
                if (this.cloud.removeClient(this.connection.getClientAddress())) {
                    this.connection.close();
                    Thread.currentThread().join();
                }
                reply = this.cloud.getReply(this.connection.getClientAddress());
                if (reply != null) {
                    this.connection.getOut().write(reply);
                    this.connection.getOut().flush();
                }
            }
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
