import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader implements Runnable {
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientReader(AnonGWCloud cloud, ClientConnection connection) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        String request = null;
        try {
            while (request == null) {
                request = this.connection.getIn().readLine();
            }
            this.cloud.insertRequest(this.connection.getClientAddress(),request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}