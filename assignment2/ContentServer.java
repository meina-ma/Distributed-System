import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ContentServer {
    private static Socket client;
    public Writer writer;
    public String user;
    public static LamportClock lamportClock;
    public static int retryTime = 1200;
    public static boolean sendSuccess = false;
    public static String MSG = "GOOD";

    public ContentServer(String host,int port,String fileName,LamportClock lamportClock) throws Exception{
        this.lamportClock = lamportClock;
        this.client = new Socket(host,port);
        this.user = fileName;
        System.out.println("Content Server [port "+this.client.getLocalPort()+"] connect to aggregation server");
    }


    //content server send a put request and send the Atom xml file to server
    public static void PUTrequest(String url,String location,LamportClock content_clock) throws Exception{
        //use a thread to send PUT request and receive the reply from aggregation server
        int i = 0;
        String temp[] = url.split(":");
        String fileName = location;
        
        while (i < 3&&!sendSuccess) {
            System.out.println((i + 1) + "times try");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ContentServer contentServer = new ContentServer(temp[1].substring(2,temp[1].length()), Integer.valueOf(temp[2]), fileName,content_clock);
                        Feed obj = new Feed(fileName);
                        MsgHandler msgHandler = new MsgHandler();
                        String msg = msgHandler.PUTrequest(contentServer,obj);
                        contentServer.send(msg);
                        contentServer.receive();
                        sendSuccess = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(retryTime);
            if (sendSuccess) {
                retryTime = 1200;
                break;
            } else {
                i++;
                retryTime = 2*retryTime;
            }
        }
    }
    
    //function: send "PUT" request to Aggregation server
    //input: a String represents the input file
    public void send(String msg) throws IOException {
        if(writer == null){
            writer = new OutputStreamWriter(this.client.getOutputStream());
        }
        msg = ("LamportClock: "+this.lamportClock.getClock()+"\n")+msg;
        writer.write(msg);
        writer.write("eof\n");
        writer.flush();

        System.out.println("Content Server "+this.client.getLocalPort()+" PUT request send successfully");
        this.lamportClock.increseClock();
        System.out.println("After Send a PUT request, Content Server's Lamport become "+this.lamportClock.getClock());

    }


    //function: receive the response from Aggregation server
    //The first time your ATOM feed is created, you should return status 201 - HTTP_CREATED
    // If later uploads are ok, you should return status 200
    public void receive(){
        try{

            Reader reader = new InputStreamReader(client.getInputStream());
            client.setSoTimeout(1000*20);
            char[] chars = new char[1024];
            int len = 0;
            StringBuilder sb = new StringBuilder();
            ArrayList<String> sb1 = new ArrayList<>();
            while((len= reader.read(chars)) != -1){
                sb1.add(new String(chars,0,len)+"\n");
            }
            String msg = sb1.toString();

            
            this.lamportClock.setClock(Math.max(getLamport(sb1), this.lamportClock.getClock()));
            this.lamportClock.increseClock();
            System.out.println("Content Server receives the reply from aggregation server");
            System.out.println("===============================================");
            System.out.println("Content Server Receive msg:");
            System.out.println(msg);
            System.out.println("===============================================");
            System.out.println("After receive a reply, Content Server's Lamport become "+this.lamportClock.getClock());

            reader.close();
            writer.close();
            client.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getLamport(ArrayList<String> sb1){
    	int start = sb1.get(0).indexOf("LamportClock: ") + "LamportClock:".length();
        String lamport = "";
        for (int i = start+1; i < sb1.get(0).length(); i++) {
            if (sb1.get(0).charAt(i) != '\n') {
                lamport += sb1.get(0).charAt(i);
            } else break;
        }
        return Integer.valueOf(lamport);

    }

    //simulate the status 400, send 'POST' request
    public static void PUT400(String url,String location,LamportClock content_clock) throws Exception{
        //use a thread to send PUT request and receive the reply from aggregation server
        int i = 0;
        String temp[] = url.split(":");
        String fileName = location;
        
        while (i < 3&&!sendSuccess) {
            System.out.println((i + 1) + "times try");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ContentServer contentServer = new ContentServer(temp[1].substring(2,temp[1].length()), Integer.valueOf(temp[2]), fileName,content_clock);
                        Feed obj = new Feed(fileName);
                        MsgHandler msgHandler = new MsgHandler();
                        String msg = msgHandler.PUT400(contentServer,obj);
                        contentServer.send(msg);
                        contentServer.receive();
                        sendSuccess = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(retryTime);
            if (sendSuccess) {
                retryTime = 1200;
                break;
            } else {
                i++;
                retryTime = 2*retryTime;
            }

        }

    }

    
    

}
