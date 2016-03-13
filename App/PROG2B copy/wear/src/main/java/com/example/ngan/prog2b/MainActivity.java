package com.example.ngan.prog2b;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener{
//        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView;
    private int option = 2;
    static HashMap<String, Drawable> repsPhotos = new HashMap<String, Drawable>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final float SHAKE_THRESHOLD = 3.25f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 10000;
    private long mLastShakeTime;
//    static HashMap<String, Bitmap> backgrounds;
//    static HashMap<String, Asset> mAssets;

    ArrayList<Legislator> mLegislators = null;
    String county = null;
    String[] vote = null;
//    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });

//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
//            MainActivity.backgrounds = new HashMap<String, Bitmap>();
            county = extras.getString("county");
            vote = extras.getStringArray("vote");
            mLegislators = (ArrayList<Legislator>) extras.getSerializable("legislators");
//            mGoogleApiClient.connect();
        }

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setConsumeWindowInsets(false);
        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(pager);
        pager.setAdapter(new PageAdapter(this,
                getFragmentManager(),
                county,
                vote,
                mLegislators
        ));

    }

    public void sendIDToPhone(String id) {
        Log.d("--WATCHTOPHONE--", "Creating intent with id: " + id);
        Intent sendIntent = new Intent(getBaseContext(), MsgToPhoneService.class);
        sendIntent.putExtra("ACTION",id);
        startService(sendIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
//                Log.d("--SHAKE--", "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Log.d("--SHAKE--", "Shake, Rattle, and Roll");
                    //TODO:WATCHTOPHONE
                    Log.d("--WATCHTOPHONE--", "Creating intent to start toPhoneService");
                    Intent sendIntent = new Intent(getBaseContext(), MsgToPhoneService.class);
                    sendIntent.putExtra("ACTION", "shake");
                    startService(sendIntent);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return; //do-nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        for (String id : mAssets.keySet()) {
//            try {
//                backgrounds.put(id, loadBitmapFromAsset(mAssets.get(id), mGoogleApiClient));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    private static final int TIMEOUT_MS = 1000 * 2; //2 seconds
//
//    public static  Bitmap loadBitmapFromAsset(final Asset picture, final GoogleApiClient mGoogleApiClient) throws InterruptedException {
//        final Bitmap[] returned = new Bitmap[1];
//        Thread t = new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        if (picture == null) {
//                            throw new IllegalArgumentException("Asset must be non-null");
//                        }
//                        // convert asset into a file descriptor and block until it's ready
//                        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, picture).await().getInputStream();
//                        if (assetInputStream == null) {
//                            Log.w("GET PROFILE PICTURE", "Requested an unknown Asset.");
//                            returned[0] = null;
//                        }
//                        // decode the stream into a bitmap
//                        returned[0] = BitmapFactory.decodeStream(assetInputStream);
//
//                    }
//                });
//        t.run();
//        t.join();
//        return returned[0];
//    }
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
}
