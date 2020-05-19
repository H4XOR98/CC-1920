import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Receiver implements Runnable{
    private AnonGWClientCloud clientCloud;
    private AnonGWServerCloud serverCloud;
    private UDPConnection connection;

    public Receiver(AnonGWClientCloud clientCloud, AnonGWServerCloud serverCloud, UDPConnection connection) {
        this.clientCloud = clientCloud;
        this.serverCloud = serverCloud;
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] incomingData = new byte[Constants.MaxSizePacket];

        while (true) {
            try {
                // get DatagramPacket
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                this.connection.getDatagramSocket().receive(incomingPacket);

                if(incomingPacket != null){
                    // get Packet
                    Packet packet = new Packet(incomingPacket.getData());

                    // get InetAddress
                    InetAddress address = incomingPacket.getAddress();

                    // Add Packet to AnonGWServerCloud
                    if (packet.getDestination() == Constants.ToServer) this.serverCloud.insertRequest(packet, address);
                    else System.out.println("Para o cliente" + packet.getDestination());

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
