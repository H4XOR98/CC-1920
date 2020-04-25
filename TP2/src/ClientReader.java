import java.io.IOException;

public class ClientReader implements Runnable {
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientReader(AnonGWCloud cloud, ClientConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] request = new byte[Constants.MaxBytesBuffer];
        try {
            while (this.connection.getIn().read(request) != -1) {
                this.cloud.insertRequest(this.connection.getClientAddress(),request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
