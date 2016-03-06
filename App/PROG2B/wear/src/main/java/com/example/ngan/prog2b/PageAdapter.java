package com.example.ngan.prog2b;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;
import android.view.Gravity;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngan on 3/4/16.
 */
public class PageAdapter extends FragmentGridPagerAdapter {

    Location locationItem;
    private final Context mContext;
    ArrayList<RepListItem> mItems;
    private List mRows;
    int option = 1;
    // Create a static set of pages in a 2D array
    private  Page[][] PAGES;
    protected final MainActivity mActivity;

    public PageAdapter(Context ctx, FragmentManager fm, Location location) {
        super(fm);
        mActivity = (MainActivity)ctx;
        mContext = ctx;
        this.locationItem = location;
        List<RepListItem> reps = location.representatives;
        ArrayList<Page> p = new ArrayList<Page>();
        for (RepListItem r : reps) {
            p.add(new RepPage(r));
        }
        p.add(new VotePage(location));
        Page[] inner = new Page[p.size()];
        inner = p.toArray(inner);
        PAGES = new Page[1][inner.length];
        PAGES[0] = inner;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        if(page.id == 0) {
            PageFragment fragment = PageFragment.create1((RepPage) page, this);
            Log.d("--FUCK--", "getFragment: create repPage");
            return fragment;
        } else {
            PageFragment fragment = PageFragment.create2((VotePage) page, this);
            return fragment;
        }
    }

    @Override
    public Drawable getBackgroundForPage(int row, int col) {
//        Page page = PAGES[row][col];
//        if (page.id == 1) {
//            return ContextCompat.getDrawable(mContext, R.mipmap.presidents);
//        } else if (page.bkgrd != null) {
//            return page.bkgrd;
//        } else {
            return GridPagerAdapter.BACKGROUND_NONE;
//        }
    }
    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return PAGES.length;
    }
    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }


    public static class Page {
        final int id; //0--> RepPage, 1-->VotePage
        int iconRes = R.mipmap.icon;
        int cardGravity = Gravity.BOTTOM;
        boolean expansionEnabled = true;
        float expansionFactor = 1.0f;
        int expansionDirection = CardFragment.EXPAND_DOWN;
        public Drawable bkgrd = null;

        public Page(int i) {
            this.id = i;
        }

        public String getTitle() {
            return "Page Title";
        }

        public String getText() {
            return "page text here";
        }

    }

    // A simple container for static data in each page
    protected static class RepPage extends Page {
        // static resources
        final String party;
        final String name;
        final String position;

        public RepPage(RepListItem i) {
            super(0);
            name = i.name;
            party = i.getParty();
            position = i.title;
            bkgrd = i.pic;
        }
        @Override
        public String getTitle() {
            return name;
        }

        @Override
        public String getText() {
            return party + "\n" + position;
        }

    }

    protected static class VotePage extends Page {
        String obamaPercent = "99%";
        String romneyPercent = "1%";
        String county = "Santa Clara County";
        String state = "CA";

        public VotePage(Location l) {
            super(1);
            obamaPercent = l.obamaPercent;
            romneyPercent = l.romneyPercent;
            county = l.county;
            state = l.state;
        }
        @Override
        public String getTitle() {
            return county + ", " + state;
        }

        public String getText() {
            return "2012 Votes \n" +
                    "Obama: " + obamaPercent + "\n"+
                    "Romney: " + obamaPercent;
        }

        public String getLocation() {
            return getTitle();
        }

    }







}
