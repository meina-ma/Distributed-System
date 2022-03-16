public class MsgHandler {
    public String massage;
    public LamportClock lamportClock;

    public MsgHandler(){

    }


    //convert the file to the format of PUT request
    public String PUTrequest(ContentServer contentServer,Feed obj){
        String msg = "PUT/atom.xml HTTP/1.1\n" + "User-Agent: ContentServer/1/0\n";
        msg = msg+"Content-Type: XML feed\n"+"Content-Length: "+obj.content_length+"\n\n"+obj.content;
        return msg;
    }

    //convert the file to the format of status 400
    public String PUT400(ContentServer contentServer,Feed obj){
        String msg = "POST/atom.xml HTTP/1.1\n" + "User-Agent: ContentServer/1/0\n";
        msg = msg+"Content-Type: XML feed\n"+"Content-Length: "+obj.content_length+"\n\n"+obj.content;
        return msg;
    }

    //return the format of PUT request
    public String GETrequest(){
        String msg = "GET/atom.xml HTTP/1.1\n" + "User-Agent: GETClient/1/0\n";
        return msg;
    }


}
