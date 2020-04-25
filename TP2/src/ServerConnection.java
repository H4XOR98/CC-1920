import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int clientId;

    public ServerConnection(String serverAddress, int port, int clientId) throws IOException {
        this.socket = new Socket(InetAddress.getByName(serverAddress), port);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public BufferedReader getIn() {
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
