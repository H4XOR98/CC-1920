import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class AnonGwWriter implements Runnable{
    private AnonGWCloud cloud;
    private OutputStream out;
    private String clientAddress;

    public AnonGwWriter(AnonGWCloud cloud, Socket socket) throws IOException {
        this.cloud = cloud;
        this.out = socket.getOutputStream();
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }


    @Override
    public void run() {
        while (true) {
            byte[] file = this.cloud.getReply(this.clientAddress);
            if(file != null){
                try {
                    this.out.write(file);
                    this.out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}