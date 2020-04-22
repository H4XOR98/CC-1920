import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWriter implements Runnable{
    private AnonGWCloud cloud;
    private ClientConnection connection;

    public ClientWriter(AnonGWCloud cloud, ClientConnection connection) throws IOException {
        this.cloud = cloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] result = null;
        try {
            while (true) {
                result = this.cloud.getReply(this.connection.getClientAddress());
                if(result != null){
                    this.connection.getOut().write(result);
                    this.connection.getOut().flush();
                    this.connection.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}