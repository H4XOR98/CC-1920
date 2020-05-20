import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerWriter implements Runnable {

    private AnonGWServerCloud cloud;
    private TCPConnection connection;
    private AtomicBoolean permission;
    private int sessionId;

    public ServerWriter(AnonGWServerCloud cloud, TCPConnection connection, AtomicBoolean permission, int sessionId) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        Packet request;
        try {
            while (true) {
                if(this.permission.get()) {
                    request = this.cloud.getRequestPacket(this.sessionId);
                    if (request != null) {
                        if (request.isLast()) {
                            break;
                        }
                        this.connection.getOut().write(request.getData());
                        this.connection.getOut().flush();
                    }
                }
            }
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}