import java.io.IOException;

public class ClientReader implements Runnable {
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientReader(AnonGWCloud cloud, ClientConnection connection) {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] request = new byte[Constants.MaxSizeBuffer];
        byte[] result;
        int numBytes;
        try {
            while ((numBytes = this.connection.getIn().read(request)) > 0) {
                result = new byte[numBytes];
                System.arraycopy(request,0,result,0,numBytes);
                this.cloud.insertRequest(this.connection.getClientAddress(),result);
            }
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
