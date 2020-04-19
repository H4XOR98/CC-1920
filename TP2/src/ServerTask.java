import javafx.util.Pair;
import java.io.IOException;
import java.net.Socket;

public class ServerTask implements Runnable{

    // Variáveis de Instância

    private AnonGWCloud cloud;
    private Socket socket;

    // Construtor

    public ServerTask (AnonGWCloud cloud, Socket socket){
        this.cloud = cloud;
        this.socket = socket;
    }


    @Override
    public void run (){
        Pair<Integer,String> request;
        while (true){
            request = this.cloud.getRequest();
            if (request != null){
                try {
                    new Thread(new ServerTaskRunner(this.cloud, this.socket, request.getKey(), request.getValue())).start();
                }
                catch (IOException e){ e.printStackTrace(); }
            }
        }
    }
}
