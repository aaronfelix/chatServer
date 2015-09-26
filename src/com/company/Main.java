package com.company;

//internal message order: the user who sent it, their display name, the message, and the current time.
//external message order: the message, the user who sent it, their display name, and the current time.

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class Main extends HttpServlet {

    ArrayList<Message> messages = new ArrayList<Message>();
    ArrayList<String> ips = new ArrayList<String>();
    //  ArrayList<String> usernames = new ArrayList<String>();
    // ArrayList<String> passwords = new ArrayList<String>();
//    String[] usernames = {"felix"};
//    String[] passwords = {"master"};
    Map<String, String> users = Collections.singletonMap("felix", "master");


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int desiredmessageamount = 30;
        int bell = 07;
        PrintWriter out = response.getWriter();
        //messages.add(new Message("me", "helloooooo", "yesterday"));
        try {
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            if (ips.contains(request.getRemoteAddr())) {
                out.print("o" + (char)bell + "o " + (char)bell + "o" + (char)bell + "o" + (char)bell);
                for (int i = messages.size() - Math.min(messages.size(), desiredmessageamount); i < messages.size(); i++) {

                    out.print(messages.get(i).getMessage());
                    out.print((char)bell);
                    out.print(messages.get(i).getSender());
                    out.print((char)bell);
                    out.print(messages.get(i).getSenderDisplayName());
                    out.print((char)bell);
                    out.print(messages.get(i).getTimestamp());
                    out.print((char)bell);


                }

            } else if (pass != null && pass.equals(users.get(user))) {

             /*   if(Arrays.asList(usernames).contains(user) && Arrays.asList(passwords).contains(pass)){
                    out.print("adding ip...");
                    if(passwords[Arrays.asList(usernames).indexOf(user)].equals(pass)){//if they aren't on the list of ips
                    }
                }*/

                ips.add(request.getRemoteAddr());
                out.print("ip added");
            } else {
                out.print("wrong pass");
                out.print(request.getRemoteAddr());
            }

            /*else if(usernames.contains(request.getParameter("user")) && passwords.contains(request.getParameter("pass"))){
                if(passwords.get(usernames.indexOf(request.getParameter("user"))) == request.getParameter("pass")){//if they aren't on the list of ips
                    ips.add(request.getRemoteAddr());
                }

            }*/


        } finally {
            out.close();
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        
            messages.add(new Message(request.getParameter("user"), request.getParameter("displayname"), request.getParameter("message"), getCurrentTimeStamp()));

            System.out.println((int)request.getParameter("message").getBytes()[0]);

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

}
