package cs160.lucina;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


public class PhoneListenerService extends WearableListenerService implements DataApi.DataListener {

    private static final String LOG_TAG = "PhoneListenerService";
    private static final String AWAKE = "awake";
    private static final String CRYING = "crying";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(LOG_TAG, "=================== Data Changed ==================== ");
        DataLayer dataLayer = DataLayer.getInstance(this.getApplicationContext());

        // connect the api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(LOG_TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        // receive data from data event
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                Log.d(LOG_TAG, "Got data");

                if (item.getUri().getPath().equals("/cribmode")) {
                    Log.d(LOG_TAG, "got images");
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    long wakeTimestamp = dataMapItem.getDataMap().getLong("wake_timestamp");
                    dataLayer.setLastAwakeTime(wakeTimestamp);

                    sendNotification(AWAKE);

                    Intent awakeIntent = new Intent();
                    awakeIntent.setAction("AWAKE");
                    sendBroadcast(awakeIntent);

                } else if (item.getUri().getPath().equals("/nurserymode")) {
                    // for stuff received when crying

                } else {
                    // for other stuff received
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    private void sendNotification(String message) {

        NotificationCompat.Builder mBuilder = null;
        DataLayer dataLayer = DataLayer.getInstance(this.getApplicationContext());
        String childName = dataLayer.getChildName();

        if (message.equals(AWAKE)) {
            mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.crib)
                        .setContentTitle("Lucina")
                        .setContentText(childName + " is awake!");
        } else if (message.equals(CRYING)) {
            mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sun)
                        .setContentTitle("Lucina")
                        .setContentText(childName + " is crying!");
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }


    /*
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());

        //code to receive fake message and show a Toast to prove the connection works

        if (messageEvent.getPath().equalsIgnoreCase("/message") ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            // Make a toast with the String
            Context context = getApplicationContext();
            Resources res = getResources();
            int duration = Toast.LENGTH_SHORT;

            Toast toast;

            if (value.equals(res.getString(R.string.SLEEP))) {
                toast = Toast.makeText(context, "Baby is awake!!!", duration);
            } else {
                toast = Toast.makeText(context, "Baby is crying!!!", duration);
            }

            toast.show();

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
    */

}
