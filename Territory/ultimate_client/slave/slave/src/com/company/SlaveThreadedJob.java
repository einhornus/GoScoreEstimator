package com.company;


import java.io.IOException;
import java.net.UnknownHostException;

public class SlaveThreadedJob extends SocketThreadedJob {
    @Override
    public void callback(String s) throws IOException {
        System.out.println(s);
        Message message = new Message(s);
        if (message.header.equals("terminate")) {
            String title = message.content[0];
            String result = message.content[1];
            String users = message.content[2];
            String settings = message.content[3];
            String id = message.content[4];
            String text = "report_game users=" + users + ";title=" + title + ";settings=" + settings + ";result=" + result;
            Server.DBSocketRequest.send(text);

            String toKill = "kill_dfkfwwqpfdkg4ssvdfgg game_id="+id;
            Server.DBSocketRequest.send(toKill, Server.SocketManager.GetSocketsHost(), Server.SocketManager.LOBBY_SERVER.port);
            System.out.println("KILL KILL KILL");
        }
    }

    public int interval = 1000;

    public SlaveThreadedJob(int index) throws UnknownHostException, IOException {
        super(Server.SocketManager.GetSocketsHost(), Server.SocketManager.GAME_SERVERS[0].port);
        write("register_slave ");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(interval);
                        write("time ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
