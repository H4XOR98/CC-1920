import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPConnection {
    private Socket socket;
    private boolean inClose;
    private InputStream in;
    private boolean outClose;
    private OutputStream out;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.inClose = false;
        this.in = socket.getInputStream();
        this.outClose = false;
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

    public void closeIn() throws IOException {
        if(!this.inClose && !this.socket.isClosed()){
            this.inClose = true;
            this.in.close();
        }
    }

    public void closeOut() throws IOException {
        if(!this.outClose && !this.socket.isClosed()){
            this.outClose = true;
            this.out.close();
        }
    }


    public void closeSocket() throws IOException {
        if(this.inClose && this.outClose && !this.socket.isClosed()){
            this.socket.close();
        }
    }

}
