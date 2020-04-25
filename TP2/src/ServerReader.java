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
        byte[] reply = new byte[Constants.MaxBytesBuffer];
        try {
            while (this.connection.getIn().read(reply) != -1) {
                this.cloud.insertReply(this.connection.getClientId(),reply);
                this.connection.close();
                Thread.currentThread().join();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
