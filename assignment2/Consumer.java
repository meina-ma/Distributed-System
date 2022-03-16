import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class Consumer implements Runnable{
    private BlockingQueue<Socket> queue;
    private LamportClock serverClock;

    //constructor
    //use productor-consumer mode to keep different request can not run simultaneously
    
    public Consumer(BlockingQueue<Socket> queue, LamportClock serverClock){
        this.queue = queue;
        this.serverClock = serverClock;
    }

    //when the queue is not empty, server will take one socket from the queue and read the request
    @Override
    public void run(){
        try{
            Socket socket = null;
            while(true){
                socket = queue.take();               
                if(AggregationServer.work){                    
                    ReplyRequest obj = new ReplyRequest(socket,serverClock);
                    obj.read_request();
                }
            }
        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}
