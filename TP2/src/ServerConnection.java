import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection implements IConnection{
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int clientId;

    public ServerConnection(String serverAddress,int port, int clientId) throws IOException {
        this.socket = new Socket(InetAddress.getByName(serverAddress), port);
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
        this.clientId = clientId;
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
        return null;
    }

    @Override
    public OutputStream getOut() {
        return null;
    }



    public int getClientId() {
        return clientId;
    }
}
