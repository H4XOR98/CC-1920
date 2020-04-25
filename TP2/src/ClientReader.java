import java.io.IOException;

public class ClientReader implements Runnable {
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientReader(AnonGWCloud cloud, ClientConnection connection) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] request = new byte[1024];
        try {
            while (this.connection.getIn().read(request) != -1) {
                this.cloud.insertRequest(this.connection.getClientAddress(),request);
                System.out.println(new String(request));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
