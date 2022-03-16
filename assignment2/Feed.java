import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Feed {
    public int content_length;
    public String content;

    //function: read and check valid Content Server PUT file and convert it to the correct Atom XML format.
    //input: the file prepared to be sended by Content Server 
    public Feed(String fileName){
        try{
            //read the PUT files
            String pathname = System.getProperty("java.class.path")+"/inputFile/"+fileName;
            FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader);
            ArrayList<String> txt = new ArrayList<>();
            String line;
            while((line = br.readLine())!=null){
                content_length++;
                txt.add(line+"\n");
            }
            reader.close();
            br.close();

            this.content = "<?xml version='1.0' encoding='iso-8859-1' ?>\n"+
                    "<feed xml:lang='en-US' xmlns=\"http://www.w3.org/2005/Atom\">\n"
                    +txt.toString()+"</feed>";
            this.content_length = content_length;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
