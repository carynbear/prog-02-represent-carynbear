package com.example.ngan.prog2b;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by ngan on 3/5/16.
 */
public class MsgFromPhoneService extends WearableListenerService {
    private static final String LOC1_FEED = "/1";
    private static final String LOC2_FEED = "/2";
    private static final String LOC3_FEED = "/3"; //using your location


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases

        if( messageEvent.getPath().equalsIgnoreCase( LOC1_FEED ) ) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("LOCATION", 1);
            Log.d("T", "about to start watch MainActivity with LOCATION OPTION: 1");
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase( LOC2_FEED )) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("LOCATION", 2);
            Log.d("T", "about to start watch MainActivity with LOCATION OPTION: 2");
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase( LOC3_FEED )) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("LOCATION", 3);
            Log.d("T", "about to start watch MainActivity with LOCATION OPTION: 3");
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
