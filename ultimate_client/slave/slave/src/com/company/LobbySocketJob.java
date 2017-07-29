package com.company;


import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class LobbySocketJob extends SocketThreadedJob {

    public Action<GameInfo> goToGameCallback;
    public Action<ArrayList<GameInfo>> gamesCallback;
    public TokenAndId tai;
    public ArrayList<GameInfo> games = new ArrayList<>();

    @Override
    public void callback(String s) throws IOException {
        if(s.charAt(0)=='!'){
            return;
        }

        s = s.substring(0, s.length() - 1);
        Message message = new Message(s);

        if (message.header.equals("go_to_game"))
        {
            GameInfo game = GameInfo.deserializeJson(message.content[0]);
            this.goToGameCallback.doJob(game);
        }



        if(message.header.equals("all_games")){
            //System.out.println("all_games "+message+" "+message.content);
            for (int i = 0; i < message.content.length; i++ )
            {
                if (!message.content[i].equals(""))
                {
                    GameInfo game = GameInfo.deserializeJson(message.content[i]);
                    games.add(game);
                }
            }
        }


        if (message.header.equals("new_game"))
        {
            GameInfo game = GameInfo.deserializeJson(message.content[0]);
            games.add(game);
        }


        if (message.header.equals("game+"))
        {
            int gameId = Integer.parseInt(message.content[0]);
            int userId = Integer.parseInt(message.content[1]);
            GameInfo game = findGame(gameId);
            game.users.add(userId);
        }

        if (message.header.equals("game-"))
        {
            int gameId = Integer.parseInt(message.content[0]);
            int userId = Integer.parseInt(message.content[1]);
            GameInfo game = findGame(gameId);
            if(game != null) {
                game.users.remove((Object) userId);
            }
        }

        if (message.header.equals("game_closed"))
        {
            int gameId = Integer.parseInt(message.content[0]);
            GameInfo game = findGame(gameId);
            game.status = "closed";
        }

        if (message.header.equals("game_removed"))
        {
            int gameId = Integer.parseInt(message.content[0]);
            int index = -1;
            for (int i = 0; i < games.size(); i++ )
            {
                if(games.get(i).id == gameId){
                    index = i;
                }
            }
            if(index != -1) {
                games.remove(index);
            }
        }

        /*
        try {
            delay(1000, 2000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        */
        gamesCallback.doJob(games);
    }

    private GameInfo findGame(int id)
    {
        for (int i = 0; i < games.size(); i++ )
        {
            if(games.get(i).id == id){
                return games.get(i);
            }
        }
        return null;
    }


    public LobbySocketJob(TokenAndId tai, Action<GameInfo> goToGameCallback, Action<ArrayList<GameInfo>> gamesCallback) throws UnknownHostException, IOException {
        super(Server.SocketManager.LOBBY_SERVER.host, Server.SocketManager.LOBBY_SERVER.port);
        write("authorize id="+tai.id+";token="+tai.token);
        this.gamesCallback = gamesCallback;
        this.goToGameCallback = goToGameCallback;
        this.tai = tai;
    }



    public void createNewGame(String game, String settings) throws IOException
    {
        String str = "create_game max=" + 2 + ";game=" + game + ";settings=" + settings;
        this.write(str);
    }


    public void openForRandomChallenge(String game, String settings)throws IOException
    {
        String str = "open_for_random_challenge " + "game=" + game + ";settings=" + settings+";id="+this.tai.id;
        this.write(str);
    }
}
