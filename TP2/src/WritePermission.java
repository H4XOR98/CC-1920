import java.util.concurrent.atomic.AtomicBoolean;

public class WritePermission {
    private AtomicBoolean clientPermission;
    private AtomicBoolean serverPermission;

    public WritePermission() {
        this.clientPermission = new AtomicBoolean(false);
        this.serverPermission = new AtomicBoolean(false);
    }

    public AtomicBoolean getClientPermission() {
        return clientPermission;
    }

    public AtomicBoolean getServerPermission() {
        return serverPermission;
    }
}
