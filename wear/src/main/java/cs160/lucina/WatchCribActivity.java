package cs160.lucina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;

public class WatchCribActivity extends Activity implements SensorEventListener {

    private static final String LOG_TAG = "WatchCribActivity";

    // filename
    private static String mFileName = "sleep_record";
    private MediaRecorder mRecorder = null;
    private long startTime;

    SensorManager mSensorManager;
    float mAccel;
    float mAccelCurrent;
    float mAccelLast;
    float threshold = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        setContentView(R.layout.activity_watch_crib);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Automatically switch watch faces

                final TextView smallText = (TextView) findViewById(R.id.place);
                smallText.setText("place watch in crib");

                final Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(900); //You can manage the time of the blink with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                smallText.startAnimation(anim);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            anim.cancel();
                            smallText.setText("is listening...");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 6000);

            }
        });
    }

    //signals for actual recording
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    //starts sound recording and sets it up to be saved
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    //ends recording and closes and saves it
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override

    public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if (mAccel > threshold) {
            // Toast is for debug purposes
            //Toast toast = Toast.makeText(getApplicationContext(), "Device has shaken.", Toast.LENGTH_LONG);
            //toast.show();

            Log.v("T", "Shaking");

            /* Tell phone to do a new search */
            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
            sendIntent.putExtra("message", "awake");
            sendIntent.putExtra("wake_timestamp", System.nanoTime());
            startService(sendIntent);

            Log.v("T", "Created shaking Intent");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

}
