package com.company;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class GameSocketJob extends SocketThreadedJob {
    public Action<GameInfo> goToGameCallback;
    public TokenAndId tai;
    public Action<String> moveCallback;
    public Action<Void> endedCallback;

    public boolean isPermission;
    public boolean isGameState;

    public void tryMove(){
        if(isPermission && isGameState){
            moveCallback.doJob(currentState);
            isPermission = false;
            isGameState = false;
        }
    }

    @Override
    public void callback(String s) throws IOException {
        System.out.println(s);
        s = s.substring(0, s.length() - 1);
        SocketThreadedJob.Message message = new SocketThreadedJob.Message(s);

        if(s.contains("event ask")){
            isGameState = false;
        }

        if (message.header.equals("+permission"))
        {
            int index = Integer.parseInt(message.content[0]);
            permissions[index] = true;
            if(index == getIndex()){
                isPermission = true;
                tryMove();
            }
        }

        if (message.header.equals("-permission"))
        {
            int index = Integer.parseInt(message.content[0]);
            permissions[index] = false;
            if(index == getIndex()){
                isPermission = false;
                tryMove();
            }
        }

        if (message.header.equals("game_state"))
        {
            String state = message.content[0];
            int index = Integer.parseInt(message.content[1]);
            if(!this.currentState.equals(state)) {
                isGameState = true;
            }
            else{
            }
            this.currentState = state;
            tryMove();
        }

        if (message.header.equals("end"))
        {
            this.endedCallback.doJob(null);
            stop();
        }

    }


    public GameInfo game;
    private int[] timers;
    private boolean[] permissions;
    private String currentState = "";

    public int getIndex()
    {
        int index = -1;
        for (int i = 0; i < game.users.size(); i++)
        {
            if (game.users.get(i) == tai.id)
            {
                index = i;
            }
        }
        return index;
    }

    public GameSocketJob(TokenAndId tai, GameInfo game, Action<String> moveCallback, Action<Void> endedCallback) throws UnknownHostException, IOException {
        super(Server.SocketManager.GAME_SERVERS[game.server].host, Server.SocketManager.GAME_SERVERS[game.server].port);
        this.game = game;
        this.endedCallback = endedCallback;
        this.moveCallback = moveCallback;
        this.tai = tai;
        this.timers = new int[game.max];
        this.permissions = new boolean[game.max];
        int index = getIndex();

        if (index != -1)
        {
            String sss = "authorize id=" + tai.id + ";token=" + tai.token + ";game_id=" + game.id + ";index=" + index;
            this.write(sss);
        }
        else
        {
            String sss = "observe id=" + tai.id + ";token=" + tai.token + ";game_id=" + game.id;
            this.write(sss);
        }

        if(index == 0){
            String ssss = "initialize id=" + tai.id + ";token=" + tai.token + ";game_id="+game.id+";title="+game.title+";settings="+game.settings+";users=";
            for (int i = 0; i < game.users.size(); i++ )
            {
                ssss += game.users.get(i);
                if(i != game.users.size() - 1){
                    ssss += "?";
                }
            }
            this.write(ssss);
        }
    }

}
