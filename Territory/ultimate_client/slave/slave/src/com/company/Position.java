package com.company;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.TreeMap;

public class Position {
    public static class Child{
        Move move;
        Position position;

        public Child(Move move, Position position){
            this.move = move;
            this.position = position;
        }
    }

    public ArrayList<Move> moves = new ArrayList<>();
    public Position parent;
    public ArrayList<Child> children = new ArrayList<Child>();
    public String comment = "";

    public Position(){

    }

    public Position(Position parent, Move move){
        this.parent = parent;
        for(int i = 0; i<parent.moves.size(); i++){
            moves.add(parent.moves.get(i));
        }
        this.moves.add(move);
        this.parent.children.add(new Child(move, this));
    }
}
