import java.io.IOException;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] reply;
        try{
            while(true){
                reply = this.cloud.getReply(this.connection.getClientAddress());
                if(reply != null){
                    this.connection.getOut().write(reply);
                    this.connection.getOut().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
