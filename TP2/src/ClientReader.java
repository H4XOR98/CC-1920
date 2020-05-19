import java.io.IOException;

public class ClientReader implements Runnable{
    private AnonGWClientCloud cloud;
    private TCPConnection connection;
    private int clientId;

    public ClientReader(AnonGWClientCloud cloud, TCPConnection connection, int clientId) {
        this.cloud = cloud;
        this.connection = connection;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        byte[] request = new byte[Constants.MaxSizeBuffer];
        byte[] result;
        int numBytes;
        try {
        while ((numBytes = this.connection.getIn().read(request)) != -1) {
                result = new byte[numBytes];
                System.arraycopy(request,0,result,0,numBytes);
                this.cloud.insertRequest(clientId,result);
                if(this.connection.getIn().available() == 0) break;
            }
            this.cloud.readComplete(clientId);
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
