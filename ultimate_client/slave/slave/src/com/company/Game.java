package com.company;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    public String blackRank;
    public String whiteRank;
    public String blackName;
    public String whiteName;
    public Position root = new Position();

    public void init(String sgfContent){

    }

    public String encodeMove(Move move){
        if(move instanceof Pass){
            return "";
        }
        else{
            NormalMove normal = (NormalMove)move;
            return (char)(normal.x+'a')+""+(char)(normal.y+'a');
        }
    }


    public static Move decodeMove(String str)throws ParseException{
        if(str.length() == 0){
            return new Pass();
        }
        if(str.length() == 2){
            int x = (int)(str.charAt(0) - 'a');
            int y = (int)(str.charAt(1) - 'a');
            return new NormalMove(x, y);
        }
        throw new ParseException(str, 0);
    }

    public String writePosition(Position position, boolean brackets){
        String res = "";
        if(brackets){
            res += "(";
        }

        if(position.moves.size() > 0) {
            res += ";";
            Move move = position.moves.get(position.moves.size() - 1);
            if (position.moves.size() % 2 == 1) {
                res += "B[" + encodeMove(move) + "]";
            } else {
                res += "W[" + encodeMove(move) + "]";
            }
            if(position.comment != ""){
                res += "C["+position.comment+"]";
            }
        }
        if(position.children.size() == 1){
            for(Position.Child ch : position.children){
                Position next = ch.position;
                String text = writePosition(next, false);
                res += text;
            }
        }
        if(position.children.size() > 1){
            for(Position.Child ch : position.children){
                Position next = ch.position;
                String text = writePosition(next, true);
                res += text;
            }
        }


        //if(position.comment != ""){
            //res += "C["+position.comment+"]";
        //}
        if(brackets){
            res += ")";
        }
        return res;
    }

    public String exportToSgf(){
        String res = "(";
        res += ";FF[4]GM[1]SZ[19]\n" +
                " PB["+blackName+"]\n" +
                " HA[0]\n" +
                " PW["+whiteName+"]\n" +
                " KM[7.5]\n" +
                " RU[Chinese]";
        String text = writePosition(root, false);
        res += text;
        res+=")";
        return res;
    }


    public static ArrayList<Move> getMovesFromSgf(String content) throws ParseException{
        ArrayList<Move> res = new ArrayList<>();
        String cleaned = "";
        for(int i = 0; i<content.length(); i++){
            char c = content.charAt(i);
            if(c == ')'){
                break;
            }
            cleaned += c;
        }

        while(cleaned.contains(";B[") || cleaned.contains(";W[")){
            int bIndex = cleaned.indexOf(";B[");
            int wIndex =  cleaned.indexOf(";W[");

            if(bIndex == -1){
                bIndex = Integer.MAX_VALUE;
            }

            if(wIndex == -1){
                wIndex = Integer.MAX_VALUE;
            }

            if(bIndex < wIndex){
                cleaned = cleaned.substring(bIndex + 3);
            }
            else{
                cleaned = cleaned.substring(wIndex + 3);
            }

            if(cleaned.contains("]")){
                int index = cleaned.indexOf("]");
                String moveString = cleaned.substring(0, index);
                Move mv = decodeMove(moveString);
                res.add(mv);
            }
        }

        return res;
    }

}
