package delhack.wargames;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class Oribit_Defense_Surface_View extends SurfaceView implements SensorEventListener{

    Orbital_Thread orbital_thread;
    SoundPool SP; // plays music
    SoundPool SP2; // plays music
    Paint mPaint;
    Bitmap snowBall;
    boolean posted;
    long startTime;
    Bitmap spaceShip;
    Bitmap orbital_background;// the background
    Bitmap laser;
    Bitmap explosion;
    DisplayMetrics metrics;
    float damage_taken;//health is the size of the rectangle that is shown
    int swarm_health;
    float laser_x;
    float laser_y;
    boolean laser1_exists;
    boolean laser2_exists;
    boolean laser3_exists;
    float alien_x;
    float alien_y;
    float space_ship_x;
    float space_ship_y;
    float laser_velocity;
    float space_ship_velocity_x;
    Bitmap alien;
    Bitmap medal;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    float accelerometerYStart;//holds y value of spaceShip of when the accelerometer should start working
    boolean alien_took_shot;
    boolean alien_shot;
    int ship_hit_sound;
    int alien_hit_sound;
    MediaPlayer player;
    int previousRandom;



    public Oribit_Defense_Surface_View(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        SurfaceHolder holder = getHolder();
        metrics = context.getResources().getDisplayMetrics(); // screen details
        orbital_thread = new Orbital_Thread(this);
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        startTime = System.currentTimeMillis();
        explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
        explosion = Bitmap.createScaledBitmap(explosion, metrics.widthPixels, metrics.heightPixels, true);
        medal = BitmapFactory.decodeResource(getResources(), R.drawable.medal);
        medal = Bitmap.createScaledBitmap(medal, metrics.widthPixels, metrics.heightPixels / 2, true);
        alien = BitmapFactory.decodeResource(getResources(), R.drawable.alien_ship);
        alien = Bitmap.createScaledBitmap(alien, metrics.widthPixels / 5, metrics.heightPixels / 5, true);
        spaceShip = BitmapFactory.decodeResource(getResources(), R.drawable.space_ship);
        spaceShip = Bitmap.createScaledBitmap(spaceShip, metrics.widthPixels / 7, metrics.heightPixels / 7, true);
        orbital_background = BitmapFactory.decodeResource(getResources(), R.drawable.orbital_background);
        orbital_background = Bitmap.createScaledBitmap(orbital_background, metrics.widthPixels, metrics.heightPixels, true);
        laser = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
        damage_taken = 1;
        laser_velocity = 20;
        posted = false;
        space_ship_velocity_x = 25;
        swarm_health = 100;
        laser1_exists = false;
        laser2_exists = false;
        laser3_exists = false;
        alien_x = 0;
        alien_y = 0 - alien.getHeight();
        laser_x = (alien.getWidth() / 2) - (laser.getWidth() / 2);
        laser_y = alien_y + (float)(alien.getHeight() / 1.5);
        space_ship_x = (metrics.widthPixels / 2) - (spaceShip.getWidth() / 2);
        space_ship_y = metrics.heightPixels + spaceShip.getHeight();
        alien_took_shot = false;
        alien_shot = false;
        SP = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        ship_hit_sound = SP.load(getContext(), R.raw.rebound, 1);
        player = MediaPlayer.create(getContext(), R.raw.main_game_sound);
        player.setLooping(true);
        player.start();
        previousRandom = 0;

        //get reference to SensorManager
        mSensorManager = (SensorManager)getContext().getSystemService(getContext().SENSOR_SERVICE);
        //Get reference to Accelerometer
        if(null == (mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))){
            ((Activity)getContext()).finish();
        }
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);


        // adds the callback to the class to be called in the given states
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                orbital_thread.setRunning(true);// starts the thread
                orbital_thread.start();

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // destroys thread
                boolean retry = true;
                orbital_thread.setRunning(false);
                while(retry){
                    try{
                        unregisterAccelerometer();
                        orbital_thread.join();
                        retry = false;
                    }
                    catch(InterruptedException e){

                    }
                }
            }
        });

    }

    public void unregisterAccelerometer(){
        mSensorManager.unregisterListener(this);
    }

    public void detectShipCollision(){
//        System.out.println("laser height is: " + (laser_y + laser.getHeight()));
//        System.out.println("space ship height is: " + space_ship_y);
        if((laser_x + laser.getWidth()) >= space_ship_x && (laser_x + laser.getWidth()) <= (space_ship_x + spaceShip.getWidth())
                && (((laser_y + laser.getHeight()) - space_ship_y) >= 15 && ((laser_y + laser.getHeight()) - space_ship_y) <= 100)){//then its a collision
            laser_velocity = -laser_velocity;
            SP.play(ship_hit_sound, 1f, 1f, 0, 0, 1f);
        }

    }

    public void detectAlienShipCollision(Canvas canvas){
//       System.out.println("laser height is: " + (laser_y + laser.getHeight()));
//       System.out.println("alien height is: " + alien_y);
        if((((alien_y + (alien.getHeight() * (5/7.0))) - laser_y) >= 3 && ((alien_y + (alien.getHeight() * (6/7.0))) - laser_y) <= 100)){//then its a collision
                swarm_health -= 10;
                alien_took_shot = false;
                alien_hit_sound = SP.load(getContext(), R.raw.alien_hit, 1);
                SP.play(alien_hit_sound, 1f, 1f, 0, 0, 1f);
                alien_took_shot = false;
        }

    }

    public void randomizeShot(Canvas canvas){
        Random rand = new Random(System.currentTimeMillis());
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        while(randomNum == previousRandom){
            randomNum = rand.nextInt((3 - 1) + 1) + 1;
        }

        laser_x = (alien.getWidth() / 2) - (laser.getWidth() / 2);
        if(randomNum == 1){
            laser_x = (alien.getWidth() / 2) - (laser.getWidth() / 2);
        }
        else if(randomNum == 2){
            laser_x = (float)(laser_x + (canvas.getWidth() / 2.5));
        }
        else {
            laser_x = (float)(laser_x + (canvas.getWidth() / 2.5) * 2);
        }

        int randSpeed = rand.nextInt((3 - 1) + 1) + 1;
        if(randomNum == 1){
            laser_velocity = 20;
        }
        else if(randomNum == 2){
            laser_velocity = 30;
        }
        else {
            laser_velocity = 40;
        }




        //System.out.println("laser_x is: " + laser_x);
        //System.out.println("width is:  " + metrics.widthPixels);
        alien_took_shot = true;
        previousRandom = randomNum;
    }



    // method to draw everything to the canvas which gets displayed on the screen
    public void myDraw(Canvas canvas){


        if(damage_taken <= 1/5.0){
            damage_taken -= (1/5.0);
            mPaint.setTextSize(100);
            canvas.drawBitmap(explosion, 0, 0, mPaint);
            canvas.drawText("GAME OVER", canvas.getWidth() / 3, canvas.getHeight() / 2, mPaint);
            player.stop();

            return;
        }

        if(swarm_health == 0){
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
            canvas.drawBitmap(medal, 0, 0, mPaint);
            mPaint.setColor(Color.BLUE);
            mPaint.setTextSize(125);
            canvas.drawText("YOU WON", (float)(canvas.getWidth() * (2/7.0)), (float)(canvas.getHeight() * (5/6.0)), mPaint);
            player.stop();



//            Intent intent = new Intent(getContext(), SpaceCombat.class);
//            getContext().startActivity(intent);
            return;
        }

        accelerometerYStart = (float)(canvas.getHeight() * (6/7.0));

        //draw the background
        canvas.drawBitmap(orbital_background, 0, 0, mPaint);
        mPaint.setTextSize(60);
        mPaint.setColor(Color.WHITE);
        canvas.drawText("Swarm Health: " + Integer.toString(swarm_health), canvas.getWidth() / 20, canvas.getHeight() / 20, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawRect((float)0, (float)(canvas.getHeight() - 10),(metrics.widthPixels * damage_taken), (float)(canvas.getHeight()), mPaint);
//        canvas.save();
//        canvas.restore();
        canvas.drawBitmap(alien, alien_x, alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5)) , alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5) * 2), alien_y, mPaint);
        if(alien_y < canvas.getHeight() / 16){
            alien_y += 8;
            laser_y += 8;
        }

        canvas.drawBitmap(spaceShip, space_ship_x, space_ship_y, mPaint);
        canvas.drawBitmap(alien, alien_x, alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5)) , alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5) * 2), alien_y, mPaint);
        if(space_ship_y > accelerometerYStart){
            space_ship_y -= 8;

        }
        else { // now the lasers will start coming faster





            canvas.drawBitmap(laser, laser_x, laser_y, mPaint);
            laser_y += laser_velocity;

//            canvas.drawBitmap(laser, (float)(laser_x + (canvas.getWidth() / 2.5)), laser_y, mPaint);
//            canvas.drawBitmap(laser, (float)(laser_x + (canvas.getWidth() / 2.5) * 2), laser_y, mPaint);



        }
        if(laser_y > canvas.getHeight()){
            damage_taken -= (1/ 5.0);
            laser_y = alien_y + (float)(alien.getHeight() / 1.5);
            //System.out.println("laser_y: " + laser_y);
            alien_took_shot = false;//this calls randomize again
        }
        if(!alien_took_shot){
            randomizeShot(canvas);
        }
        detectShipCollision();
        if(laser_y > (alien_y + alien.getHeight()) || laser_velocity < 0) {
            detectAlienShipCollision(canvas);
        }

        //changed code HERE!!!
        if(laser_y <= 0){
            randomizeShot(canvas);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            System.out.println("x is: " + x);
            if(space_ship_y <= accelerometerYStart){
                if(swarm_health < 50){
                    if(x > 0 && space_ship_x > 0){//move left
                        space_ship_x -= space_ship_velocity_x;
                    }
                    else if(x < 0 && (space_ship_x + spaceShip.getWidth()) < metrics.widthPixels) {//move right
                        space_ship_x += space_ship_velocity_x;
                    }
                }
                else {
                    if(x > 0.5 && space_ship_x > 0){//move left
                        space_ship_x -= space_ship_velocity_x;
                    }
                    else if(x < -0.5 && (space_ship_x + spaceShip.getWidth()) < metrics.widthPixels) {//move right
                        space_ship_x += space_ship_velocity_x;
                    }
                }

            }



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }






}
