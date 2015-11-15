package delhack.wargames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySpaceView extends SurfaceView implements SensorEventListener {


    SpaceThread spaceThread;
    boolean posted;
    Bitmap missle;
    Bitmap alien;
    float missle_x, missle_y;
    float alien_x, alien_y;
    DisplayMetrics metrics;


    public MySpaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        spaceThread = new SpaceThread(this);
        posted = false;
        missle = BitmapFactory.decodeResource(getResources(), R.drawable.missle);
        alien = BitmapFactory.decodeResource(getResources(), R.drawable.alien_ship2);
        missle_x = metrics.widthPixels / 2 - (missle.getWidth() / 2);
        missle_y = metrics.heightPixels / 2;


        SurfaceHolder holder = getHolder();

        // adds the callback to the class to be called in the given states
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                spaceThread.setRunning(true);// starts the thread
                spaceThread.start();

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // destroys thread
                boolean retry = true;
                spaceThread.setRunning(false);
                while (retry) {
                    try {
                        //unregisterAccelerometer();
                        spaceThread.join();
                        retry = false;
                    } catch (InterruptedException e) {

                    }
                }
            }
        });

    }




    // method to draw everything to the canvas which gets displayed on the screen
    public void myDraw(Canvas canvas) {



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            float x = event.values[0];
//            if (space_ship_y <= accelerometerYStart) {
//                if (x > 0 && space_ship_x > 0) {//move left
//                    space_ship_x -= space_ship_velocity_x;
//                } else if (x < 0 && (space_ship_x + spaceShip.getWidth()) < metrics.widthPixels) {//move right
//                    space_ship_x += space_ship_velocity_x;
//                }
//            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

}