import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private InputStream in;
    private PrintWriter out;
    private int clientId;

    public ServerConnection(String serverAddress, int port, int clientId) throws IOException {
        this.socket = new Socket(InetAddress.getByName(serverAddress), port);
        this.in = this.socket.getInputStream();
        this.out = new PrintWriter(socket.getOutputStream());
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public InputStream getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
