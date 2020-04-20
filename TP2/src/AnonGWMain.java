import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AnonGWMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 6) {
            throw new IllegalArgumentException("insuficient arguments");
        }

        String serverIP = args[1];
        int port = Integer.parseInt(args[3]);

        List<String> peers;
        peers = new ArrayList<>();
        for(int i = 6 ; i < args.length ; i++){
            peers.add(args[i]);
        }

        InetAddress inetAddress = InetAddress.getLocalHost();
        String localAnonGWIP = inetAddress.getHostAddress();

        AnonGWCloud cloud = new AnonGWCloud(serverIP, localAnonGWIP);

        ServerSocket s = new ServerSocket(port);
        System.out.println("Socket server open");

        InetAddress localAddress = InetAddress.getByName(serverIP);
        Socket socketServer = new Socket(localAddress, port);
        new Thread(new ServerTask(cloud,socketServer)).start();

        while (true) {
            Socket socketClient = s.accept();
            System.out.println("Socket cliente open");
            Thread readerClient = new Thread(new AnonGwReader(cloud, socketClient));
            Thread writerClient = new Thread(new AnonGwWriter(cloud, socketClient));
            readerClient.start();
            writerClient.start();
        }
    }
}