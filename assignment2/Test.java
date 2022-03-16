import java.io.*;
public class Test {
    public static void main(String[] args) {
        //args[0]: "http://servername:portnumber"
        //args[1]: test case number
    	int number = Integer.valueOf(args[1]);
        //test case0: Aggregation server died, content server send a PUT request
        //result: content server will send three times and Aggregation server does not reply
        if(number==0){
        	AsCrash_CsPUT(args[0]);
        }
        //test case1: Aggregation server died, GETClient send a PUT request
        //result: GETClient will send three times and Aggregation server does not reply
        else if(number==1){
        	AsCrash_ClientGET(args[0]);
        }
        //test case2:content server send a PUT request
        //result: Aggregation server send a 200 or 201 reply to content server
        //the Atom xml feed store in the feed directory 
        else if(number==2){
            CS_PUT(args[0]);
        }
        
        //test case3: GETClient send a GET request 
        //result: GETClient will read all feeds in feed directory, then convert it to txt form
        else if(number==3){
            Client_GET(args[0]);
        }
        //testcase4: three ContentServer send PUT getRequest
        //result: AggregationServer will deal the three putRequest in the order of LamportClock
        else if(number==4){
        	CS3_PUT(args[0]);
        }
        //testcase5:three GETClient send GET getRequest
        //result: all the client will read the feed in the order of arrive time
        else if(number==5){
        	Client3_GET(args[0]);
        }
        //testcase6: a ContentServer send PUTrequest and a GETClient send getRequest and 
        //the ContentServer send PUTrequest again.
        //result: GETClient will read the previous version of put
        
        else if(number==6){
        	PUT_GET_PUT(args[0]);
        }
        //testcase7: ContentServer send no content to the server 
        //result: ContentServer receive a error 204 code from AggregationServer
        else if(number==7){
            PUT_204(args[0]);
        }
        //testcase8:Any request other than GET or PUT for example POST
        //result: return status 400
        else if(number==8){
            PUT_400(args[0]);
        }
        //testcase9: if the ATOM XML does not make sense 
        //result: return status code 500 - Internal server error
        else if(number==9){
            
            GET_500(args[0]);
        }

    }

    
    //Test case 0: Content Server send a PUT request to ATOM server, but ATOM server does not
    //reply to Content Server. Content Server will retry three times and touch off a SocketTimeoutException when
    //Cs try to read the reply from ATOM server
    public static void AsCrash_CsPUT(String url){
        try{
        	System.out.println("===========test case 0===========\n");
            System.out.println("==============purpose============\n");
            System.out.println("test case0: Aggregation server died, content server send a PUT request");
            System.out.println("result: content server will send three times and Aggregation server does not reply");
        	System.out.println("===============result============\n");
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(0);
            AggregationServer.work = false;
            AggregationServer.start(port, server_Clock);
            ContentServer.PUTrequest(url,"Content_put_file0.txt",content_Clock);
            Thread.sleep(500);
            System.out.println("case 0 passed");
            System.out.println("===========test case 0===========\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    

    public static void AsCrash_ClientGET(String url){
        try{
        	System.out.println("===========test case 1===========\n");
            System.out.println("==============purpose============\n");
            System.out.println("test case1: Aggregation server died, GETClient send a PUT request");
            System.out.println("result: GETClient will send three times and Aggregation server does not reply");
            System.out.println("===============result============\n");
        	int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(0);
            AggregationServer.work = false;
            AggregationServer.start(port, server_Clock);
            GETClient.getRequest(url);
            Thread.sleep(500);
            System.out.println("case 1 passed");
            System.out.println("===========test case 1===========\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void CS_PUT(String url){
        try{
        	System.out.println("===========test case 2===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("test case2:content server send a PUT request");
            System.out.println("result: Aggregation server send a 200 or 201 reply to content server\n"+
        "the Atom xml feed store in the feed directory ");
            System.out.println("===============result============\n");
        	int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(100);
            
            AggregationServer.start(port, server_Clock);
            ContentServer.PUTrequest(url,"Content_put_file4.txt",content_Clock);

            Thread.sleep(500);
            System.out.println("case 2 passed");
            System.out.println("===========test case 2===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void Client_GET(String url){
        try{
            System.out.println("===========test case 3===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("test case3: GETClient send a GET request");
            System.out.println("result: GETClient will read all feeds in feed directory, then convert it to txt form");
            System.out.println("===============result============\n");
            String serverName = url.split(":")[1].substring(2,url.split(":")[1].length());
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock client_Clock = new LamportClock(0);

            
            AggregationServer.start(port, server_Clock); 
            GETClient.getRequest(url);
            Thread.sleep(500);
            System.out.println("case 3 passed");
            System.out.println("===========test case 3===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void CS3_PUT(String url){
        try{
            System.out.println("===========test case 4===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase4: three ContentServer send PUT getRequest");
            System.out.println("result: AggregationServer will deal the three putRequest in the order of LamportClock");
            System.out.println("===============result============\n");
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock0 = new LamportClock(10);
            LamportClock content_Clock1 = new LamportClock(5);
            LamportClock content_Clock2 = new LamportClock(2);
            
            AggregationServer.start(port, server_Clock);

            ContentServer.PUTrequest(url,"Content_put_file3.txt",content_Clock0);
            ContentServer.PUTrequest(url,"Content_put_file4.txt",content_Clock1);
            ContentServer.PUTrequest(url,"Content_put_file5.txt",content_Clock2);
            Thread.sleep(500);
            System.out.println("case 4 passed");
            System.out.println("===========test case 4===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void Client3_GET(String url){
        try{
        	System.out.println("===========test case 5===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase5:three GETClient send GET getRequest");
            System.out.println("result: all the client will read the feed in the order of arrive time");
            System.out.println("===============result============\n");
            String serverName = url.split(":")[1].substring(2,url.split(":")[1].length());
        	int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock client_Clock0 = new LamportClock(0);
            LamportClock client_Clock1 = new LamportClock(1);
            LamportClock client_Clock2 = new LamportClock(2);
            
            AggregationServer.start(port, server_Clock);
            
            GETClient.getRequest(url);
            
            GETClient.getRequest(url);
            // GETClient.getRequest(url);
            // GETClient.getRequest(url);
            Thread.sleep(500);
            System.out.println("case 5 passed");
            System.out.println("===========test case 5===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void PUT_GET_PUT(String url){
        try{
        	System.out.println("===========test case 6===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase6: a ContentServer send PUTrequest and a GETClient send getRequest and\n"+
                "the ContentServer send PUTrequest again");
            System.out.println("result: GETClient will read the previous version of put");
            System.out.println("===============result============\n");
        	int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock0 = new LamportClock(0);
            LamportClock content_Clock1 = new LamportClock(1);
            
            AggregationServer.start(port, server_Clock);
            ContentServer.PUTrequest(url,"Content_put_file0.txt",content_Clock0);
            GETClient.getRequest(url);
            ContentServer.PUTrequest(url,"Content_put_file0.txt",content_Clock1);
            Thread.sleep(500);
            System.out.println("case 6 passed");
            System.out.println("===========test case 6===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void PUT_204(String url){
        try{
            System.out.println("===========test case 7===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase7: ContentServer send no content to the server");
            System.out.println("result: ContentServer receive a error 204 code from AggregationServer");
            System.out.println("===============result============\n");
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(0);           
            AggregationServer.start(port, server_Clock);
            ContentServer.PUTrequest(url,"Content_put_file204.txt",content_Clock);
            Thread.sleep(500);
            System.out.println("case 7 passed");
            System.out.println("===========test case 7===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    

    public static void PUT_400(String url){
        try{
            System.out.println("===========test case 8===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase8:Any request other than GET or PUT for example POST");
            System.out.println("result: return status 400");
            System.out.println("===============result============\n");
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(0);           
            AggregationServer.start(port, server_Clock);
            ContentServer.PUT400(url,"Content_put_file400.txt",content_Clock);
            Thread.sleep(500);
            System.out.println("case 8 passed");
            System.out.println("===========test case 8===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void GET_500(String url){
        try{
            System.out.println("===========test case 9===========\n\n");
            System.out.println("==============purpose============\n");
            System.out.println("testcase9: if the ATOM XML does not make sense ");
            System.out.println("result: return status code 500 - Internal server error");
            System.out.println("===============result============\n");
            int port = Integer.valueOf(url.split(":")[2]);
            LamportClock server_Clock = new LamportClock(0);
            LamportClock content_Clock = new LamportClock(0); 
            String path = System.getProperty("java.class.path")+"/feed/";

            File directory = new File(path);
            if(directory.isDirectory()){
                File fileList[] = directory.listFiles();

                for(int i = 0;i<fileList.length;i++){
                    fileList[i].delete();
                    }
            }
            File file = new File(path+"feed500.txt");
            file.createNewFile(); 
            BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
            out.write("LamportClock: 100\n"+
                        "PUT/atom.xml HTTP/1.1\n"+
                        "User-Agent: ContentServer/1/0\n"+
                        "Content-Type: XML feed\n"+
                        "Content-Length: 17\n\n"+
                        "<?xml version='1.0' encoding='iso-8859-1' ?>\n"+
                        "<title>Content_put_file1");
            out.flush();
            out.close();     
            AggregationServer.start(port, server_Clock);
            GETClient.getRequest(url);
            Thread.sleep(500);
            System.out.println("case 9 passed");
            System.out.println("===========test case 9===========\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
