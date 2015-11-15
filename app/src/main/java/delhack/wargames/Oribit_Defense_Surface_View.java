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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Oribit_Defense_Surface_View extends SurfaceView implements SensorEventListener{

    Orbital_Thread orbital_thread;
    Paint mPaint;
    Bitmap snowBall;
    boolean posted;
    long startTime;
    Bitmap spaceShip;
    Bitmap orbital_background;// the background
    Bitmap laser;
    DisplayMetrics metrics;
    int score;
    int health;//health is the size of the rectangle that is shown
    int xp;
    float laser_x;
    float laser_y;
    float alien_x;
    float alien_y;
    float space_ship_x;
    float space_ship_y;
    float laser_velocity;
    float space_ship_velocity_x;
    Bitmap alien;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    float accelerometerYStart;//holds y value of spaceShip of when the accelerometer should start working


    public Oribit_Defense_Surface_View(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        SurfaceHolder holder = getHolder();
        metrics = context.getResources().getDisplayMetrics(); // screen details
        orbital_thread = new Orbital_Thread(this);
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        startTime = System.currentTimeMillis();
        alien = BitmapFactory.decodeResource(getResources(), R.drawable.alien_ship);
        alien = Bitmap.createScaledBitmap(alien, metrics.widthPixels / 5, metrics.heightPixels / 5, true);
        spaceShip = BitmapFactory.decodeResource(getResources(), R.drawable.space_ship);
        spaceShip = Bitmap.createScaledBitmap(spaceShip, metrics.widthPixels / 7, metrics.heightPixels / 7, true);
        orbital_background = BitmapFactory.decodeResource(getResources(), R.drawable.orbital_background);
        orbital_background = Bitmap.createScaledBitmap(orbital_background, metrics.widthPixels, metrics.heightPixels, true);
        laser = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
        score = 0;
        health = 0;
        laser_velocity = 20;
        space_ship_velocity_x = 10;
        xp = 0;
        alien_x = 0;
        alien_y = 0 - alien.getHeight();
        laser_x = (alien.getWidth() / 2) - (laser.getWidth() / 2);
        laser_y = alien_y + (float)(alien.getHeight() / 1.5);
        space_ship_x = (metrics.widthPixels / 2) - (spaceShip.getWidth() / 2);
        space_ship_y = metrics.heightPixels + spaceShip.getHeight();
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

    public void detectCollision(){
        System.out.println("space ship height is: " + space_ship_y);
        System.out.println("laser height is: " + (laser_y + laser.getHeight()));
        if((laser_x + laser.getWidth()) >= space_ship_x && (laser_x + laser.getWidth()) <= (space_ship_x + spaceShip.getWidth())
            && ((laser_y + laser.getHeight()) - space_ship_y) >= 15){//then its a collision
            laser_velocity = -20;

        }
    }

    // method to draw everything to the canvas which gets displayed on the screen
    public void myDraw(Canvas canvas){

        accelerometerYStart = (float)(canvas.getHeight() * (6/7.0));

        //draw the background
        canvas.drawBitmap(orbital_background, 0, 0, mPaint);
        mPaint.setTextSize(60);
        mPaint.setColor(Color.WHITE);
        canvas.drawText("Score: " + Integer.toString(score), (float)(canvas.getWidth() * (4/5.0)), canvas.getHeight() / 20, mPaint);
        canvas.drawText("XP: " + Integer.toString(xp), canvas.getWidth() / 20, canvas.getHeight() / 20, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawRect((float)0, (float)(canvas.getHeight() - 10), (float)metrics.widthPixels, (float)(canvas.getHeight()), mPaint);
        canvas.save();
        canvas.restore();
        canvas.drawBitmap(alien, alien_x, alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5)) , alien_y, mPaint);
        canvas.drawBitmap(alien, (float)(alien_x + (canvas.getWidth() / 2.5) * 2), alien_y, mPaint);
        if(alien_y < canvas.getHeight() / 16){
            alien_y += 8;
            laser_y += 8;
        }

        canvas.drawBitmap(spaceShip, space_ship_x, space_ship_y, mPaint);
        if(space_ship_y > accelerometerYStart){
            space_ship_y -= 8;
        }
        else { // now the lasers will start coming faster
            canvas.drawBitmap(laser, laser_x, laser_y, mPaint);
//            canvas.drawBitmap(laser, (float)(laser_x + (canvas.getWidth() / 2.5)), laser_y, mPaint);
//            canvas.drawBitmap(laser, (float)(laser_x + (canvas.getWidth() / 2.5) * 2), laser_y, mPaint);
            laser_y += laser_velocity;
        }
        detectCollision();


















    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            if(space_ship_y <= accelerometerYStart){
                if(x > 0 && space_ship_x > 0){//move left
                    space_ship_x -= space_ship_velocity_x;
                }
                else if(x < 0 && (space_ship_x + spaceShip.getWidth()) < metrics.widthPixels) {//move right
                    space_ship_x += space_ship_velocity_x;
                }
            }



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }






}
