package com.company;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;

public abstract class SocketThreadedJob extends ThreadedJob {
    public static class Message {
        public String header;
        public String[] content;
        public static char MAIN_DELIMITER = ' ';
        public static char SIDE_DELIMITER = ';';
        public static int u = 0;



        public Message(String line) throws IOException {
            if (line.charAt(0) == '!') {
                System.out.println("error");
                header = "error";
                return;
            }

            String[] main = line.split(MAIN_DELIMITER + "");
            header = main[0];
            if(main.length == 2) {
                content = main[1].split(SIDE_DELIMITER + "");
            }
            else{
                content = new String[0];
            }
        }
    }

    public void delay(int from, int to)throws InterruptedException{
        int del = (int)(Math.random()*(to-from)+from);
        Thread.sleep(del);
    }


    @Override
    public void stop() {
        this.done = true;
        try{
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Socket socket;
    public SocketThreadedJob(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        inputStream = new BufferedInputStream(socket.getInputStream());
    }

    OutputStream outputStream;
    InputStream inputStream;

    String buffer = "";

    public void write(String s)throws IOException{
        s = s+"\r\n";
        byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
        outputStream.write(bytes);
        outputStream.flush();
    }

    public abstract void callback(String s)throws IOException;

    @Override
    public void heart() throws IOException {
        if(socket.isClosed()){
            return;
        }
        char c = (char) inputStream.read();
        if (c == '\n') {
            callback(buffer);
            buffer = "";
        } else {
            buffer += c + "";
        }
    }
}