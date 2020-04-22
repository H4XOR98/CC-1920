import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private OutputStream out;
    private String clientAddress;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = socket.getOutputStream();
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public BufferedReader getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
