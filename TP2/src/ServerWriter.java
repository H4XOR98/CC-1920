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
        String result = null;
        try {
            while (result == null) {
                result = this.cloud.getRequest(this.connection.getClientId());
            }
            this.connection.getOut().write(result.getBytes());
            this.connection.getOut().flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}