import java.io.IOException;

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
            while (permission.get()) {
                request = this.cloud.getRequest(this.connection.getClientId());
                if (request != null) {
                    this.connection.getOut().write(request);
                    this.connection.getOut().flush();
                    this.permission.set(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
