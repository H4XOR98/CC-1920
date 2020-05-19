import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnonGWMain {
    public static void main(String[] args) throws IOException {
        /*if (args.length < 6) {
            throw new IllegalArgumentException("insuficient arguments");
        }*/

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



        UDPConnection udpConnection = new UDPConnection();
        AnonGWClientCloud clientCloud = new AnonGWClientCloud();
        AnonGWServerCloud serverCloud = new AnonGWServerCloud(InetAddress.getByName(targetServerAddress), udpConnection);
        Random randomize = new Random();

        ServerSocket anonGWSeverSocket = new ServerSocket(Constants.TCPPort);

        new Thread(new Receiver(clientCloud,serverCloud,udpConnection)).start();

        int clientId;
        while (true) {
            TCPConnection tcpConnection = new TCPConnection(anonGWSeverSocket.accept());
            clientId = clientCloud.insertClient(tcpConnection.getIPAddress());
            if(clientId != -1) {
                new Thread(new ClientReader(clientCloud, tcpConnection, clientId)).start();
                new Thread(new ClientWriter(clientCloud,tcpConnection)).start();
                InetAddress overlayPeer = overlayPeers.get(randomize.nextInt(overlayPeers.size()));
                new Thread(new ClientSender(clientCloud,udpConnection,clientId,overlayPeer)).start();
            }
        }
    }
}