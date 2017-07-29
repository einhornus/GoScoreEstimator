package com.company;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;

public class LeelaConstructor {
    public LeelaResponse[] array;
    public ArrayList<Move> moves;
    public Action<String> done;
    public Action<String> out;
    public Action<double[]> graph;
    public int playoutsLimit = 10000;
    public int times = 1;
    public int threads = 4;
    public int COLLEGE = 2;


    public class Jobber implements Action<LeelaResponse>{
        @Override
        public void doJob(LeelaResponse item) {
            int index = Integer.parseInt(item.index);
            int threadIndex = index % threads;
            out.doJob(index+"/"+array.length);
            array[index] = item;


            boolean isDoneAll = true;
            for(int i = 0; i<array.length; i++){
                if(array[i] == null){
                    isDoneAll = false;
                    break;
                }
            }
            if(isDoneAll){
                String sgf = makeSgf();
                done.doJob(sgf);
                return;
            }

            int newIndex = index+threads;
            if(newIndex < array.length) {
                ArrayList<Move> prev = new ArrayList<>();
                for (int j = 0; j < newIndex; j++) {
                    prev.add(moves.get(j));
                }
                try {
                    LeelaRequest.RequestParameters parameters = new LeelaRequest.RequestParameters();
                    parameters.thread = threadIndex;
                    parameters.moves = moves;
                    parameters.index = newIndex + "";
                    parameters.action = new Jobber();
                    parameters.times = times;
                    LeelaRequest.query(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isTenuki(Move a, Move b){
        if(a instanceof Pass || b instanceof Pass){
            return true;
        }
        NormalMove _a = (NormalMove)a;
        NormalMove _b = (NormalMove)b;
        int sq = (_a.x - _b.x)*(_a.x - _b.x) + (_a.y - _b.y)*(_a.y - _b.y);
        return sq > 25;
    }


    public class Comp implements Comparator<LeelaResponse.Variation>{

        public boolean inverted;
        public Comp(boolean inverted){
            this.inverted = inverted;
        }

        @Override
        public int compare(LeelaResponse.Variation v1, LeelaResponse.Variation v2) {
            int res =  Double.compare(v1.winrate, v2.winrate);
            if(inverted){
                res *= -1;
            }
            return res;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    }

    public String makeSgf(){
        Game game = new Game();



        for(int i = 0; i<array.length-1; i++){
            if(array[i+1].variations.size() > 0){

                List<LeelaResponse.Variation> considering = new ArrayList<>();


                for(int j = 0; j<array[i+1].variations.size(); j++){
                    if(array[i+1].variations.get(j).iterations > playoutsLimit){
                        considering.add(array[i+1].variations.get(j));
                    }
                }

                if(considering.size() > 0) {
                    considering.sort(new Comp((i + 1) % 2 == 0));

                    double sumW = 0;
                    double sum = 0;
                    for (int j = 0; j < Math.min(COLLEGE, considering.size()); j++) {
                        LeelaResponse.Variation var = considering.get(j);
                        double w = (double) var.iterations;
                        double winrate = var.winrate;
                        sum += w * winrate;
                        sumW += w;
                    }

                    double res = sum / sumW;

                    array[i].estimation = res;
                }
            }
        }

        double[] graphArray = new double[array.length];
        for(int i = 0; i<graphArray.length; i++){
            graphArray[i] = array[i].estimation;
        }
        this.graph.doJob(graphArray);

        Position[] positions = new Position[array.length+1];
        positions[0] = new Position();
        for(int i = 1; i<array.length; i++){
            positions[i] = new Position(positions[i-1], moves.get(i-1));
            String comment = "";
            if(array[i-1].estimation != -1){
                comment = String.format("Winrate: %1$.1f\n", array[i-1].estimation);
            }
            if(array[i-1].estimation != -1 && array[i-2].estimation != -1 && i>1){
                double quality = array[i-1].estimation - array[i-2].estimation;
                if(i%2 == 0){
                    quality*=-1;
                }
                comment += String.format("Power  : %1$.1f\n",quality);
            }
            positions[i].comment = comment;

            LeelaResponse resp = array[i-1];

            boolean bestMovePlayed = false;

            if(resp.variations.size() > 0){
                for(int j = 0; j<resp.variations.size(); j++) {
                    Move move = resp.variations.get(j).root;
                    if(!move.toString().equals(moves.get(i-1).toString())) {
                        int iters = resp.variations.get(j).iterations;
                        if (iters > playoutsLimit) {
                            Position ppp = new Position(positions[i - 1], move);
                            if(move.toString().equals(resp.bestMove.toString())){
                                bestMovePlayed = true;
                            }

                            Position prev = ppp;
                            for (int k = 1; k < resp.variations.get(j).pv.size(); k++) {
                                Move prevMove = prev.moves.get(prev.moves.size() - 1);
                                Move newMove = resp.variations.get(j).pv.get(k);
                                Position qqq = new Position(prev, resp.variations.get(j).pv.get(k));
                                prev = qqq;
                                if (isTenuki(prevMove, newMove)) {
                                    break;
                                }
                            }

                            double wr = resp.variations.get(j).winrate;
                            String com = String.format("Winrate: %1$.1f\n",wr);
                            if(array[i-2].estimation != -1 && i>1){
                                double quality = wr - array[i-2].estimation;
                                if(i%2 == 0){
                                    quality*=-1;
                                }
                                com += String.format("Power  : %1$.1f\n",quality);
                            }
                            ppp.comment = com;
                        }
                    }
                }
            }

            if(resp.bestMove.toString().equals(moves.get(i-1).toString())) {
               bestMovePlayed = true;
            }

            if(!bestMovePlayed){
                Position ppp = new Position(positions[i - 1], resp.bestMove);
            }
        }
        game.root = positions[0];
        String res = game.exportToSgf();
        return res;
    }

    public static void shrinkTo(ArrayList list, int newSize) {
        int size = list.size();
        if (newSize >= size) return;
        for (int i = newSize; i < size; i++) {
            list.remove(list.size() - 1);
        }
    }

    public void makeAnalyzerSgf(int threads, int times, int limit, String content, int shrink, Action<double[]> graph, Action<String> done, Action<String> out)throws Exception{
        this.playoutsLimit = limit;
        this.done = done;
        this.out = out;
        this.graph = graph;
        this.times = times;
        this.threads = threads;

        this.moves = Game.getMovesFromSgf(content);
        shrinkTo(this.moves, shrink);
        array = new LeelaResponse[moves.size()];

        int beginIndex = 0;

        ArrayList<ArrayList<Move>> prevs = new ArrayList<>();
        for(int i = 0; i<threads; i++){
            ArrayList<Move> am = new ArrayList<>();
            for(int j = 0; j<i; j++){
                if(moves.size()-1 > j) {
                    am.add(moves.get(j));
                }
            }
            prevs.add(am);
        }

        for(int i = 0; i<threads; i++) {
            if(moves.size() - 1 > i) {
                LeelaRequest.RequestParameters parameters = new LeelaRequest.RequestParameters();
                parameters.thread = i;
                parameters.moves = moves;
                parameters.index = beginIndex + i + "";
                parameters.action = new Jobber();
                parameters.times = 1;
                LeelaRequest.query(parameters);
            }
        }
    }
}
