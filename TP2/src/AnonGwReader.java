import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class AnonGwReader implements Runnable{
    private AnonGwCloud cloud;
    private InputStream in;
    private String address;

    public AnonGwReader(AnonGwCloud cloud, Socket socket) throws IOException{
        this.cloud = cloud;
        this.in = socket.getInputStream();
        this.address = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        byte[] reply = new byte[1024];
        try{
            while (in.read(reply) != -1 ) {
                cloud.inserirFicheiro(this.address,reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
