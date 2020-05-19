import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPConnection{
    private DatagramSocket datagramSocket;

    public UDPConnection() throws SocketException {
        this.datagramSocket = new DatagramSocket(Constants.UDPPort);
    }

    public DatagramSocket getDatagramSocket() {
        return this.datagramSocket;
    }
}
