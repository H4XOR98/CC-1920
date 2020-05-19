import java.io.IOException;

public class ClientWriter implements Runnable{
    private AnonGWClientCloud cloud;
    private TCPConnection connection;

    public ClientWriter (AnonGWClientCloud cloud, TCPConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        String clientAddress = this.connection.getIPAddress();
        Packet reply;
        try {
            while(true) {
                reply = this.cloud.getReplyPacket(clientAddress);
                if (reply != null){
                    if (reply.isLast()){
                        break;
                    }
                    this.connection.getOut().write(reply.getData());
                    this.connection.getOut().flush();
                }
            }
            this.connection.close();
            Thread.currentThread().join();
        }
        catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
}