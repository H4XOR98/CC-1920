import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerWriter implements Runnable {
    private AnonGWCloud cloud;
    private ServerConnection connection;
    private AtomicBoolean permission;

    public ServerWriter(AnonGWCloud cloud, ServerConnection connection, AtomicBoolean permission) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
    }

    @Override
    public void run() {
        byte[] request;
        try {
            while (true) {
                if (this.permission.get()) {
                    request = this.cloud.getRequest(this.connection.getClientId());
                    if (request != null) {
                        this.connection.getOut().write(request);
                        this.connection.getOut().flush();
                        this.permission.set(false);
                        Thread.currentThread().join();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
