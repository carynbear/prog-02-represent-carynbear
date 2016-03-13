package com.example.ngan.prog2b;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
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
            sendMessage("SHAKE");
        } else if( messageEvent.getPath().equalsIgnoreCase(DETAIL_FEED) ) {
            // Value contains the id of the person sent to us to display
            String id = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            sendMessage(id);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }

    private void sendMessage(String id) {
        Log.d("--FROM WATCH SERVICE--", "Broadcasting message");
        Intent intent = new Intent("Received Message from Watch");
        // You can also include some extra data.
        intent.putExtra("id", id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
