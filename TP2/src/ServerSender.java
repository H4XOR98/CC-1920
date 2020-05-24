import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerSender implements Runnable{
    private AnonGWServerCloud cloud;
    private UDPConnection connection;
    private AtomicBoolean permission;
    private int sessionId;
    private InetAddress overlayPeer;

    public ServerSender(AnonGWServerCloud cloud, UDPConnection connection, AtomicBoolean permission, int sessionId, InetAddress overlayPeer) {
        this.cloud = cloud;
        this.connection = connection;
        this.permission = permission;
        this.sessionId = sessionId;
        this.overlayPeer = overlayPeer;
    }

    @Override
    public void run() {
        Packet packet;
        Encryptor encryptor = new Encryptor();
        byte[] encryptedData;
        while (true){
            if(this.permission.get()) {
                packet = this.cloud.getReplyPacket(sessionId);
                if (packet != null) {
                    try {
                        encryptedData = encryptor.encryptMessage(packet.getData());
                        packet.setData(encryptedData);
                        DatagramPacket datagramPacket = new DatagramPacket(packet.toBytes(), packet.toBytes().length, overlayPeer, Constants.UDPPort);
                        this.connection.getDatagramSocket().send(datagramPacket);
                        //System.out.println("[client " + packet.getId() + "] reply sent through UDP");
                    } catch (IOException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException
                    e) {
                        e.printStackTrace();
                    }
                    if (packet.isLast()) {
                        System.out.println("[client " + packet.getId() + "] all replies sent through UDP");
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