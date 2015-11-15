package delhack.wargames;


import android.graphics.Canvas;
import android.media.SoundPool;

public class Orbital_Thread extends Thread {


    Oribit_Defense_Surface_View orbitalSurfaceView; // the surface view of the game
    private boolean running = false; // if the thread is running or not
    public long sleepMillis = 20; // determines fps of game, the lower the better
    SoundPool SP; // plays music
    int sound_hit; // sound of item hit
    Canvas canvas; // the canvas


    public Orbital_Thread(Oribit_Defense_Surface_View orbitalSurfaceView){
        super();
        this.orbitalSurfaceView = orbitalSurfaceView;

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
            if(!orbitalSurfaceView.posted){
                canvas = orbitalSurfaceView.getHolder().lockCanvas();

            }
            else {
                orbitalSurfaceView.posted = false;
            }

            if(canvas != null){
                synchronized (orbitalSurfaceView.getHolder()){
                    orbitalSurfaceView.myDraw(canvas);
                }
                if(!orbitalSurfaceView.posted){
                    orbitalSurfaceView.getHolder().unlockCanvasAndPost(canvas);
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
