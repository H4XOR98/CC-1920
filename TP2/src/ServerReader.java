import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerReader implements Runnable {

    private AnonGWServerCloud cloud;
    private TCPConnection connection;
    private AtomicBoolean permission;
    private int sessionId;

    public ServerReader(AnonGWServerCloud cloud, TCPConnection connection, AtomicBoolean permission, int sessionId) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        byte[] result = new byte[Constants.MaxSizeBuffer];
        byte[] reply;
        int numBytes;
        try {
            while(true) {
                if(this.permission.get()) {
                    while ((numBytes = this.connection.getIn().read(result)) != -1) {
                        reply = new byte[numBytes];
                        System.arraycopy(result, 0, reply, 0, numBytes);
                        this.cloud.insertReply(sessionId, reply);
                    }
                    if(numBytes == -1){
                        break;
                    }
                }
            }
            this.cloud.readComplete(sessionId);
	        this.connection.close();
            Thread.currentThread().join();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}