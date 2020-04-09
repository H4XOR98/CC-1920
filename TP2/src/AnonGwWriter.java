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
        try  {
            while (true) {
                byte[] conteudo = this.cloud.getConteudo(this.address);
                int tamanho = this.cloud.getTamanho(this.address);
                if(conteudo != null && tamanho > 0){
                    this.out.write(conteudo,0,tamanho);
                    this.out.flush();
                    System.out.println("Sucesso na escrita");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
