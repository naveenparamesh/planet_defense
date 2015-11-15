package delhack.wargames;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import delhack.wargames.R;

public class Instructions extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructions, menu);
        return true;
    }

}

