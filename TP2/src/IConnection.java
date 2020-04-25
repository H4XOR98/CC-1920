import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IConnection {
    void open() throws IOException;
    InputStream getIn();
    OutputStream getOut();
    void close() throws IOException;
}
