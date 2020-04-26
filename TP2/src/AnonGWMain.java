import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class AnonGWMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 6) {
            throw new IllegalArgumentException("insuficient arguments");
        }

        String serverAddress = args[1];
        int port = Integer.parseInt(args[3]);

        List<String> peers;
        peers = new ArrayList<>();
        for(int i = 6 ; i < args.length ; i++){
            peers.add(args[i]);
        }

        ServerSocket anonGWSeverSocket = new ServerSocket(port);
        AnonGWCloud cloud = new AnonGWCloud();

        while (true) {
            ClientConnection clientConnection = new ClientConnection(anonGWSeverSocket.accept());
            WriterPermission writerPermission = new WriterPermission();
            int result = cloud.insertClient(clientConnection.getClientAddress(), writerPermission);
            new Thread(new ClientReader(cloud, clientConnection)).start();
            new Thread(new ClientWriter(cloud, clientConnection, writerPermission.getClientWriterPermission())).start();
            System.out.println("Cliente Aberto com id " + result);
            if(result != -1){
                ServerConnection serverConnection = new ServerConnection(serverAddress, port, result);
                new Thread(new ServerReader(cloud, serverConnection)).start();
                new Thread(new ServerWriter(cloud, serverConnection, writerPermission.getServerWriterPermission())).start();
                System.out.println("Socket Servidor Aberto para o cliente com id" + serverConnection.getClientId());
            }
        }
    }
}
