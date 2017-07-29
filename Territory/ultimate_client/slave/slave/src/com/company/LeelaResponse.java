package com.company;


import java.util.ArrayList;
import java.util.List;

public class LeelaResponse {
    public static class Variation{
        public Move root = new Pass();
        public double winrate;
        public int iterations;
        public ArrayList<Move> pv;
    }

    public String index;
    public Move bestMove;
    public double estimation = -1;
    public List<Variation> variations = new ArrayList<>();
}
