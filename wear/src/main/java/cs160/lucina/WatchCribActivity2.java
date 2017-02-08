package cs160.lucina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.Random;

/*
 Back up WatchCribActivity2 by Sunny.
 */

public class WatchCribActivity2 extends Activity {

    private static final String LOG_TAG = "AudioRecordTest";
    // filename
    private static String mFileName = "sleep_record";
    private MediaRecorder mRecorder = null;

    public WatchCribActivity2() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
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



    SensorManager mSensorManager;
    float mAccel;
    float mAccelCurrent;
    float mAccelLast;
    float threshold = 20;

    final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > threshold) {
                Toast toast = Toast.makeText(getApplicationContext(), "Device has shaken.", Toast.LENGTH_LONG);
                toast.show();

                Log.v("T", "Shaking");

                /* Tell phone to do a new search */
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("command", "new");
                startService(sendIntent);

                Log.v("T", "Created shaking Intent");
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_crib);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final boolean isAsleep = true;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                final ViewFlipper mViewFlipper = (ViewFlipper) findViewById(R.id.crib_view_flipper);
                mViewFlipper.setDisplayedChild(0);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            mViewFlipper.setDisplayedChild(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mViewFlipper.setDisplayedChild(2);
                            //TODO: move this to the right place after adding sound monitoring
                            Intent toAlert = new Intent(WatchCribActivity2.this, WatchAlertActivity.class);
                            toAlert.putExtra("alert_type", getResources().getString(R.string.SLEEP));
                            startActivity(toAlert);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 8000);

                //TODO: start recording sounds and give alert
                //while baby is still sleeping
                while(isAsleep){
                    onRecord(true);

                    //should pause for 5 secs
                    try{
                        Thread.sleep(5000);
                    }catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                    }

                    onRecord(false);
                }

            }
        });
    }
}
