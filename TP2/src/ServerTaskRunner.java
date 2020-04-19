import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerTaskRunner implements Runnable {

    // Variáveis de Instância

    private AnonGWCloud cloud;
    private int idRequest;
    private String request;
    private InputStream in;
    private OutputStream out;

    // Construtor

    public ServerTaskRunner (AnonGWCloud cloud, Socket socket, int idRequest, String request) throws IOException {
        this.cloud = cloud;
        this.idRequest = idRequest;
        this.request = request;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }


    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        int numBytes;
        try{
            out.write(request.getBytes());
            out.flush();
            System.out.println("Enviado ao Servidor");
            while ((numBytes = in.read(buffer)) != -1) {
                byte[] reply = new byte[numBytes];
                for (int i = 0; i < numBytes; i++){
                    reply[i] = buffer[i];
                }
                this.cloud.insertReply(this.idRequest, reply);
                System.out.println("Lido com sucesso");
            }
        }
        catch (IOException e){ e.printStackTrace(); }
    }
}
