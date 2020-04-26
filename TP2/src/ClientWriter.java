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
                    this.connection.close();
                    Thread.currentThread().join();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
