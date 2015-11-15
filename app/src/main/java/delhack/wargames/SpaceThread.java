package delhack.wargames;

import android.graphics.Canvas;
import android.media.SoundPool;

public class SpaceThread extends Thread{

    MySpaceView spaceView;
    private boolean running = false; // if the thread is running or not
    public long sleepMillis = 20; // determines fps of game, the lower the better
    SoundPool SP; // plays music
    int sound_hit; // sound of item hit
    Canvas canvas; // the canvas


    public SpaceThread(MySpaceView spaceView){
        super();
        this.spaceView = spaceView;

    }


    // sets thread running to true
    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run(){
        while(running){

            // basically locks canvas and posts it
            // only if it hasn't been done already
            if(!spaceView.posted){
                canvas = spaceView.getHolder().lockCanvas();

            }
            else {
                spaceView.posted = false;
            }

            if(canvas != null){
                synchronized (spaceView.getHolder()){
                    spaceView.myDraw(canvas);
                }
                if(!spaceView.posted){
                    spaceView.getHolder().unlockCanvasAndPost(canvas);
                }



            }

            try{
                sleep(sleepMillis); // makes thread wait before drawing again
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }


        }
    }
}
