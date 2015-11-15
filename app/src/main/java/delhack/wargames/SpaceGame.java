package delhack.wargames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SpaceGame extends Activity {
    // basically old game, but horizontal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_game);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
