package com.company;


import java.io.IOException;

public class LeelaBotMain {
    public static void main(String[] args)throws IOException{
        System.out.println("I am Leela, nice to meet you");
        String pass = "anyuta_zhavoronkova";


        Bot leela1 = new Bot("leela_robot1", pass, "go", "board_size-19|handi-1|time-b3600#60#5", false);
        Bot leela6 = new Bot("leela_robot2", pass, "one-color-go", "board_size-19|handi-1|time-b3600#60#5", false);
        Bot leela7 = new Bot("leela_robot3", pass, "blind-go", "board_size-19|handi-1|time-b3600#60#5", false);
        //Bot leela10 = new Bot("leela4", pass, "hidden-move-go", "board_size-9|handi-0|time-b3600#60#5", false);


        /*
        for(int i = 0; i<10; i++){
            if(i%2 == 0) {
                Bot leela1 = new Bot("leela" + i, pass, "go", "board_size-19|handi-0|time-b3600#60#5", true);
            }
            else{
                Bot leela1 = new Bot("leela" + i, pass, "go", "board_size-19|handi-0|time-b3600#60#5", false);
            }
        }
        */

        SlaveThreadedJob slave = new SlaveThreadedJob(0);
        slave.start();
    }
}
