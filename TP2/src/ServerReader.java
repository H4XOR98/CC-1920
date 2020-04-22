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
        int numBytes = 0;
        byte[] buffer = new byte[4096];
        try {
            while (true) {
                numBytes = this.connection.getIn().read(buffer);
                if (numBytes > 0) {
                    byte[] reply = new byte[numBytes];
                    for (int i = 0; i < numBytes; i++){
                        reply[i] = buffer[i];
                    }
                    this.cloud.insertReply(this.connection.getClientId(), reply);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}