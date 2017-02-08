package cs160.lucina;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService {
    private static final String PATH = "/message";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());

        Resources res = getResources();

        if ( messageEvent.getPath().equalsIgnoreCase(PATH) ) {

            String message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            if (message.equals(res.getString(R.string.start_crib_mode))) {
                Intent intent = new Intent(this, WatchCribActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //you need to add this flag since you're starting a new activity from a service
                Log.d("T", "about to start WatchCribActivity");
                startActivity(intent);
            } else if (message.equals(res.getString(R.string.start_nursery_mode))) {
                Intent intent = new Intent(this, WatchNurseryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //you need to add this flag since you're starting a new activity from a service
                Log.d("T", "about to start WatchNurseryActivity");
                startActivity(intent);
            } else if (message.equals("stop")) {
                Intent intent = new Intent(this, WatchMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

       } else {
            super.onMessageReceived( messageEvent );
       }

    }

}