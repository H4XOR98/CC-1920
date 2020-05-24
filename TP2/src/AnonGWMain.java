import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AnonGWMain {
    public static void main(String[] args) throws IOException , IllegalArgumentException{
        
        if (args.length < 5) {
            throw new IllegalArgumentException("insuficient arguments");
        }

        String targetServerAddress = args[1];
        if(!targetServerAddress.matches(Constants.IPV4Pattern) || targetServerAddress == null){
            throw new IllegalArgumentException("target server format invalid");
        }


        List<InetAddress> overlayPeers = new ArrayList<>();
        for(int i = 3 ; i < args.length ; i++){
            if(args[i].matches(Constants.IPV4Pattern) && args[i] != null) {
                overlayPeers.add(InetAddress.getByName(args[i]));
            }
        }

        if (overlayPeers.size() < Constants.MinOverlayPeers){
            throw new IllegalArgumentException("not enough overlay-peers");
        }

        UDPConnection udpConnection = new UDPConnection();
        AnonGWClientCloud clientCloud = new AnonGWClientCloud(udpConnection, overlayPeers);
        AnonGWServerCloud serverCloud = new AnonGWServerCloud(InetAddress.getByName(targetServerAddress), udpConnection);

        ServerSocket anonGWSeverSocket = new ServerSocket(Constants.TCPPort);

        new Thread(new Receiver(clientCloud,serverCloud,udpConnection)).start();

        while (true) {
            Socket socket = anonGWSeverSocket.accept();
            clientCloud.insertClient(socket);
        }
    }
}