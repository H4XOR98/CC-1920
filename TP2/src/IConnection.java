import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IConnection {
    InputStream getIn();
    OutputStream getOut();
    void close() throws IOException;
}
