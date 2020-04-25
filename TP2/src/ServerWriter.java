import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerWriter implements Runnable {
    private AnonGWCloud cloud;
    private ServerConnection connection;
    private AtomicBoolean writePermission;

    public ServerWriter(AnonGWCloud cloud, ServerConnection connection, AtomicBoolean writePermission) {
        this.cloud = cloud;
        this.connection = connection;
        this.writePermission = writePermission;
    }

    @Override
    public void run() {
        byte[] request;
        try {
            while (writePermission.get()) {
                request = this.cloud.getRequest(this.connection.getClientId());
                if (request != null) {
                    this.connection.getOut().write(request);
                    this.connection.getOut().flush();
                    this.writePermission.set(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
