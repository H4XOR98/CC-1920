import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        Encryptor encryptor = new Encryptor();
        byte[] decryptedData;

        while (true) {
            try {
                // get DatagramPacket
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                this.connection.getDatagramSocket().receive(incomingPacket);

                if(incomingPacket != null){
                    // get Packet
                    Packet packet = new Packet(incomingPacket.getData());
                    decryptedData = encryptor.decryptMessage(packet.getData());
                    packet.setData(decryptedData);

                    // get InetAddress
                    InetAddress address = incomingPacket.getAddress();
		    //System.out.println(address.toString());

                    // Add Packet to AnonGWServerCloud
                    if (packet.getDestination() == Constants.ToServer) this.serverCloud.insertRequest(packet, address);
                    else this.clientCloud.insertReply(packet);
                }
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        }
    }
}
