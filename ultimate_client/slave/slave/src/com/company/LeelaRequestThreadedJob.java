package com.company;


import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class LeelaRequestThreadedJob extends SocketThreadedJob {

    @Override
    public void callback(String s) throws IOException {
        s = s.substring(0, s.length() - 1);
        Message message = new Message(s);

        if(message.header.equals("best_move")){
            String index = message.content[0];
            if(this.index.equals(index)){
                String move = message.content[1];
                Move bm = Move.parse(move);
                this.response.bestMove = bm;
            }
        }

        if(message.header.equals("variation")){
            String index = message.content[0];
            if(this.index.equals(index)){
                Move move = Move.parse(message.content[1]);
                double winrate = Double.parseDouble(message.content[2]);
                int iterations = Integer.parseInt(message.content[3]);
                String[] movesString = message.content[4].split("#");
                ArrayList<Move> pv = new ArrayList<>();
                for(int i = 0; i<movesString.length; i++) {
                    pv.add(Move.parse(movesString[i]));
                }
                LeelaResponse.Variation variation = new LeelaResponse.Variation();
                variation.root = move;
                variation.iterations = iterations;
                variation.winrate = winrate;
                variation.pv = pv;
                this.response.variations.add(variation);
            }
        }

        if(message.header.equals("end")) {
            String index = message.content[0];
            response.index = index;
            if (this.index.equals(index)) {
                this.callback.doJob(response);
            }
            this.stop();
        }
    }


    private Action<LeelaResponse> callback;
    private LeelaResponse response = new LeelaResponse();
    private String index;
    public LeelaRequestThreadedJob(String host, int port, LeelaRequest.RequestParameters parameters) throws UnknownHostException, IOException {
        super(host, port);
        this.callback = parameters.action;
        this.index = parameters.index;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<parameters.moves.size(); i++){
            builder.append(parameters.moves.get(i).toString());
            if(i != parameters.moves.size() - 1){
                builder.append("|");
            }
        }
        write("leela index="+parameters.index+";moves="+builder.toString()+";times="+parameters.times+";delegate=0;size="+parameters.boardSize+";playouts="+parameters.playouts+";komi="+parameters.komi+";handi="+parameters.han);
    }

}
