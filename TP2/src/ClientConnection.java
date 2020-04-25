import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection implements IConnection{
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String clientAddress;

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void open() throws IOException {
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void close() throws IOException {
        if(!this.socket.isClosed()){
            this.in.close();
            this.out.close();
            this.socket.close();
        }
    }

    @Override
    public InputStream getIn() {
        return in;
}

    @Override
    public OutputStream getOut() {
        return this.out;
    }

    public String getClientAddress() {
        return clientAddress;
    }
}
