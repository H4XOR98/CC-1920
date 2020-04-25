import java.io.IOException;

public class ServerWriter implements Runnable {
    private AnonGWCloud cloud;
    private ServerConnection connection;

    public ServerWriter(AnonGWCloud cloud, ServerConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] request;
        try {
            while (true) {
                request = this.cloud.getRequest(this.connection.getClientId());
                if (request != null) {
                    this.connection.getOut().write(request);
                    this.connection.getOut().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
