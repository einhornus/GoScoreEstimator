package com.company;


import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import org.json.*;


public class Server {

    public static void register(String login, String password) {
        String str = "register " + "login=" + login + ";password=" + password;
        String response = DBSocketRequest.send(str);
        System.out.println(response);
    }



    public static TokenAndId authorize(String login, String password) {
        String str = "authorize " + "login=" + login + ";password=" + password;
        String response = DBSocketRequest.send(str);
        try {
            JSONObject reader = new JSONObject(response);
            int id = reader.getInt("id");
            String token = reader.getString("token");
            TokenAndId res = new TokenAndId(id, token);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class SocketManager {
        public static class HostAndPort {
            public String host;
            public int port;

            public HostAndPort(String host, int port) {
                this.host = host;
                this.port = port;
            }
        }

        public static String PRODUCTION = "178.62.112.136";
        public static String MY = "46.101.218.246";
        public static String LOCALHOST = "localhost";
        public static String GO = "139.59.117.106";


        public static String GetSocketsHost() {
            return LOCALHOST;
        }

        public static HostAndPort DB_SERVER = new HostAndPort(GetSocketsHost(), 111);
        public static HostAndPort BOT_SERVER = new HostAndPort(GetSocketsHost(), 116);
        public static HostAndPort LOBBY_SERVER = new HostAndPort(GetSocketsHost(), 112);
        public static HostAndPort[] GAME_SERVERS = new HostAndPort[]{new HostAndPort(GetSocketsHost(), 113)};
        public static HostAndPort CHAT_SERVER = new HostAndPort(GetSocketsHost(), 114);
        public static HostAndPort[] PICTURE_SERVERS = new HostAndPort[]{new HostAndPort(GetSocketsHost(), 115)};
        public static HostAndPort[] LEELA_SERVERS = new HostAndPort[]{
                new HostAndPort(GetSocketsHost(), 20001),
                new HostAndPort(GetSocketsHost(), 20002),
                new HostAndPort(GetSocketsHost(), 20003),
                new HostAndPort(GetSocketsHost(), 20004),
                new HostAndPort(GetSocketsHost(), 20005),
                new HostAndPort(GetSocketsHost(), 20006),
                new HostAndPort(GetSocketsHost(), 20007),
                new HostAndPort(GetSocketsHost(), 20008),
                new HostAndPort(GetSocketsHost(), 20009),
                new HostAndPort(GetSocketsHost(), 20010),
                new HostAndPort(GetSocketsHost(), 20011),
                new HostAndPort(GetSocketsHost(), 20012)
        };
        public static int CHUNK_SIZE = 16000;
    }

    public static class DBSocketRequest {
        public String host = SocketManager.DB_SERVER.host;
        public int port = SocketManager.DB_SERVER.port;

        public static void write(BufferedOutputStream out, String s) throws IOException {
            s = s + "\r\n";
            byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
            out.write(bytes);
            out.flush();
        }

        public static String readLine(BufferedInputStream is) throws IOException {
            String res = "";
            char c = 0;
            while ((c = (char) is.read()) != '\n') {
                res += c;
            }
            res = res.replace('@', '\n');
            return res;
        }

        public static String send(String str, String host, int port) {
            Socket s = null;
            String r = "";
            try {
                s = new Socket(host, port);
                BufferedOutputStream outputStream = new BufferedOutputStream(s.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(s.getInputStream());
                write(outputStream, str);
                r = readLine(inputStream);
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }


        public static String send(String str) {
            Socket s = null;
            String r = "";
            try {
                s = new Socket(SocketManager.GetSocketsHost(), SocketManager.DB_SERVER.port);
                BufferedOutputStream outputStream = new BufferedOutputStream(s.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(s.getInputStream());
                write(outputStream, str);
                r = readLine(inputStream);
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }
    }
}
