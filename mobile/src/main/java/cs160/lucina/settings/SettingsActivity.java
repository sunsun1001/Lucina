package cs160.lucina.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.View;

import cs160.lucina.R;

/**
 * Created by M117 on 4/18/16.
 */
public class SettingsActivity extends Activity {


    GestureDetectorCompat mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final SettingsActivity thisActivity = this;

        View screen = findViewById(R.id.settings);
    }

}
