import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
        int numBytes = -1;
        byte[] buffer = new byte[1024];
        try{
            out.write(request.getBytes());
            out.flush();
            System.out.println("Enviado ao Servidor");
            while (numBytes == -1){
                numBytes = in.read(buffer);
            }
            this.cloud.insertReply(this.idRequest, Arrays.copyOfRange(buffer,0,numBytes));
            System.out.println("Lido com sucesso");
        }
        catch (IOException e){ e.printStackTrace(); }
    }
}