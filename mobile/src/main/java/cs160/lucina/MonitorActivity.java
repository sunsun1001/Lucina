package cs160.lucina;

import android.animation.FloatArrayEvaluator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Brian on 4/25/16.
 */
public class MonitorActivity extends Activity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

        setUpButtons();
        setUpReceivers();
        setUpStyle();
    }

    void setUpButtons() {
        final MonitorActivity thisActivity = this;

        final FloatingActionButton listenButton = (FloatingActionButton) findViewById(R.id.listen_button);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent listenIntent = new Intent(thisActivity, PhoneToWatchService.class);
                listenIntent.putExtra("message", "LISTEN");
                startService(listenIntent);
            }
        });
    }

    public void setUpReceivers() {
        final MonitorActivity thisActivity = this;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DataLayer dataLayer = DataLayer.getInstance(thisActivity);
                int[] recording = dataLayer.getLastRecording();
                thisActivity.playRecording(recording);
            }
        };
        this.registerReceiver(receiver, new IntentFilter("AUDIO_READY"));
    }

    public void setUpStyle() {
        TextView header = (TextView) findViewById(R.id.header_text);
        Typeface blogger = Typeface.createFromAsset(getAssets(), "fonts/Blogger_Sans.ttf");
        header.setTypeface(blogger);
    }

    @Override
    public void finish() {
        this.unregisterReceiver(receiver);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        this.unregisterReceiver(receiver);
        super.onBackPressed();
    }

    protected void playRecording(int[] recording) {
        return;
    }

}
