package delhack.wargames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SpaceCombat extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_combat);
        listenForButton();
    }

    void listenForButton(){
        Button button = (Button) findViewById(R.id.startcombat_button);
        final Intent intent = new Intent(this, SpaceGame.class);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
