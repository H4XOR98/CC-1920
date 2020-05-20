import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientWriter implements Runnable{
    private AnonGWClientCloud cloud;
    private TCPConnection connection;
    private AtomicBoolean permission;

    public ClientWriter (AnonGWClientCloud cloud, TCPConnection connection, AtomicBoolean permission) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
    }

    @Override
    public void run() {
        String clientAddress = this.connection.getIPAddress();
        Packet reply;
        try {
            while(true) {
                if (this.permission.get()) {
                    reply = this.cloud.getReplyPacket(clientAddress);
                    if (reply != null) {
                        if (reply.isLast()) {
                            break;
                        }
                        this.connection.getOut().write(reply.getData());
                        this.connection.getOut().flush();
                    }
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