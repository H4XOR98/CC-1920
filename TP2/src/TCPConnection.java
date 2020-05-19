import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPConnection {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public String getIPAddress() {
        return this.socket.getInetAddress().getHostAddress();
    }

    public void close() throws IOException {
        if(!this.socket.isClosed()){
            this.in.close();
            this.out.close();
            this.socket.close();
        }
    }

}
