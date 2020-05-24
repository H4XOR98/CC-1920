import java.io.*;

public class Packet implements Serializable {
    private int id;
    private boolean last;
    private int sequenceNum;
    private int destination;
    private byte[] data;

    public Packet (int id, int sequenceNum, boolean last, int destination, byte[] data) {
        this.id = id;
        this.last = last;
        this.sequenceNum = sequenceNum;
        this.destination = destination;
        this.data = data;
    }

    public Packet (byte[] incommingData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(incommingData);
        ObjectInputStream ois = new ObjectInputStream(in);
        Packet pack = (Packet) ois.readObject();
        this.id = pack.getId();
        this.last = pack.isLast();
        this.sequenceNum = pack.getSequenceNum();
        this.destination = pack.getDestination();
        this.data = pack.getData();
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }

    /*
        Getters
     */

    public int getId() {
        return this.id;
    }

    public boolean isLast() {
        return this.last;
    }

    public int getSequenceNum() {
        return this.sequenceNum;
    }

    public int getDestination() {
        return this.destination;
    }

    public byte[] getData() {
        return this.data;
    }

    /*
        Setters
     */

    public void setId(int id) {
        this.id = id;
    }

    public void setData (byte[] data){
        this.data = data;
    }
}
