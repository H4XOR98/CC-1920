import java.util.concurrent.atomic.AtomicBoolean;

public class ClientCloudPermissions {
    private AtomicBoolean clientSenderPermission;
    private AtomicBoolean clientWriterPermission;


    public ClientCloudPermissions() {
        this.clientSenderPermission = new AtomicBoolean(false);
        this.clientWriterPermission = new AtomicBoolean(false);
    }


    public synchronized AtomicBoolean getClientSenderPermission() {
        return this.clientSenderPermission;
    }

    public synchronized AtomicBoolean getClientWriterPermission() {
        return this.clientWriterPermission;
    }

    public synchronized void aproveClientSenderPermssion() {
        this.clientSenderPermission.set(true);
    }

    public synchronized void aproveClientWriterPermission() {
        this.clientWriterPermission.set(true);
    }
}
