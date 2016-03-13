package com.example.ngan.prog2b;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;
import android.view.Gravity;

import com.github.florent37.davinci.DaVinci;
import com.google.android.gms.wearable.Asset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngan on 3/4/16.
 */
public class PageAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    ArrayList<Legislator> mItems;
    private List mRows;
    int option = 1;
    // Create a static set of pages in a 2D array
    private  Page[][] PAGES;
    private  String[][] IDS;
    protected final MainActivity mActivity;

    public PageAdapter(Context ctx, FragmentManager fm, String county, String[] votes, ArrayList<Legislator> legislators) {
        super(fm);
        mActivity = (MainActivity)ctx;
        mContext = ctx;

        if (county == null) {
            Page p = new Page(-1);
            PAGES = new Page[][]{{p}};
        } else {
            ArrayList<Page> p = new ArrayList<Page>();
            ArrayList<String> ids  = new ArrayList<String>();
            for (Legislator legislator : legislators) {
                p.add(new RepPage(legislator));
                ids.add(legislator.id);
            }
            p.add(new VotePage(county, votes));
            ids.add("vote");
            Page[] inner = new Page[p.size()];
            String[] innerIDs = new String[ids.size()];

            inner = p.toArray(inner);
            innerIDs = ids.toArray(innerIDs);
            PAGES = new Page[1][inner.length];
            IDS = new String[1][innerIDs.length];
            PAGES[0] = inner;
            IDS[0] = innerIDs;
        }
        mItems = legislators;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        if(page.id == 0) {
            PageFragment fragment = PageFragment.create1((RepPage) page, this);
            Log.d("--FUCK--", "getFragment: create repPage");
            return fragment;
        } else if (page.id == 1) {
            PageFragment fragment = PageFragment.create2((VotePage) page, this);
            return fragment;
        } else {
            PageFragment fragment = PageFragment.create0(this);
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
        if (IDS == null) {
            return GridPagerAdapter.BACKGROUND_NONE;
        }
        String id = IDS[row][col];
        String imageUrl = null;
//        for (Legislator l : mItems) {
//            if (l.id.equals(id)) {
//                imageUrl = l.profilePicture;
//                break;
//            }
//        }
//        imageUrl = "/image/"+id;

//        if (!id.equals("vote") && !(imageUrl == null)) {
//            return DaVinci.with(mContext).load(imageUrl).into(this, row, col);
//        } else {
//            return GridPagerAdapter.BACKGROUND_NONE;
//        }
        return GridPagerAdapter.BACKGROUND_NONE;
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
        public Bitmap bkgrd = null;

        public Page(int i) {
            this.id = i;
        }

        public String getTitle() {
            return "Page Title";
        }

        public String getText() {
            return "Page Text Here";
        }

    }

    // A simple container for static data in each page
    protected static class RepPage extends Page {
        // static resources
        final String party;
        final String name;
        final String position;
        final String id;

        public RepPage(Legislator l) {
            super(0);
            id = l.id;
            name = l.firstName +" "+ l.lastName;
            party = l.getParty();
            position = l.getChamber();
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
        String countyState = "Santa Clara County, CA";

        public VotePage(String county, String[] votes) {
            super(1);
            obamaPercent = votes[0];
            romneyPercent = votes[1];
            countyState = county;
        }
        @Override
        public String getTitle() {
            return countyState;
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
