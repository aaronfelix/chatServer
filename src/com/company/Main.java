package com.company;

//internal message order: the user who sent it, their display name, the message, and the current time.
//external message order: the message, the user who sent it, their display name, and the current time.

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {

    static ArrayList<Message> messages = new ArrayList<Message>();
    ArrayList<String> ips = new ArrayList<String>();
    static ArrayList<ChatThread> threads = new ArrayList<ChatThread>();
    static int desiredmessageamount = 30;
    //  ArrayList<String> usernames = new ArrayList<String>();
    // ArrayList<String> passwords = new ArrayList<String>();
//    String[] usernames = {"felix"};
//    String[] passwords = {"master"};
    Map<String, String> users = Collections.singletonMap("felix", "master");

    public static void main(String[] args) throws Exception{
        ServerSocket ss;
        Socket clientSocket;


        ss = new ServerSocket(44555);
        while (true) {
            clientSocket = ss.accept();
            System.out.println("recieved");
            ChatThread chatThread = new ChatThread(clientSocket);
            threads.add(chatThread);
            chatThread.start();



            Iterator<ChatThread> cleaner = threads.iterator();
            while (cleaner.hasNext()){
                ChatThread thread = cleaner.next();
                if(!thread.isAlive()){
                    cleaner.remove();

                }

            }

        }




    }




    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy/HH:mm:ss");//dd/MM/yyyy
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


        public Message(String sender, String senderDisplayName, String message, String timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
            this.senderDisplayName = senderDisplayName;

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
                    this.deliver(messages.get(i));
                }


                while((line = in.readLine()) != null){

                    Message msg = new Message(line.split("\u0007")[0],line.split("\u0007")[1],line.split("\u0007")[2],getCurrentTimeStamp());

                    System.out.println("recieved");
                    messages.add(msg);




                    for(ChatThread chatThread : threads){
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
