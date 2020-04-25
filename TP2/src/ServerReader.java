import java.io.IOException;

public class ServerReader implements Runnable {
    private AnonGWCloud cloud;
    private ServerConnection connection;

    public ServerReader(AnonGWCloud cloud, ServerConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }


    @Override
    public void run() {
        byte[] reply = new byte[Constants.MaxSizeBuffer];
        try {
            while (this.connection.getIn().read(reply) != -1) {
                this.cloud.insertReply(this.connection.getClientId(),reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
