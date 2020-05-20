import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ServerSender implements Runnable{
    private AnonGWServerCloud cloud;
    private UDPConnection connection;
    private int sessionId;
    private InetAddress overlayPeer;

    public ServerSender(AnonGWServerCloud cloud, UDPConnection connection, int sessionId, InetAddress overlayPeer) {
        this.cloud = cloud;
        this.connection = connection;
        this.sessionId = sessionId;
        this.overlayPeer = overlayPeer;
    }

    @Override
    public void run() {
        Packet packet;
        while (true){
            packet = this.cloud.getReplyPacket(sessionId);
            if(packet != null) {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(packet.toBytes(), packet.toBytes().length, overlayPeer, Constants.UDPPort);
                    this.connection.getDatagramSocket().send(datagramPacket);
                    //System.out.println("[client " + packet.getId() + "] reply sent through UDP");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(packet.isLast()){
                    System.out.println("[client " + packet.getId() + "] all replies sent through UDP");
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