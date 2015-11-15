package delhack.wargames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity  {


    Context context;
    SoundPool SP; // plays music
    int background_music; // sound of item hit
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        player = MediaPlayer.create(this, R.raw.main_menu_sound);
        player.setLooping(true);
        player.start();


        listenForButton();
        context = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void alertToExit(){
        // build alert
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Exit");
        alert.setMessage("Are you sure you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    // if yes, finish
                    public void onClick(DialogInterface dialog, int id){
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        // if no, go back
                        dialog.cancel();
                    }
                });
        // create and show
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    void listenForButton(){
        Button startButton = (Button) findViewById(R.id.start_button);
        Button instructionsButton = (Button) findViewById(R.id.instructions_button);
        Button exitButton = (Button) findViewById(R.id.exit_button);
        final Intent startGame = new Intent(this, GameActivity.class); // we changed from GameActivity
        final Intent instructions = new Intent(this, Instructions.class);

        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                SP.stop(background_music);
//                SP.release();
                player.stop();
                player.release();

                startActivity(startGame);
            }
        });

        instructionsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){startActivity(instructions);
            }
        });



        exitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                alertToExit();
            }
        });
    }

    @Override
    public void onBackPressed(){
        alertToExit();
    }


}
