package com.company;


public abstract class Move{
    public static char[] chars =  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'};
    public static Move parse(String move){
        if (move.equals("resign")){
            return new Resignation();
        }
        if (move.equals("pass")){
            return new Pass();
        }
        else{
            int x = 0;
            for(int i = 0; i<chars.length; i++){
                if (move.charAt(0) == chars[i]){
                    x = i;
                }
            }
            int y = Integer.parseInt(move.substring(1))-1;
            return new NormalMove(x, y);
        }
    }
}


class Pass extends Move{
    @Override
    public String toString(){
        return "pass";
    }

    public int compareTo(Move other) {
        return Integer.compare(0, 0);
    }
}

class NormalMove extends Move{
    public int x;
    public int y;
    public NormalMove(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return chars[x]+""+(y+1)+"";
    }
}

class Resignation extends Move{
    @Override
    public String toString(){
        return "resign";
    }
}