package com.example.ngan.prog2b;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private int option = 2;
    private Location locationItem;
    static HashMap<String, Drawable> repsPhotos = new HashMap<String, Drawable>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final float SHAKE_THRESHOLD = 3.25f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 10000;
    private long mLastShakeTime;

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

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int display_option = extras.getInt("LOCATION");
            option = display_option;
        }
        setUp();

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setConsumeWindowInsets(false);
        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(pager);
        pager.setAdapter(new PageAdapter(this, getFragmentManager(), sendToPageAdapter()));

    }

    //TODO:REMOVE THIS LATER
    void setUp() {

        List<Integer> repsPhoto = new ArrayList<Integer>();       // Photos of Dummy Reps
        List<String[]> repsInfo = new ArrayList<String[]>();         // Info Array[] of Dummy Reps
        String[] caryn = {"D", "Caryn Tran", "Representative", "caryn.tran@berkeley.edu",
                "caryntran.com", "Quam as etur at. Is intia as is dernatet qui doluptat "};
        String[] nico = {"D", "Yannick Keone LeCoeuche", "Senator", "nico.senator@berkeley.edu",
                "nicoisasenator.com", "If I need my hands to type and my feet to walk, how do I eat cake?"};
        String[] perth = {"R", "Perth Silvers", "Representative", "perthlovesanarchy@gov.ca",
                "perthisangry.com", "I love racism, sexism, and Donald Trump #republicanparty"};
        String[] cynthia = {"D", "Cynthia Contreras", "Senator", "cyncity@newyork.gov",
                "cyncitaaaaay.com", "Womyn are beautiful. And seafood is just so amazing. I love fish"};
        String[] noah = {"D", "Noah Shafi", "Representative", "noone@ca.gov",
                "NoahGoYah.ca.gov", "Bernie is the best thing for america #longlivebernie #hopefully"};
        String[] kate = {"D", "Kate Feenstra", "Representative", "compliKate@ca.gov",
                "schnitzmcgee.ca.gov", "I'm actually an illegal alien who is addicted to plastic surgery"};
        String[] collin = {"R", "Collin Barlow", "Representative", "collinoscopy@ca.gov",
                "collinbeballin.ca.gov", "I wanna be rich. I wanna be super rich...the next donald trump"};


        int[] option1 = {0, 1, 2, 3}; //1 --> Zip Code 11111 (Caryn, Niko, Perth, Cynthia) [Santa Clara County, CA, 80, 20]
        int[] option2 = {4, 5, 6}; //2 --> Zip Code 22222 (Noah, Kate, Collin) [Alameda County, CA, 100, 0]
        int[] option3 = {0, 3, 5}; //3 --> Using your location (Caryn, Kate, Cynthia) [Orange County, CA, 50, 50]

        repsInfo.add(caryn);
        repsInfo.add(nico);
        repsInfo.add(perth);
        repsInfo.add(cynthia);
        repsInfo.add(noah);
        repsInfo.add(kate);
        repsInfo.add(collin);
        repsPhoto.add(R.mipmap.profile);
        repsPhoto.add(R.mipmap.niko);
        repsPhoto.add(R.mipmap.perth);
        repsPhoto.add(R.mipmap.cynthia);
        repsPhoto.add(R.mipmap.noah);
        repsPhoto.add(R.mipmap.kate);
        repsPhoto.add(R.mipmap.collin);
        int[] selectedOption;

        for (int i = 0; i< repsPhoto.size(); i++){
            repsPhotos.put(repsInfo.get(i)[1], ContextCompat.getDrawable(this, repsPhoto.get(i)));
        }

        switch (option) {
            case 1:
                selectedOption = option1;
                locationItem = new Location("Santa Clara County", "CA", "80", "20");
                break;
            case 2:
                selectedOption = option2;
                locationItem = new Location("Alameda County", "CA", "100", "0");
                break;
            case 3:
                selectedOption = option3;
                locationItem = new Location("Orange County", "CA", "50", "50");
                break;
            default:
                Log.d("--option--", Integer.toString(option));
                selectedOption = option1;
        }
        // initialize the items list
        ArrayList<RepListItem> mItems = new ArrayList<RepListItem>();
        Resources resources = getResources();

        // add RepListItems to the list
        for (int i : selectedOption) {
            String[] repInfo = repsInfo.get(i);
            Drawable repPhoto = ContextCompat.getDrawable(this, repsPhoto.get(i));
            RepListItem rep = new RepListItem(repPhoto, repInfo[0], repInfo[1], repInfo[2], repInfo[3], repInfo[4], repInfo[5]);
            mItems.add(rep);
        }
        locationItem.representatives = mItems;
    }

    public Location sendToPageAdapter() {
        return locationItem;
    }

    //TODO:REMOVE LATER
    public void changeOptionTo(int newOption) {
        if (newOption%3 != option) {
            option = newOption%3;
            if (newOption%3 == 0) option = 1;
            List<Integer> repsPhoto = new ArrayList<Integer>();       // Photos of Dummy Reps
            List<String[]> repsInfo = new ArrayList<String[]>();         // Info Array[] of Dummy Reps
            String[] caryn = {"D", "Caryn Tran", "Representative", "caryn.tran@berkeley.edu",
                    "caryntran.com", "Quam as etur at. Is intia as is dernatet qui doluptat "};
            String[] nico = {"D", "Yannick Keone LeCoeuche", "Senator", "nico.senator@berkeley.edu",
                    "nicoisasenator.com", "If I need my hands to type and my feet to walk, how do I eat cake?"};
            String[] perth = {"R", "Perth Silvers", "Representative", "perthlovesanarchy@gov.ca",
                    "perthisangry.com", "I love racism, sexism, and Donald Trump #republicanparty"};
            String[] cynthia = {"D", "Cynthia Contreras", "Senator", "cyncity@newyork.gov",
                    "cyncitaaaaay.com", "Womyn are beautiful. And seafood is just so amazing. I love fish"};
            String[] noah = {"D", "Noah Shafi", "Representative", "noone@ca.gov",
                    "NoahGoYah.ca.gov", "Bernie is the best thing for america #longlivebernie #hopefully"};
            String[] kate = {"D", "Kate Feenstra", "Representative", "compliKate@ca.gov",
                    "schnitzmcgee.ca.gov", "I'm actually an illegal alien who is addicted to plastic surgery"};
            String[] collin = {"R", "Collin Barlow", "Representative", "collinoscopy@ca.gov",
                    "collinbeballin.ca.gov", "I wanna be rich. I wanna be super rich...the next donald trump"};


            int[] option1 = {0, 1, 2, 3}; //1 --> Zip Code 11111 (Caryn, Niko, Perth, Cynthia) [Santa Clara County, CA, 80, 20]
            int[] option2 = {4, 5, 6}; //2 --> Zip Code 22222 (Noah, Kate, Collin) [Alameda County, CA, 100, 0]
            int[] option3 = {0, 3, 5}; //3 --> Using your location (Caryn, Kate, Cynthia) [Orange County, CA, 50, 50]

            repsInfo.add(caryn);
            repsInfo.add(nico);
            repsInfo.add(perth);
            repsInfo.add(cynthia);
            repsInfo.add(noah);
            repsInfo.add(kate);
            repsInfo.add(collin);
            repsPhoto.add(R.mipmap.profile);
            repsPhoto.add(R.mipmap.niko);
            repsPhoto.add(R.mipmap.perth);
            repsPhoto.add(R.mipmap.cynthia);
            repsPhoto.add(R.mipmap.noah);
            repsPhoto.add(R.mipmap.kate);
            repsPhoto.add(R.mipmap.collin);
            int[] selectedOption;

            for (int i = 0; i< repsPhoto.size(); i++){
                repsPhotos.put(repsInfo.get(i)[1], ContextCompat.getDrawable(this, repsPhoto.get(i)));
            }

            switch (option) {
                case 1:
                    selectedOption = option1;
                    locationItem = new Location("Santa Clara County", "CA", "80", "20");
                    break;
                case 2:
                    selectedOption = option2;
                    locationItem = new Location("Alameda County", "CA", "100", "0");
                    break;
                case 3:
                    selectedOption = option3;
                    locationItem = new Location("Orange County", "CA", "50", "50");
                    break;
                default:
                    Log.d("--option--", Integer.toString(option));
                    selectedOption = option1;
            }
            // initialize the items list
            ArrayList<RepListItem> mItems = new ArrayList<RepListItem>();
            Resources resources = getResources();

            // add RepListItems to the list
            for (int i : selectedOption) {
                String[] repInfo = repsInfo.get(i);
                Drawable repPhoto = ContextCompat.getDrawable(this, repsPhoto.get(i));
                RepListItem rep = new RepListItem(repPhoto, repInfo[0], repInfo[1], repInfo[2], repInfo[3], repInfo[4], repInfo[5]);
                mItems.add(rep);
            }
            locationItem.representatives = mItems;
        }
    }

    public void sendNameToPhone(String name) {
        Log.d("--WATCHTOPHONE--", "Creating intent with name: " + name);
        Intent sendIntent = new Intent(getBaseContext(), MsgToPhoneService.class);
        sendIntent.putExtra("ACTION", name);
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
                    Toast.makeText(this, "Location changed due to shake!", Toast.LENGTH_SHORT);
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
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
