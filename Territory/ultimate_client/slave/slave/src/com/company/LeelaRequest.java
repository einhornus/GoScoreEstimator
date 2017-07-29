package com.company;


import java.util.ArrayList;

public class LeelaRequest {
    public static class RequestParameters{
        public int thread = 0;
        public ArrayList<Move> moves;
        public String index;
        public Action<LeelaResponse> action;
        public int times=1;
        public int playouts=100;
        public double komi=7.5;
        public int han=0;
        public int boardSize=19;
    }

    public static void query(RequestParameters parameters)throws Exception {
        System.out.println("Initiate "+parameters.index+" of "+parameters.thread);
        Server.SocketManager.HostAndPort hap = Server.SocketManager.LEELA_SERVERS[parameters.thread];
        LeelaRequestThreadedJob slave = new LeelaRequestThreadedJob(hap.host, hap.port, parameters);
        slave.start();
    }
}
