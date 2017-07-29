package com.company;

import java.io.IOException;

public abstract class ThreadedJob {
    protected Thread thread = null;
    protected boolean done = false;

    public void threadFunction()throws IOException{
        while(true){
            if(done){
                break;
            }
            heart();
        }
    }

    public abstract void stop();

    public abstract void heart()throws IOException;

    public void start() throws IOException {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Run();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void Run()throws IOException {
        threadFunction();
    }
}