import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

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
        int numBytes;
        try{
            while ((numBytes = in.read(reply)) != -1 ) {
                cloud.inserirConteudo(this.address,reply,numBytes);
                System.out.println("Lido com sucesso");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
