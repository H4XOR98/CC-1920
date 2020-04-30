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
        byte[] result;
        int numBytes;
        try {
            while ((numBytes = this.connection.getIn().read(reply)) > 0) {
                result = new byte[numBytes];
                System.arraycopy(reply,0,result,0,numBytes);
                this.cloud.insertReply(this.connection.getClientId(),result);
            }
            this.cloud.serverReadComplete(this.connection.getClientId());
            this.connection.close();
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
