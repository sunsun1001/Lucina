package cs160.lucina;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cs160.lucina.onboarding.OnboardSignupActivity;
import cs160.lucina.settings.SettingsActivity;


/**
 * Created by M117 on 4/18/16.
 */
public class HomeActivity extends Activity {

    DataLayer dataLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeStyle();

        dataLayer = DataLayer.getInstance(this);
        /*if (dataLayer.isFirstOpen()) {
            Intent intent = new Intent(this, OnboardSignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/

        String name = dataLayer.getChildName();

        TextView nursery = (TextView) findViewById(R.id.nursery_text);
        TextView data = (TextView) findViewById(R.id.data_text);

        nursery.setText("Listen for crying while " + name + " is awake.");
        data.setText("View trends in " + name + "\'s sleeping pattern.");


        setUpButtons();
    }

    private void initializeStyle() {
        Typeface blogger = Typeface.createFromAsset(getAssets(), "fonts/Blogger_Sans.ttf");
    }

    private void setUpButtons() {
        final HomeActivity thisActivity = this;
        final Resources res = getResources();

        FrameLayout cribMonitorButton = (FrameLayout) findViewById(R.id.crib_monitor);
        cribMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcast = new Intent();
                broadcast.setAction("ASLEEP");
                Intent nextScreenIntent = new Intent(thisActivity, CribMonitorActivity.class);
                Intent toWatchCrib = new Intent(getBaseContext(), PhoneToWatchService.class);
                toWatchCrib.putExtra("message", res.getString(R.string.start_crib_mode));
                thisActivity.startService(toWatchCrib);
                thisActivity.startActivity(nextScreenIntent);
                sendBroadcast(broadcast);
            }
        });

        FrameLayout nurseryMonitorButton = (FrameLayout) findViewById(R.id.nursery_monitor);
        nurseryMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreenIntent = new Intent(thisActivity, NurseryMonitorActivity.class);
                Intent toWatchNursery = new Intent(getBaseContext(), PhoneToWatchService.class);
                toWatchNursery.putExtra("message", res.getString(R.string.start_nursery_mode));
                thisActivity.startService(toWatchNursery);
                thisActivity.startActivity(nextScreenIntent);
            }
        });

        FrameLayout exploreDataButton = (FrameLayout) findViewById(R.id.explore_data);
        exploreDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreenIntent = new Intent(thisActivity, AllDataActivity.class);
                thisActivity.startActivity(nextScreenIntent);
            }
        });
    }

}