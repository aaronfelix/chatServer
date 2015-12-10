package com.company;


//message order: the message, the user who sent it, their display name, and the current time.

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {


    static ArrayList<Message> messages = new ArrayList<Message>();
    ArrayList<String> ips = new ArrayList<String>();
    static ArrayList<ChatThread> threads = new ArrayList<ChatThread>();
    static int desiredmessageamount = 30;
    static boolean running = true;

    static String bell = String.valueOf((char)7);

    //  ArrayList<String> usernames = new ArrayList<String>();
    // ArrayList<String> passwords = new ArrayList<String>();
//    String[] usernames = {"felix"};
//    String[] passwords = {"master"};
    Map<String, String> users = Collections.singletonMap("felix", "master");

    public static void main(String[] args) throws Exception{

            TheServer server = new TheServer( new InetSocketAddress( 80 ));
            server.start();
            System.out.println("started");




            while (running) {
            /* ChatThread chatThread = new ChatThread(clientSocket);
            threads.add(chatThread);
            chatThread.start();*/
            /*Iterator<ChatThread> cleaner = threads.iterator();
            while (cleaner.hasNext()){
                ChatThread thread = cleaner.next();
                if(!thread.isAlive()){
                    cleaner.remove();
                }
            }

       */
                java.lang.Thread.sleep(10000);
            }

        System.out.println("quitting!");




    }




    public static String getCurrentTimeStamp() {
        //SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy/HH:mm:ss");//dd/MM/yyyy
        SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm:ss a");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


    private static class Message {

        private String sender;
        private String message;
        private String timestamp;
        private String senderDisplayName;

        public String getSenderDisplayName() {return senderDisplayName;}

        public void setSenderDisplayName(String senderDisplayName) {this.senderDisplayName = senderDisplayName;}

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return this.sender + "\u0007" + this.message + "\u0007" + this.senderDisplayName + "\u0007" + this.timestamp + "\u0007";
        }

        public Message(String sender, String senderDisplayName, String message, String timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
            this.senderDisplayName = senderDisplayName;

        }
    }

    public static class TheServer extends WebSocketServer {


       public TheServer(InetSocketAddress address) {
           super(address);
       }

       @Override
       public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
           System.out.println("connected");
           if(messages.size() > 30){
               for (int i=30; i > 1; i--) {
                   webSocket.send(messages.get(messages.size() - i).toString());
               }
           }
       }

       @Override
       public void onClose(WebSocket webSocket, int i, String s, boolean b) {
         /*  Message msg = new Message(s.split("\u0007")[0],s.split("\u0007")[1],s.split("\u0007")[2],getCurrentTimeStamp());



           Collection<WebSocket> con = connections();
           for( WebSocket c : con ) {l

               c.send( s );
           }
           synchronized ( messages ) {
               messages.add(msg);
           }*/
       }

       @Override
       public void onMessage(WebSocket webSocket, String s) {
           System.out.println(s.split(bell)[0]);

           Message msg = new Message(s.split(bell)[0],s.split(bell)[1],s.split(bell)[2],getCurrentTimeStamp());

               Collection<WebSocket> con = connections();
                for( WebSocket c : con ) {
                 c.send( s + getCurrentTimeStamp() + "\u0007");
                }
               synchronized ( messages ) {

                   messages.add(msg);
                   System.out.println("onmessage");
               }


       }

       @Override
       public void onError(WebSocket webSocket, Exception e) {
            e.printStackTrace();
       }
    }

    private static class ChatThread extends Thread{
        Socket clientSocket;
        PrintWriter out;
        ChatThread(Socket cs){
            clientSocket = cs;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public void run() {
            try {
                super.run();
                String line;

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                for (int i = messages.size() - Math.min(messages.size(), desiredmessageamount); i < messages.size(); i++) {
                    this.deliver(messages.get(i));//give last 30 messages on login
                }


                while((line = in.readLine()) != null){//the line in is a legit message, not a time out or something. AKA: on recieve

                    Message msg = new Message(line.split("\u0007")[0],line.split("\u0007")[1],line.split("\u0007")[2],getCurrentTimeStamp());

                    System.out.println("recieved");
                    messages.add(msg);




                    for(ChatThread chatThread : threads){//send out the new text to every client
                        this.deliver(msg);
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void deliver(Message message){
            out.print(message.message);
            out.print("\u0007");
            out.print(message.sender);
            out.print("\u0007");
            out.print(message.senderDisplayName);
            out.print("\u0007");
            out.print(message.timestamp);
            out.print("\u0007");
            out.flush();


        }

    }
}
