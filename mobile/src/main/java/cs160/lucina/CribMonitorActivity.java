package cs160.lucina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CribMonitorActivity extends MonitorActivity {

    private final static String LOG_TAG = "CribMonitorActivity";

    View screen;
    TextView headerText;
    DataLayer dataLayer;
    String childName;
    boolean showingAsleep = true;

    BroadcastReceiver asleepReceiver, awakeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.screen = findViewById(R.id.monitor_screen);
        this.headerText = (TextView) findViewById(R.id.header_text);
        dataLayer = DataLayer.getInstance(this.getApplicationContext());
        childName = dataLayer.getChildName();
        dataLayer.setCribModeStart(System.nanoTime());

        setUpReceivers();
        setUpButtons();

        setAsleep();

    }

    void setUpButtons() {
        super.setUpButtons();
        final CribMonitorActivity thisActivity = this;
        final FrameLayout stopMonitorButton = (FrameLayout) findViewById(R.id.stop_monitor);
        final MediaPlayer mp = MediaPlayer.create(CribMonitorActivity.this, R.raw.crying);



        stopMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Log.d(LOG_TAG, "stopping crib mode");
                long lastAwakeTime = dataLayer.getLastAwakeTime();
                long startTime = dataLayer.getCribModeStartTime();

                Calendar cal = Calendar.getInstance();

                // insert a fake duration
                //float duration = TimeUnit.NANOSECONDS.convert(lastAwakeTime - startTime, TimeUnit.SECONDS) / 3600f;
                float duration = 2.5f;


                Log.d(LOG_TAG, "last duration: " + Float.toString(duration));

                // for testing: remove the following line before real run
                dataLayer.sleepDataDb.delete(SleepDataContract.SleepData.TABLE_NAME, null, null); //clear db
                dataLayer.dbHelper.insertSleepDuration(
                        dataLayer.sleepDataDb,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.DAY_OF_MONTH),
                        duration);

                dataLayer.setCribModeStop();

                // stop app on watch
                Intent stopWatch = new Intent(thisActivity, PhoneToWatchService.class);
                stopWatch.putExtra("message", "stop");
                startService(stopWatch);

                Intent intent = new Intent(thisActivity, AllDataActivity.class);
                // TODO: add highlight to intent
                intent.putExtra("FROM_CRIB", true);
                startActivity(intent);
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
                        CribMonitorActivity.this.runOnUiThread(new Runnable() {
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

    // TODO: set up this activity to use a broadcast received to hear when the audio data is ready
    public void setUpReceivers() {
        final CribMonitorActivity thisActivity = this;
        asleepReceiver = new BroadcastReceiver() {
                                  @Override
                                  public void onReceive(Context context, Intent intent) {
                                      thisActivity.setAsleep();
                                  }
                              };
        this.registerReceiver(asleepReceiver, new IntentFilter("ASLEEP"));

        awakeReceiver = new BroadcastReceiver() {
                                  @Override
                                  public void onReceive(Context context, Intent intent) {
                                      thisActivity.setAwake();
                                  }
                              };
        this.registerReceiver(awakeReceiver, new IntentFilter("AWAKE"));
    }

    protected void setAsleep() {
        showingAsleep = true;
        screen.setBackground(ContextCompat.getDrawable(this, R.drawable.crib_monitor_asleep));
        headerText.setText(childName + " is asleep");
    }

    protected void setAwake() {
        showingAsleep = false;
        screen.setBackground(ContextCompat.getDrawable(this, R.drawable.crib_monitor_awake));
        headerText.setText(childName + " is awake");
    }

    @Override
    public void onBackPressed() {
        this.unregisterReceiver(awakeReceiver);
        this.unregisterReceiver(asleepReceiver);
        super.onBackPressed();
    }

}
