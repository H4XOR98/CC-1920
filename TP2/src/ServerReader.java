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
        byte[] reply;
        String line;
        try {
            while ((line = this.connection.getIn().readLine()) != null){
                reply = line.getBytes();
                if(reply.length > 0){
                    this.cloud.insertReply(this.connection.getClientId(), reply);
                    this.connection.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}