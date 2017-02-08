package cs160.lucina;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WatchToPhoneService extends Service {

    private final static String LOG_TAG = "WatchToPhoneService";
    private GoogleApiClient mWatchApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
            .addApi( Wearable.API )
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                }

                @Override
                public void onConnectionSuspended(int cause) {
                }
            })
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "started");
        /*
        Now we send fake messages that toggles a Toast on the phone
        to prove that the connection is established;
        In the future this should be replaced with code that sends
        the proper message and sound snippet.
        */

        final String message = intent.getStringExtra("message");
        if (message.equals("awake")) {
            final long wakeTimestamp = intent.getLongExtra("wake_timestamp", -1);
            new Thread(new Runnable() {
               @Override
               public void run() {
                   //first, connect to the apiclient
                   mWatchApiClient.connect();
                   sendAwakeMessage(mWatchApiClient, wakeTimestamp);
                   Log.d(LOG_TAG, "awake message sent");
               }
            }).start();
        } else if (message.equals("crying")) {
            // for crying alert to send
            sendCryingMessage(mWatchApiClient);
        } else {
            // for other stuff to send
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }

    private void sendAwakeMessage(GoogleApiClient mApiClient, long wakeTimestamp) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/cribmode");
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();

        DataMap data = new DataMap();
        data.putLong("wake_timestamp", wakeTimestamp);

        putDataRequest.setData(data.toByteArray());

        putDataRequest.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mApiClient, putDataRequest);

    }

    private void sendCryingMessage(GoogleApiClient mApiClient) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/nurserymode");
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();

        DataMap data = new DataMap();
        // TODO: put data needed when baby is crying

        putDataRequest.setData(data.toByteArray());

        putDataRequest.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mApiClient, putDataRequest);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mWatchApiClient ).await();
                Log.d(LOG_TAG, Integer.toString(nodes.getNodes().size()));
                for (Node node : nodes.getNodes()) {
                    Log.d(LOG_TAG, "sendMessage: " + path + ", " + text);
                    Wearable.MessageApi.sendMessage(
                            mWatchApiClient, node.getId(), path, text.getBytes());
                }

            }
        }).start();
    }

}
