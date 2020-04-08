import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class AnonGwWriter implements Runnable{
    private AnonGwCloud cloud;
    private OutputStream out;
    private String address;

    public AnonGwWriter(AnonGwCloud cloud, Socket socket) throws IOException {
        this.cloud = cloud;
        this.out = socket.getOutputStream();
        this.address = socket.getInetAddress().getHostAddress();
    }


    @Override
    public void run() {
        try {
            while (true) {
                byte[] ficheiro = this.cloud.getFicheiro(address);
                this.out.write(ficheiro);
                this.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
