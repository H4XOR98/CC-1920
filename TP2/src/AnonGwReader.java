import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AnonGwReader implements Runnable{
    private AnonGWCloud cloud;
    private BufferedReader in;
    private String clientAddress;

    public AnonGwReader(AnonGWCloud cloud, Socket socket) throws IOException{
        this.cloud = cloud;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        String request = null;
        try{
            while ((request = in.readLine()) != null) {
                this.cloud.insertRequest(this.clientAddress,request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}