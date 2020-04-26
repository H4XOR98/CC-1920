import java.util.concurrent.atomic.AtomicBoolean;

public class WriterPermission {
    private AtomicBoolean clientWriterPermission;
    private AtomicBoolean serverWriterPermission;

    public WriterPermission(){
        this.clientWriterPermission = new AtomicBoolean(false);
        this.serverWriterPermission = new AtomicBoolean(false);
    }

    public AtomicBoolean getClientWriterPermission() {
        return this.clientWriterPermission;
    }

    public AtomicBoolean getServerWriterPermission() {
        return this.serverWriterPermission;
    }
}
