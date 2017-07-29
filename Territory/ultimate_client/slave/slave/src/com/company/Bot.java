package com.company;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.json.*;


public class Bot {
    TokenAndId tai;
    LobbySocketJob lobbySocketJob;
    GameSocketJob gameSocketJob;


    public String title;
    public String settings;
    public boolean type = true;
    public static int french = 1000000;
    public boolean entered = false;
    public static int[] askingTimesOneColor = new int[]{10, 20, 60};
    public static int[] askingTimesBlind = new int[]{10, 15, 35};

    public static boolean revealAgree = true;


    public void begin() throws IOException {
        entered = false;
        lobbySocketJob = new LobbySocketJob(tai, new Action<GameInfo>() {
            @Override
            public void doJob(GameInfo item) {
                try {
                    MoveHandler mh = new MoveHandler();
                    mh.hiddens = null;
                    gameSocketJob = new GameSocketJob(tai, item, mh, new Action<Void>() {
                        @Override
                        public void doJob(Void item) {
                            System.out.println("Game ended");
                            gameSocketJob.stop();
                            lobbySocketJob.stop();
                            System.out.println("End lobby");
                            try {
                                begin();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mh.gsj = gameSocketJob;
                    gameSocketJob.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("go to game");
            }
        }
                , new Action<ArrayList<GameInfo>>() {
            @Override
            public void doJob(ArrayList<GameInfo> item) {
                try {
                    if (type) {
                        for (int i = 0; i < item.size(); i++) {
                            GameInfo game = item.get(i);
                            if (game.status.equals("open")) {
                                if (!game.users.contains(tai.id)) {
                                    if (game.title.equals(title)) {
                                        if (game.settings.equals(settings)) {
                                            if (!entered) {
                                                lobbySocketJob.write("enter game_id=" + game.id);
                                                entered = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        );
        System.out.println("Start lobby");
        lobbySocketJob.start();
        if (!type) {
            lobbySocketJob.createNewGame(title, settings);
        }
    }

    public String login;

    public Bot(String login, String password, String title, String settings, boolean type) throws IOException {
        this.title = title;
        this.login = login;
        this.type = type;
        this.settings = settings;
        Server.register(login, password);
        TokenAndId tai = Server.authorize(login, password);
        this.tai = tai;
        begin();
    }


    public static class MoveHandler implements Action<String> {
        public GameSocketJob gsj;
        public ArrayList<Move> hiddens = null;
        public ArrayList<Integer> colors = null;
        public boolean asked = false;
        public boolean isSetup = false;


        public MoveHandler() {
        }

        @Override
        public void doJob(String str) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(str);


                String phase = obj.getString("phase");
                int black = Integer.parseInt(obj.getString("black_player"));
                double komi1 = Double.parseDouble(obj.getString("komi1"));
                double komi2 = Double.parseDouble(obj.getString("komi2"));
                //double isRevealed = int.Parse(_attrs[3]) == 1);
                int handi = Integer.parseInt(obj.getString("handi"));
                int fpl = Integer.parseInt(obj.getString("fpl"));

                int boardSize = Integer.parseInt(obj.getString("n"));
                int nPlayers = 2;
                String gameTitle = obj.getString("title");
                String boardContent = obj.getString("board");
                Random random = new Random();

                if (random.nextInt() % french == 0) {
                    try {
                        gsj.write("move move=resign;game_db_id=" + gsj.game.id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (phase.equals("counting")) {
                    try {
                        gsj.write("move move=agree;game_db_id=" + gsj.game.id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(phase.equals("setup")){
                    if(!isSetup) {
                        int count = Integer.parseInt(obj.getString("hm_count"));

                        ArrayList<NormalMove> unique = new ArrayList<>();
                        while(unique.size() < count){
                            int x = Math.abs(random.nextInt() % boardSize);
                            int y = Math.abs(random.nextInt() % boardSize);
                            boolean good = true;
                            for(int j = 0; j<unique.size(); j++){
                                if(unique.get(j).x == x && unique.get(j).y == y){
                                    good = false;
                                    break;
                                }
                            }
                            if (good){
                                unique.add(new NormalMove(x, y));
                                gsj.write("move move=" + x + "-" + y + ";game_db_id=" + gsj.game.id);
                                Thread.sleep(1000);
                            }
                        }
                        Thread.sleep(1000);
                        gsj.write("move move=" + "go" + ";game_db_id=" + gsj.game.id);
                        isSetup = true;
                    }
                }

                if (phase.equals("game")) {
                    boolean ask = obj.getString("asking").equals("1");
                    int rev1 = Integer.parseInt(obj.getString("rev1") + "");
                    int rev2 = Integer.parseInt(obj.getString("rev2") + "");


                    if (ask) {
                        try {
                            Thread.sleep(3000);
                            if(revealAgree) {
                                gsj.write("move move=yes;game_db_id=" + gsj.game.id);
                            }
                            else{
                                gsj.write("move move=no;game_db_id=" + gsj.game.id);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    ArrayList<Move> moves = new ArrayList<>();


                    if (obj.has("hidden_moves") && hiddens == null) {
                        hiddens = new ArrayList<>();
                        colors = new ArrayList<>();
                        JSONArray hiddenMovesSequence = obj.getJSONArray("hidden_moves");
                        for (int i = 0; i < hiddenMovesSequence.length(); i++) {
                            Object mstr = hiddenMovesSequence.get(i);
                            String s = (String) mstr;
                            String[] xy = s.split("\\#");
                            int x = Integer.parseInt(xy[0]);
                            int y = Integer.parseInt(xy[1]);
                            int color = Integer.parseInt(xy[2]);
                            Move move = new NormalMove(x, y);
                            hiddens.add(move);
                            colors.add(color);
                        }
                    }

                    if (hiddens != null) {
                        ArrayList<Move> blacks = new ArrayList<>();
                        ArrayList<Move> whites = new ArrayList<>();

                        if (fpl == 0) {
                            for (int i = 0; i < hiddens.size(); i++) {
                                if(colors.get(i) == 0){
                                    blacks.add(hiddens.get(i));
                                }
                                if(colors.get(i) == 1){
                                    whites.add(hiddens.get(i));
                                }

                            }
                        } else {
                            for (int i = 0; i < hiddens.size(); i++) {
                                if(colors.get(i) == 1){
                                    blacks.add(hiddens.get(i));
                                }
                                if(colors.get(i) == 0){
                                    whites.add(hiddens.get(i));
                                }
                            }
                        }

                        for(int i = 0; i<blacks.size(); i++){
                            moves.add(blacks.get(i));
                            moves.add(whites.get(i));
                        }
                    }

                    JSONArray movesSequence = obj.getJSONArray("moves");
                    for (int i = 0; i < movesSequence.length(); i++) {
                        Object mstr = movesSequence.get(i);
                        if (mstr.equals("pass")) {
                            Move move = new Pass();
                            moves.add(move);
                        } else {
                            String s = (String) mstr;
                            String[] xy = s.split("\\-");
                            int x = Integer.parseInt(xy[0]);
                            int y = Integer.parseInt(xy[1]);
                            Move move = new NormalMove(x, y);
                            moves.add(move);
                        }
                    }

                    int limit = 1000000;
                    if(gameTitle.equals("one-color-go") && boardSize == 9){
                        limit = askingTimesOneColor[0];
                    }

                    if(gameTitle.equals("one-color-go") && boardSize == 13){
                        limit = askingTimesOneColor[1];
                    }

                    if(gameTitle.equals("one-color-go") && boardSize == 19){
                        limit = askingTimesOneColor[2];
                    }

                    if(gameTitle.equals("blind-go") && boardSize == 9){
                        limit = askingTimesBlind[0];
                    }

                    if(gameTitle.equals("blind-go") && boardSize == 13){
                        limit = askingTimesBlind[1];
                    }

                    if(gameTitle.equals("blind-go") && boardSize == 19){
                        limit = askingTimesBlind[2];
                    }

                    boolean isRevealed = false;
                    if(obj.has("is_revealed") && (Integer)obj.get("is_revealed")==1){
                        isRevealed = true;
                    }
                    if(moves.size() > limit && !asked && !isRevealed){
                        asked = true;
                        gsj.write("move move=ask;game_db_id=" + gsj.game.id);
                    }
                    else {
                        try {
                            LeelaRequest.RequestParameters parameters = new LeelaRequest.RequestParameters();
                            parameters.thread = 0;
                            parameters.moves = moves;
                            parameters.index = Math.random() + "";
                            parameters.han = handi;
                            parameters.boardSize = boardSize;
                            parameters.komi = komi1 != 0 ? komi1 : komi2;
                            parameters.playouts = 1000;
                            parameters.action = new Action<LeelaResponse>() {
                                @Override
                                public void doJob(LeelaResponse item) {
                                    try {
                                        if (item.bestMove instanceof Pass) {
                                            gsj.write("move move=pass;game_db_id=" + gsj.game.id);
                                        } else {

                                            if (item.bestMove instanceof NormalMove) {
                                                gsj.write("move move=" + ((NormalMove) item.bestMove).x + "-" + ((NormalMove) item.bestMove).y + ";game_db_id=" + gsj.game.id);
                                            } else {
                                                gsj.write("move move=resign" + ";game_db_id=" + gsj.game.id);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            LeelaRequest.query(parameters);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
