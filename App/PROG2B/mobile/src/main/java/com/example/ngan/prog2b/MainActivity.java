package com.example.ngan.prog2b;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements RepListFragment.ListActionListener, HomeFragment.HomeActionListener{
    int option = 0;
    EditText zipcode;
    FrameLayout zip;
    RepListItem selected;
    RepListFragment RepView;
    DetailsFragment DetView;
    Set<String> validZip =  new HashSet<String>();
    Location locationItem = locationItem = new Location("Santa Clara County", "CA", "80", "20");
    HashMap<String,RepListItem> PEOPLE = new HashMap<String, RepListItem>();
    private static final String NEW_LOCATION_OPTION = "NEW_LOCATION";

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    //TODO: FROMWATCH (DONE)
    protected void onCreate(Bundle savedInstanceState) {
        setUpForWatch();
        validZip.add("11111");
        validZip.add("22222");
        validZip.add("33333");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            zipcode = (EditText)findViewById(R.id.enter_zip);
            String display_option = extras.getString("DISPLAY");
            if (display_option.equals(NEW_LOCATION_OPTION)) {
                Random rand = new Random();
                int randomNum = 1 + rand.nextInt(3);
                int new_option = randomNum;
                changeOption(new_option);
                zipcode.setText("shake");
                changeToListView();
            } else {
                RepListItem detail_rep = PEOPLE.get(display_option);
                changeToDetailView(detail_rep);
            }
        } else {
            loadHomeFragment(savedInstanceState);
            handleLocationChange();
            zip = (FrameLayout) findViewById(R.id.location_bar);
        }
    }

    /**
     * called when activity is created
     * adds a listener to the location_button
     * @param savedInstanceState
     */
    private void loadHomeFragment(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
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
     * helper ensures that all location changes are propogated back to the watch
     * @param o
     * @return
     */
    private boolean changeOption(int o) {
        if (o == 1 || o == 0) {
            changeOption("11111");
        } else if (o == 2) {
            changeOption("22222");
        } else {
            changeOption("33333");
        }
        return true;
    }
    /**
     * Called by handleLocationChange()
     * Two Locations... data needed to pass to ListFragment,
     * see passDataToListFragment for more details
     *
     * TODO: TOWATCH (DONE)
     * ToDo:get rid of option handling (using temporarily for PROG2B to handle zipcodes)
     * @param location
     */
    private boolean changeOption(String location) {
        Log.d("--PHONETOWATCH--", "Should send msg to watch");
        int prevOption = option;
        Intent sendIntent;
        switch (location) {
            case "11111": option = 1;
                sendIntent = new Intent(getBaseContext(), MsgToWatchService.class);
                sendIntent.putExtra("LOCATION", "1");
                startService(sendIntent);
                Log.d("--PHONETOWATCH--", "1");
                break;
            case "22222": option = 2;
                sendIntent = new Intent(getBaseContext(), MsgToWatchService.class);
                sendIntent.putExtra("LOCATION", "2");
                startService(sendIntent);
                Log.d("--PHONETOWATCH--", "2");
                break;
            default: option = 3;
                sendIntent = new Intent(getBaseContext(), MsgToWatchService.class);
                sendIntent.putExtra("LOCATION", "3");
                startService(sendIntent);
                Log.d("--PHONETOWATCH--", "3");
                break;
        }
        if (option == prevOption) {
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * Called by handleLocationChange() to change the rep list and/or switch to that view
     * First you should call changeOption or manually change the value of var=option
     */
    public void changeToListView(){
        RepView = new RepListFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, RepView);
        transaction.addToBackStack("list_fragment");
        transaction.commit();
    }

    public void changeToDetailView(RepListItem i) {
        onDetailSelected(i);
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
                        //entered valid location
                        if (validZip.contains(text)) {
                            t.setError(null);
                            if (changeOption(text)) {
                                changeToListView();
                            }
                        } else {
                            t.setError("Invalid Location");
                        }
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
    public ArrayList<RepListItem> passDataToListFragment(){
        List<Integer> repsPhotos = new ArrayList<Integer>();       // Photos of Dummy Reps
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
        repsPhotos.add(R.mipmap.profile);
        repsPhotos.add(R.mipmap.niko);
        repsPhotos.add(R.mipmap.perth);
        repsPhotos.add(R.mipmap.cynthia);
        repsPhotos.add(R.mipmap.noah);
        repsPhotos.add(R.mipmap.kate);
        repsPhotos.add(R.mipmap.collin);
        int[] selectedOption;
        switch (option) {
            case 1: selectedOption = option1;
                locationItem = new Location("Santa Clara County", "CA", "80", "20");
                break;
            case 2: selectedOption = option2;
                locationItem = new Location("Alameda County", "CA", "100", "0");
                break;
            case 3: selectedOption = option3;
                locationItem = new Location("Orange County", "CA", "50", "50");
                break;
            default:
                Log.d("--option--", Integer.toString(option));
                selectedOption = option1;
        }
        // initialize the items list
        ArrayList<RepListItem> mItems = new ArrayList<RepListItem>();
        Resources resources = getResources();

        for (int i = 0; i < repsInfo.size(); i++) {
            String[] repInfo = repsInfo.get(i);
            String name;
            Drawable repPhoto = ContextCompat.getDrawable(this, repsPhotos.get(i));
            name = repInfo[1];
            if(!PEOPLE.containsKey(name)) {
                RepListItem rep = new RepListItem(repPhoto, repInfo[0], repInfo[1], repInfo[2], repInfo[3], repInfo[4], repInfo[5]);
                PEOPLE.put(name, rep);
            }
        }

        // add RepListItems to the list
        for (int i : selectedOption) {
            String[] repInfo = repsInfo.get(i);
            Drawable repPhoto = ContextCompat.getDrawable(this, repsPhotos.get(i));
            RepListItem rep = new RepListItem(repPhoto, repInfo[0], repInfo[1], repInfo[2], repInfo[3], repInfo[4], repInfo[5]);
            mItems.add(rep);
        }
        locationItem.representatives = mItems;
        return mItems;
    }

    /**
     *
     * @return
     */
    public RepListItem passDataToDetailsFragment(){
        String[] collin = {"R", "Collin Barlow", "Representative", "collinoscopy@ca.gov",
                "collinbeballin.ca.gov", "I wanna be rich. I wanna be super rich...the next donald trump"};
        if (selected == null) {
            Drawable repPhoto = ContextCompat.getDrawable(this, R.mipmap.collin);
            selected = new RepListItem(repPhoto, collin[0], collin[1], collin[2], collin[3], collin[4], collin[5]);
        }
        return selected;
    }

    @Override
    public void onWebSelected(RepListItem i) {
        //Launch website on browser
        //ToDo: onWebSelected -->launch web client
    }

    @Override
    public void onEmailSelected(RepListItem i) {
        //Launch email in client
        //ToDo: onEmailSelected -->launch email client
    }

    @Override
    public void onDetailSelected(RepListItem i) {
        //Update DetailsFragment and switch views
        selected = i;
        DetView = new DetailsFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, DetView);
        transaction.addToBackStack("details_fragment");
        transaction.commit();
    }

    /**
     * ToDo: make the zip bar disappear for DetailFragment views but reappear on backpress
     * @param tag
     */
    public void setZipVisibility(String tag) {
        if (tag == "details_fragment") {
            zip.setVisibility(View.GONE);
            zipcode.setVisibility(View.GONE);
        } else {
            zip.setVisibility(View.VISIBLE);
            zipcode.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocButtonClick() {
        changeOption("location button");
        changeToListView();
        zipcode.setHint("using your location");
        zipcode.setHintTextColor(getResources().getColor(R.color.colorOffBlue));
        zipcode.setText(null);
    }

    //ToDo: consolidate with pass to listFragment
    public void setUpForWatch(){
        List<Integer> repsPhotos = new ArrayList<Integer>();       // Photos of Dummy Reps
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
        repsPhotos.add(R.mipmap.profile);
        repsPhotos.add(R.mipmap.niko);
        repsPhotos.add(R.mipmap.perth);
        repsPhotos.add(R.mipmap.cynthia);
        repsPhotos.add(R.mipmap.noah);
        repsPhotos.add(R.mipmap.kate);
        repsPhotos.add(R.mipmap.collin);

        for (int i = 0; i < repsInfo.size(); i++) {
            String[] repInfo = repsInfo.get(i);
            String name;
            Drawable repPhoto = ContextCompat.getDrawable(this, repsPhotos.get(i));
            name = repInfo[1];
            if(!PEOPLE.containsKey(name)) {
                RepListItem rep = new RepListItem(repPhoto, repInfo[0], repInfo[1], repInfo[2], repInfo[3], repInfo[4], repInfo[5]);
                PEOPLE.put(name, rep);
            }
        }
    }


}
