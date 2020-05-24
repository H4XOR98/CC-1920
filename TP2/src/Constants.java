import java.net.InetAddress;

public class Constants {
    //Sizes
    public static final int MaxSizeBuffer = 1024;
    public static final int MaxSizePacket = 512 + MaxSizeBuffer;

    //Packet Destinations
    public static final int ToClient = 0;
    public static final int ToServer = 1;

    //Ports
    public static final int TCPPort = 80;
    public static final int UDPPort = 6666;

    //IPV4 Pattern
    public static final String IPV4Pattern = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$";

    //Encryption
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final byte[] Key = "1234567890123456".getBytes();

    //Overlay-Peers
    public static final int MinOverlayPeers = 1;
}
