package com.example.ngan.prog2b;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity
        implements RepListFragment.ListActionListener,
        HomeFragment.HomeActionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "TU7A8gbGxhorbuP2or775UnPr";
    private static final String TWITTER_SECRET = "i6XWXvwLcKNwss4CMqT1xxkA5ugrTvGTn10ef4DgFj4x8U8ekT";

    EditText zipcode;
    FrameLayout zip;
    RepListFragment RepView;
    DetailsFragment DetView;

    Set<String> validZip =  new HashSet<String>();

    private static final String NEW_LOCATION_OPTION = "NEW_LOCATION";



    @Override
    public void onBackPressed() {
        zip.setVisibility(View.VISIBLE);
        zipcode.setVisibility(View.VISIBLE);
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    //TODO: FROMWATCH (DONE)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For location services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(AppIndex.API).build();
//            mGoogleApiClient.connect();
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main);
        loadHomeFragment(savedInstanceState);
        handleLocationChange();
        zip = (FrameLayout) findViewById(R.id.location_bar);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Received Message from Watch"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String id = intent.getStringExtra("id");
            if (id.equals("SHAKE")) {
                getNewRandomLocation();
            } else {
                onDetailSelected(mLegislatorsMap.get(id));
            }
            Log.d("--RECEIVER--", "Got message: " + id);
        }
    };

    /**
     * called when activity is created
     * adds a listener to the location_button
     * @param savedInstanceState
     */
    private void loadHomeFragment(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // we're being restored from a previous state, then we don't need to do anything and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            HomeFragment firstFragment = new HomeFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment, "home_fragment").commit();
        }
    }


    /**
     * Called by handleLocationChange() to change the rep list and/or switch to that view
     * First you should call changeOption or manually change the value of var=option
     */
    public void changeToListView(){
        Log.d("--UPDATE FRAGMENT--", "changeToListView");
        mGoogleApiClient.disconnect();
        RepView = new RepListFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, RepView);
        transaction.addToBackStack("list_fragment");
        transaction.commit();
    }

    public void changeToDetailView(Legislator i) {
        hideInputBox();
        mGoogleApiClient.disconnect();
        selected = i;
        DetView = new DetailsFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, DetView);
        transaction.addToBackStack("details_fragment");
        transaction.commit();
    }

    /**
     * handles typing into field and pressing enter.
     * if valid location, will changeOption, changeToListView
     * else display error
     */
    //ToDo:DONE doesn't recognize focus change; removed focus on enter key press
    public void handleLocationChange() {
        final FrameLayout zipFrame = (FrameLayout)findViewById(R.id.location_bar);
        zipFrame.setFocusable(true);
        zipFrame.setFocusableInTouchMode(true);
        zipcode = (EditText)findViewById(R.id.enter_zip);
        zipcode.setFocusable(true);
        zipcode.setFocusableInTouchMode(true);
        zipcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    EditText t = (EditText) v;
                    String text = t.getText().toString();
                    if (!hasFocus) {
                        syncAsyncFetches = new AtomicInteger(0);
                        addr_Failed = false;
                        gotCoordinates = false;
                        asyncFetchAddressWithZipcode(text, t);
                        asyncFetchLegislatorsWithZipcode(text);
                        //When completed, syncAsyncFetch() will call on things to change the views
                        //and will propogate information to watch
                    }
                }
            }
        });
        zipcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    zipcode.setFocusable(false);
                    zipcode.setFocusableInTouchMode(true);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * Should probably use Bundle()... putString + fragment.setArguments(bundle)
     *
     * Called on creation by RepListFragment
     * to pass along information about location
     * three options (11111 or 22222 or using your location)
     *
     * 0 --> nothing selected [should set to home_fragment_layout]
     * 1 --> Zip Code 11111 (Caryn, Niko, Perth, Cynthia)
     * 2 --> Zip Code 22222 (Noah, Kate, Collin)
     * 3 --> Using your location (Caryn, Kate, Cynthia)
     *
     * @return int code
     */
    public ArrayList<Legislator> passDataToListFragment(){
        return mLegislators;
    }

    public void getNewRandomLocation(){
        // TODO: 3/11/16 getNewRandomLocation()
        new AsyncLoadRandomLocation(this).execute();
    }

    public void tryNewRandomLocation(String latitude, String longitude) {
        syncAsyncFetches = new AtomicInteger(0);
        MainActivity.this.asyncFetchAddressWithCoordinates(latitude, longitude);
        MainActivity.this.asyncFetchLegislatorsWithCoordinates(Double.valueOf(latitude), Double.valueOf(longitude));
    }
    /**
     *
     * @return
     */
    public Legislator passDataToDetailsFragment(){
        //// TODO: 3/11/16 if selected is null
        return selected;
    }

    @Override
    public void onWebSelected(Legislator i) {
        //Launch website on browser
        //ToDo: onWebSelected -->launch web client
        String url = i.website;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onEmailSelected(Legislator i) {
        //Launch email in client
        //ToDo: onEmailSelected -->launch email client
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, i.email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "MURICA: Email to legislator");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear " + i.firstName + " " + i.lastName + ",\n");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void onDetailSelected(Legislator i) {
        changeToDetailView(i);
    }

    /**
     *
     */
    public void hideInputBox() {
        zip.setVisibility(View.GONE);
        zipcode.setVisibility(View.GONE);
    }

    public void showInputBox() {
        zip.setVisibility(View.VISIBLE);
        zipcode.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /**
     * Gathers the location information and fetches corresponding information
     */
    @Override
    public void onLocButtonClick() {
        mAddressRequested = true;
        mGoogleApiClient.connect();
        zipcode.setText(null);
        zipcode.setHint("Using your location");
//        if (mLastLocation != null) {
////            asyncFetchAddressWithZipcode("95035", null);
////            asyncFetchLegislatorsWithZipcode("95035");
//            asyncFetchAddressWithCoordinates();
//            asyncFetchLegislatorsWithCoordinates();
//        }
    }

    ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    final static int SYNC_DONE = 5;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    final static int REQUEST_LOCATION = 199;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation = null;
    TextView mLatitudeText;
    TextView mLongitudeText;
    TextView mCountyStateText;
    Button mUpdateButton;

    AtomicInteger syncAsyncFetches = new AtomicInteger(0);
    protected boolean mAddressRequested;
    PendingResult<LocationSettingsResult> result;
    Boolean gotCoordinates = false;
    Double longitude;
    Double latitude;

    static String mAddressOutput = null; //STORES THE INFO FOR COUNTY AND STATE!! IMPORTANT!!
    static String[] mVoteInfo = null; //STORES THE VOTES FOR COUNTY IMPORTANT!!
    static ArrayList<Legislator> mLegislators = null; //STORES THE LEGISLATORS FOR THE AREA
    Legislator selected;
    HashMap<String,Legislator> mLegislatorsMap = null;


    protected void asyncFetchAddressWithZipcode(String zip, EditText editText) {
        String isCoordinate = String.valueOf(false);
        String[] args = new String[]{isCoordinate, zip};
        new AsyncFetchAddress(this, editText).execute(args);
    }

    protected void asyncFetchAddressWithCoordinates() {
        if (mLastLocation != null) {
            String isCoordinate = String.valueOf(true);
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            String latitudeS = String.valueOf(latitude);
            String longitudeS = String.valueOf(longitude);
            String[] args = new String[]{isCoordinate, latitudeS, longitudeS};
            new AsyncFetchAddress(this, null).execute(args); //could choose to input a non null edit text
        }
    }

    protected void asyncFetchAddressWithCoordinates(String latitudeS, String longitudeS) {
        if (mLastLocation != null) {
            String isCoordinate = String.valueOf(true);
            String[] args = new String[]{isCoordinate, latitudeS, longitudeS};
            new AsyncFetchAddress(this, null).execute(args); //could choose to input a non null edit text
        }
    }

    protected void asyncFetchLegislatorsWithZipcode(String zip) {
        String isCoordinate = String.valueOf(false);
        String[] args = new String[]{isCoordinate, zip};
        new AsyncFetchLegislators(this).execute(args);
    }

    protected void asyncFetchLegislatorsWithCoordinates() {
        if (mLastLocation != null) {
            String isCoordinate = String.valueOf(true);
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            String latitudeS = String.valueOf(latitude);
            String longitudeS = String.valueOf(longitude);
            String[] args = new String[]{isCoordinate, latitudeS, longitudeS};
            new AsyncFetchLegislators(this).execute(args);
        }
    }

    protected void asyncFetchLegislatorsWithCoordinates(double latitude, double longitude) {
        String isCoordinate = String.valueOf(true);
        String latitudeS = String.valueOf(latitude);
        String longitudeS = String.valueOf(longitude);
        String[] args = new String[]{isCoordinate, latitudeS, longitudeS};
        new AsyncFetchLegislators(this).execute(args);
    }

    protected void asyncFetchVoteData() {
        if(mAddressOutput != null) {
            new AsyncLoadVoteData(this).execute(mAddressOutput);
        }
    }

    protected void asyncFetchLegislatorData(){
        if(mLegislators != null) {
            new AsyncFetchComBillTweets(this, mLegislators).execute();
        }
    }

    Boolean addr_Failed = false;
    protected void syncAsyncFetch(Boolean passed, String process) {
        if (passed) {
            int compare = syncAsyncFetches.incrementAndGet();
            Log.d("-SYNC-", "syncAsyncFetch " + process + ": "
                    + String.valueOf(compare) + "/"
                    + String.valueOf(SYNC_DONE));
            if (compare == SYNC_DONE) {
                //Done with fetching county and state,
                //Done with fetching vote data
                //Done with fetching legislators,
                //Done with fetching bills and committees
                //Done with fetching tweets,

//            mAddressOutput;
//            mLegislators;
//            mVoteInfo; %[80, 90]

                //load RepListview
                changeToListView();

                //send data to phone
                Intent sendIntent = new Intent(getBaseContext(), MsgToWatchService.class);
                startService(sendIntent);
                Log.d("--PHONETOWATCH--", "Sent command to communicate to watch");
                syncAsyncFetches.set(0);
            }
        } else {
            if (process.equals("zipcodeLegislators")) {
                while (true) {
                    if (gotCoordinates) {
                        asyncFetchLegislatorsWithCoordinates(latitude, longitude);
                        break;
                    }
                }
            }

            if (process.equals("addr")) {
                // TODO: 3/12/16  when you can't get the county or the vote data, everything fails.
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("--mGAPI--", "onConnected: 3");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("--mGAPI--", "onConnected: 2");
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        try {
                            Log.d("--mGAPI--", "onConnected: 1");
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                if (!Geocoder.isPresent()) {
                                    Toast.makeText(MainActivity.this, R.string.no_geocoder_available,
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (MainActivity.this.mAddressRequested) {
                                    Log.d("--mGAPI--", "onConnected: banzai " + mLastLocation.toString());
                                    syncAsyncFetches = new AtomicInteger(0);
                                    MainActivity.this.asyncFetchAddressWithCoordinates();
                                    MainActivity.this.asyncFetchLegislatorsWithCoordinates();
                                    MainActivity.this.mAddressRequested = false;
                                } else {
                                    mGoogleApiClient.disconnect();
                                }
                            }
                        } catch (SecurityException e) {
                            Log.d("--LocationServices--", "Security Exception");
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        mGoogleApiClient.connect();
                        Toast.makeText(MainActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
