import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class AggregationServer {
    public static boolean work = true;
    public static void main(String[] args) {
        int port = args.length >= 1? Integer.parseInt(args[0]) : 4567;
        LamportClock serverClock = new LamportClock(0);
        start(port, serverClock);
    }

    //use productor-consumer mode to keep different request can not run simultaneously
    public static void start(int port, LamportClock serverClock){
        BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();

        Consumer consumer = new Consumer(queue,serverClock);
        Producer producer = new Producer(queue,port,serverClock);
        new Thread((Runnable) producer,"Producer").start();
        new Thread((Runnable) consumer,"Consumer").start();
    }
}