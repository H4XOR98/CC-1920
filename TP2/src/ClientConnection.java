import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String clientAddress;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void close() throws IOException {
        if(!this.socket.isClosed()){
            this.out.close();
            this.in.close();
            this.socket.close();
        }
    }
}
