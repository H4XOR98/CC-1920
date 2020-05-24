import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSender implements Runnable{
    private AnonGWClientCloud cloud;
    private UDPConnection connection;
    private AtomicBoolean permission;
    private int clientId;
    private InetAddress overlayPeer;

    public ClientSender(AnonGWClientCloud cloud, UDPConnection connection, AtomicBoolean permission, int clientId, InetAddress overlayPeer) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
        this.clientId = clientId;
        this.overlayPeer = overlayPeer;
    }

    @Override
    public void run() {
        Packet packet;
        Encryptor encryptor = new Encryptor();
        byte[] encryptedData;
        while (true){
            if(this.permission.get()) {
                packet = this.cloud.getRequestPacket(clientId);
                if (packet != null) {
                    try {
                        encryptedData = encryptor.encryptMessage(packet.getData());
                        packet.setData(encryptedData);
                        DatagramPacket datagramPacket = new DatagramPacket(packet.toBytes(), packet.toBytes().length, overlayPeer, Constants.UDPPort);
                        this.connection.getDatagramSocket().send(datagramPacket);
                        //System.out.println("[client " + clientId + "] request sent through UDP");
                    } catch (IOException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                    if (packet.isLast()) {
                        System.out.println("[client " + clientId + "] all requests sent through UDP");
                        break;
                    }
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
