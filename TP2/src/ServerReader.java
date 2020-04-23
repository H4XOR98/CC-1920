import java.io.IOException;
import java.util.Arrays;

public class ServerReader implements Runnable {
    private AnonGWCloud cloud;
    private ServerConnection connection;

    public ServerReader(AnonGWCloud cloud, ServerConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        int numBytes;
        byte[] buffer = new byte[4096];
        try {
            while ((numBytes = this.connection.getIn().read(buffer)) != -1);
            this.cloud.insertReply(this.connection.getClientId(), Arrays.copyOfRange(buffer, 0, numBytes));
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}