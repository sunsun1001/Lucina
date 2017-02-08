package cs160.lucina;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class WatchAlertActivity extends Activity {

    private TextView mTextView;
    private final String LOG_TAG = "WatchAlertActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_alert);
        final Intent fromMode = getIntent();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                final View vAlertScreen = (View) findViewById(R.id.alert_screen);
                final Resources res = getResources();
                String alertType = fromMode.getStringExtra("alert_type");
                int background = 0;
                if (alertType.equals(res.getString(R.string.SLEEP))) {
                    background = R.color.whiteBakc;
                } else if (alertType.equals(res.getString(R.string.CRY))) {
                    background = R.color.darkBlueText;
                }
                vAlertScreen.setBackgroundColor(res.getColor(background));

                Animation animation = new AlphaAnimation(1, 0); // Change alpha

                animation.setDuration(1000); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at

                vAlertScreen.startAnimation(animation);

                //fake message to Phone to show the connection works
                Intent toPhone = new Intent(WatchAlertActivity.this, WatchToPhoneService.class);
                toPhone.putExtra("message", alertType);
                startService(toPhone);

                //fake interaction to go back to start screen
                vAlertScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toStartScreen = new Intent(WatchAlertActivity.this, WatchMainActivity.class);
                        startActivity(toStartScreen);
                    }
                });

            }
        });
    }
}
