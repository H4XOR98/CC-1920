import java.util.concurrent.atomic.AtomicBoolean;

public class ServerCloudPermissions {
    private AtomicBoolean serverSenderPermission;
    private AtomicBoolean serverWriterPermission;
    private AtomicBoolean serverReaderPermission;


    public ServerCloudPermissions() {
        this.serverSenderPermission = new AtomicBoolean(false);
        this.serverWriterPermission = new AtomicBoolean(false);
        this.serverReaderPermission = new AtomicBoolean(false);
    }

    public synchronized AtomicBoolean getServerSenderPermission() {
        return this.serverSenderPermission;
    }

    public synchronized AtomicBoolean getServerWriterPermission() {
        return this.serverWriterPermission;
    }

    public synchronized AtomicBoolean getServerReaderPermission() {
        return this.serverReaderPermission;
    }


    public synchronized void aproveServerSenderPermission() {
        this.serverSenderPermission.set(true);
    }

    public synchronized void aproveServerWriterPermission() {
        this.serverWriterPermission.set(true);
    }

    public synchronized void aproveServerReaderPermission() {
        this.serverReaderPermission.set(true);
    }
}
