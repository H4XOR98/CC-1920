import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ClientSender implements Runnable{
    private AnonGWClientCloud cloud;
    private UDPConnection connection;
    private int clientId;
    private InetAddress overlayPeer;

    public ClientSender(AnonGWClientCloud cloud, UDPConnection connection, int clientId, InetAddress overlayPeer) {
        this.cloud = cloud;
        this.connection = connection;
        this.clientId = clientId;
        this.overlayPeer = overlayPeer;
    }

    @Override
    public void run() {
        Packet packet;
        while (true){
            packet = this.cloud.getRequestPacket(clientId);
            if(packet != null) {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(packet.toBytes(), packet.toBytes().length, overlayPeer, Constants.UDPPort);
                    this.connection.getDatagramSocket().send(datagramPacket);
                    //System.out.println("[client " + clientId + "] request sent through UDP");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(packet.isLast()){
                    System.out.println("[client " + clientId + "] all requests sent through UDP");
                    break;
                }
            }
        }
        try{
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
