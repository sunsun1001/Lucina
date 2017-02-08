package cs160.lucina;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class PhoneToWatchService extends Service {

    private final static String LOG_TAG = "PhoneToWatchService";
    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "################ On Create ###############");
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
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
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // The intent passed into PhoneToWatchService should have Extra {"message": <msg>},
        // such as {"message": "START CRIB MODE"}
        // The available messages are in string.xml
        if (intent == null) {
            return START_STICKY;
        }
        final String message = intent.getStringExtra("message");
        Log.d(LOG_TAG, "get message: " + message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                // WARNING: REMEMBER TO SET THE CORRECT PATH IN WatchListenerService
                sendMessage("/message" , message);
            }
        }).start();

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                Log.d(LOG_TAG, "send: " + text);
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

}
