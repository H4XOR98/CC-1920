import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Reply {
    private Queue<byte[]> replies;
    private boolean complete;

    public Reply() {
        this.replies = new LinkedList<>();
        this.complete = false;
    }

    public synchronized byte[] getReply() {
        return this.replies.poll();
    }

    public synchronized void addReply(byte[] reply){
        if(reply != null){
            this.replies.add(reply);
        }
    }

    public synchronized boolean isComplete() {
        return complete;
    }

    public synchronized void complete() {
        this.complete = true;
    }

    public synchronized int size(){
        return this.replies.size();
    }
}
