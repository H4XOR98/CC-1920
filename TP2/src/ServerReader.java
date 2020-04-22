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
        int numBytes = -1;
        byte[] buffer = new byte[1024];
        try {
            while (numBytes == -1) {
                numBytes = this.connection.getIn().read(buffer);
            }
            this.cloud.insertReply(this.connection.getClientId(), Arrays.copyOfRange(buffer,0,numBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}