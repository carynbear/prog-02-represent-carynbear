package com.example.ngan.prog2b;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ngan on 3/5/16.
 */
public class MsgFromPhoneService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MsgFromPhoneService.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private boolean started = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // I can see this fires properly on the Android mobile phone
        Log.d(TAG, "onCreate");
        if(null == mGoogleApiClient) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            Log.v(TAG, "GoogleApiClient created");
        }

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
            Log.v(TAG, "Connecting to GoogleApiClient..");
        }
    }

    ArrayList<Legislator> mLegislators = null;
    String county = null;
    String[] vote = null;
//    static AtomicInteger synced = null;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;

            if ("/newView".equals(path)) {
                Log.d("--DATA FROM PHONE--", "Received county, vote, legislators");
                started = true;
                mLegislators = new ArrayList<Legislator>();
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                vote = new String[]{map.getString("vote1"), map.getString("vote2")};
                county = map.getString("county");
                Log.d("--DATA FROM PHONE--", county);
                Log.d("--DATA FROM PHONE--", vote.toString());

                int numLegislators = map.getInt("countLegislator");
                Log.d("--DATA FROM PHONE--", String.valueOf(numLegislators) + " legislators");

//                MainActivity.mAssets = new HashMap<String, Asset>();
                for (int i = 0; i < numLegislators; i++) {
                    DataMap m = map.getDataMap("legislator" + String.valueOf(i));
                    Legislator mLegislator = new Legislator(m);
//                    MainActivity.mAssets.put(mLegislator.id, Legislator.getAsset(m));
                    mLegislators.add(mLegislator);
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("county", county);
                intent.putExtra("vote", vote);
                intent.putExtra("legislators", mLegislators);
                Log.d("--DATA FROM PHONE--", "Stop: Display on watch");
                started = false;
                startActivity(intent);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        if (started) {
            Log.v(TAG, "cannot be Destroyed");
        }
        Log.v(TAG, "Destroyed");

        if(null != mGoogleApiClient){
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
                Log.v(TAG, "GoogleApiClient disconnected");
            }
        }

        super.onDestroy();
    }
}
