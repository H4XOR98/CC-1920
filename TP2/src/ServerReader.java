import java.io.IOException;

public class ServerReader implements Runnable {

    private AnonGWServerCloud cloud;
    private TCPConnection connection;
    private int sessionId;

    public ServerReader(AnonGWServerCloud cloud, TCPConnection connection, int sessionId) {
        this.cloud = cloud;
        this.connection = connection;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        byte[] result = new byte[Constants.MaxSizeBuffer];
        byte[] reply;
        int numBytes;
        try {
            while ((numBytes = this.connection.getIn().read(result)) != -1) {
                reply = new byte[numBytes];
                System.arraycopy(result,0,reply,0,numBytes);
                this.cloud.insertReply(sessionId, reply);
		System.out.println(new String(reply));
            }
            this.cloud.readComplete(sessionId);
	    this.connection.closeIn();
	    this.connection.closeOut();
	    this.connection.closeSocket();
            Thread.currentThread().join();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}