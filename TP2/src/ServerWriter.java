import java.io.IOException;

public class ServerWriter implements Runnable {

    private AnonGWServerCloud cloud;
    private TCPConnection connection;
    private int sessionId;

    public ServerWriter(AnonGWServerCloud cloud, TCPConnection connection, int sessionId) {
        this.cloud = cloud;
        this.connection = connection;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        Packet request;
        try {
            while (true) {
                request = this.cloud.getRequestPacket(this.sessionId);
                if (request != null) {
                    if(request.isLast()){
                        break;
                    }
                    this.connection.getOut().write(request.getData());
                    this.connection.getOut().flush();
                }
            }
            
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}