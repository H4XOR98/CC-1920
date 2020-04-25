import java.util.concurrent.atomic.AtomicBoolean;

public class WritePermission {
    private AtomicBoolean clientWritePermission;
    private AtomicBoolean serverWritePermission;

    public WritePermission(){
        this.clientWritePermission = new AtomicBoolean(false);
        this.serverWritePermission = new AtomicBoolean(false);
    }

    public AtomicBoolean getClientWritePermission() {
        return clientWritePermission;
    }

    public void setClientWritePermission(AtomicBoolean clientWritePermission) {
        this.clientWritePermission = clientWritePermission;
    }

    public AtomicBoolean getServerWritePermission() {
        return serverWritePermission;
    }

    public void setServerWritePermission(AtomicBoolean serverWritePermission) {
        this.serverWritePermission = serverWritePermission;
    }
}
