import java.util.LinkedList;
import java.util.Queue;

public class Packages {
    private Queue<byte[]> packages;
    private boolean complete;

    public Packages() {
        this.packages = new LinkedList<>();
        this.complete = false;
    }

    public synchronized byte[] getPackage() {
        return this.packages.poll();
    }

    public synchronized void removePackage(){
        this.packages.remove();
    }

    public synchronized void addPackage(byte[] reply){
        if(reply != null){
            this.packages.add(reply);
        }
    }

    public synchronized boolean isComplete() {
        return complete;
    }

    public synchronized void complete() {
        this.complete = true;
    }

    public synchronized int size(){
        return this.packages.size();
    }
}
