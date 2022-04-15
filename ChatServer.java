package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    public ServerSocket server;
    public Socket client = null;
    public DataOutputStream os;
    public DataInputStream is;
    public HashMap<String, Socket> clientList = new HashMap<String, Socket>();

    public static void main(String[] args) {
        ChatServer a = new ChatServer();
        a.doConnections();
    }

    public void doConnections() {
        try {
            server = new ServerSocket(6556);
            MyThreadServer messageRouterThread = new MyThreadServer();
            messageRouterThread.start();
            while (true) {

                client = server.accept();
                if (client != null) {
                    os = new DataOutputStream(client.getOutputStream());
                    is = new DataInputStream(client.getInputStream());
                    String requestedClientName = is.readUTF();
                    clientList.put(requestedClientName, client);
                    os.writeUTF("#accepted");
                    messageRouterThread.clientList.put(requestedClientName, client);
                }
            }
        } catch (Exception e) {
            System.out.println("Error Occured Oops!" + e.getMessage());
        }
    }
}

class MyThreadServer extends Thread {

    public HashMap<String, Socket> clientList = new HashMap<String, Socket>();
    public DataInputStream is = null;
    public DataOutputStream os = null;

    public void run() {
        String msg = "";
        int i = 0;
        System.out.println("Chat Server Running .....");
        String toClientName = "";
        while (true) {
            try {
                if (clientList != null) {
                    for (String key : clientList.keySet()) {
                        is = new DataInputStream(clientList.get(key).getInputStream());
                        if (is.available() > 0) {

                            msg = is.readUTF();

                            if (msg.equals("list")) {
                                os = new DataOutputStream(clientList.get(key).getOutputStream());
                                msg = clientList.toString();
                                os.writeUTF(msg);
                            } else {
                                i = msg.indexOf("@");
                                toClientName = msg.substring(i);

                                if (toClientName.equals("@all")) {

                                    for (Map.Entry<String, Socket> client : clientList.entrySet()) {
                                        os = new DataOutputStream(clientList.get(client.getKey()).getOutputStream());
                                        os.writeUTF(key + ": " + msg.substring(0, i));
                                    }
                                    break;
                                }

                                // System.out.println(msg);
                                // obtained the client name 
                                // now write that message to that client
                                if (clientList.containsKey(toClientName)) {
                                    os = new DataOutputStream(clientList.get(toClientName).getOutputStream());
                                    os.writeUTF(key + ": " + msg.substring(0, i));
                                } else {
                                    os = new DataOutputStream(clientList.get(key).getOutputStream());
                                    os.writeUTF("Message From Server : No such Username found");
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Server Recovering Initialization Failure ...");
                System.out.println("Server Up And Running ...");
            }
            //goes through all clientList and reads the UTF and Writes to that Client
        }
    }
}
