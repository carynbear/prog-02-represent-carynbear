package com.example.ngan.prog2b;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.github.florent37.davinci.daemon.DaVinciDaemon;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by ngan on 3/5/16.
 */
public class MsgToWatchService extends Service {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        Log.d("--PHONETOWATCH--", "MsgToWatchService created");
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
//                //now that you're connected, send a series of messages
//                sendMessage("/start", "");
//                sendMessage("/county", MainActivity.mAddressOutput);
//                sendMessage("/vote", MainActivity.mVoteInfo[0]+"|" + MainActivity.mVoteInfo[1]);
//                for (Legislator l : MainActivity.mLegislators) {
//                    sendMessage("/legislator", l.toString());
//                }
////                sendMessage("/stop", "");
//
//                Log.d("--SENDING TO WATCH--", "start");
//                 putDataMapReq = PutDataMapRequest.create("/start");
//                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
//                 dataItemResult = Wearable.DataApi.putDataItem(mApiClient, putDataReq).await();

                Log.d("--SENDING TO WATCH--", "county, vote, legislators");
                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/newView");
                putDataMapReq.getDataMap().putString("county", MainActivity.mAddressOutput);
                putDataMapReq.getDataMap().putString("vote1", MainActivity.mVoteInfo[0]);
                putDataMapReq.getDataMap().putString("vote2", MainActivity.mVoteInfo[1]);
                int i = 0;
                for (Legislator l : MainActivity.mLegislators) {
//                    DaVinciDaemon.with(getBaseContext()).load(l.profilePic).into("/image/"+l.id);
                    DataMap legislatorMap = new DataMap();
                    l.putToDataMap(legislatorMap);
                    putDataMapReq.getDataMap().putDataMap("legislator"+String.valueOf(i), legislatorMap);
                    i++;
                }
                putDataMapReq.getDataMap().putInt("countLegislator", MainActivity.mLegislators.size());
                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                DataApi.DataItemResult dataItemResult = Wearable.DataApi.putDataItem(mApiClient, putDataReq).await();
//
//                Log.d("--SENDING TO WATCH--", "legislators");
//
//
//                Log.d("--SENDING TO WATCH--", "stop");
//                putDataMapReq = PutDataMapRequest.create("/done");
//                putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
//                dataItemResult = Wearable.DataApi.putDataItem(mApiClient, putDataReq).await();

            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        Log.d("--PHONETOWATCH--", "sent message to watch "+text);
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}

