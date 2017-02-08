package cs160.lucina;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class NurseryMonitorActivity extends MonitorActivity {

    View screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        screen = findViewById(R.id.monitor_screen);

        setUpButtons();
    }

    void setUpButtons() {
        super.setUpButtons();
        final MonitorActivity thisActivity = this;
        final FrameLayout stopMonitorButton = (FrameLayout) findViewById(R.id.stop_monitor);
        final MediaPlayer mp = MediaPlayer.create(NurseryMonitorActivity.this, R.raw.crying);

        stopMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                thisActivity.finish();
            }
        });
        final FloatingActionButton listen_button = (FloatingActionButton) findViewById(R.id.listen_button);

        listen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen_button.setEnabled(false);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mp.start();
//                        listen_button.setEnabled(true);
//                    }
//                }, 5500);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        NurseryMonitorActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listen_button.setEnabled(true);
                                mp.start();


                            }
                        });
                    }
                }).start();


            }
        });
    }


//    @Override
//    public void onBackPressed() {
//        mp.stop();
//        super.onBackPressed();
//    }

}
