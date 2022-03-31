package chat;

import java.io.*;
import java.net.*;

public class ChatClient{
    
    public Socket client = null;
    public DataOutputStream os;
    public DataInputStream is;
    public String clientName="@Client0";

    public ChatClient(){

    }
   
    public static void main(String[] args)throws IOException{
        
        ChatClient a = new ChatClient();
        a.doConnections();

    }
    public void doConnections()throws IOException{
        try{
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Enter Your Name <Please Dont use Space> : ");
        clientName = "@" + br.readLine();
        client = new Socket("127.0.0.1",6556);
        os = new DataOutputStream(client.getOutputStream());
        is = new DataInputStream(client.getInputStream());
        //request as a client 
        String request=clientName;
        os.writeUTF(request);
        String response = is.readUTF(); 
        MyThreadRead read = new MyThreadRead(is);
        MyThreadWrite write = new MyThreadWrite(os,clientName);
        if(response.equals("#accepted")){
            System.out.println("# Login Successful  as "+ clientName +" !");
            System.out.println("Message Format");
            System.out.println("--------------");
            System.out.println("MESSAGE_BODY @to_username [Message Body may contain Space]");
           //now run the thread
            read.start();
            write.start();

            read.join();
            write.join();
        }     
        else
        {
            System.out.println("# Could Not Connect To Server !");
        }      
         
      }
      catch(Exception e){
        System.out.println("Error Occured Oops!");
      }
    }
}
class MyThreadRead extends Thread{
    DataInputStream is;
    public MyThreadRead(DataInputStream i){
           is=i;  
    }
    public void run()
    {
        try{
            String msg=null;
            while(true){
                msg = is.readUTF();
                if(msg != null)
                    System.out.println(msg);
                
                
                msg = null;
            }
         }
         catch(Exception e){

         }
    }
} 
class MyThreadWrite extends Thread{
    private DataOutputStream os;
    public BufferedReader br;
    public String clientName =  "@Client0";
    public MyThreadWrite(DataOutputStream o,String name){
           os=o;
           clientName = name;
           try{
            InputStreamReader isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
           }
           catch(Exception e)
           {

           }
    }
    public void run()
    {
        try{
            while(true){
                String msg = br.readLine();
                os.writeUTF(msg);
            }
        }
        catch(Exception e){
            
        }
    }
}