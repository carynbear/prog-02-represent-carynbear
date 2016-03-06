package com.example.ngan.prog2b;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by ngan on 3/5/16.
 */
public class MsgFromWatchService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String SHAKE = "/s";
    private static final String DETAIL_FEED = "/d";
    private static final String NEW_LOCATION_OPTION = "NEW_LOCATION";
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(SHAKE) ) {
            Intent intent = new Intent(this, MainActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("DISPLAY", NEW_LOCATION_OPTION);
            Log.d("T", "about to start watch MainActivity with NEW LOCATION");
            startActivity(intent);
        } else if( messageEvent.getPath().equalsIgnoreCase(DETAIL_FEED) ) {
            // Value contains the name of the person sent to us to display
            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("DISPLAY", name);
            Log.d("T", "about to start watch MainActivity with DETAIL VIEW of: "+ name);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
