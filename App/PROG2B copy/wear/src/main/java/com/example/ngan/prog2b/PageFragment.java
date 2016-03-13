package com.example.ngan.prog2b;


import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.ActionLabel;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by ngan on 3/4/16.
 */
public class PageFragment extends Fragment {

    private static final String SHAKE = "/s";
    private static final String DETAIL_FEED = "/d";

    PageAdapter.Page p;
    PageAdapter mPageAdapter;
    String obamaPercent;
    String romneyPercent;
    String county;
    String state;
    static HashMap<String, Integer> repsPhotos = new HashMap<String, Integer>();

    public PageFragment(){
    }

    public static PageFragment create1(PageAdapter.RepPage p, PageAdapter pA){
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        if(p != null) {
            args.putInt("PageFragment_option", 0);
            args.putString("PageFragment_party", p.party);
            args.putString("PageFragment_name", p.name);
            args.putString("PageFragment_id", p.id);
            args.putString("PageFragment_title", p.position);
//            args.putParcelable("PageFragment_background", p.bkgrd);
        }
        fragment.setArguments(args);
        fragment.mPageAdapter = pA;
        return fragment;
    }

    public static PageFragment create2(PageAdapter.VotePage p, PageAdapter pA){
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        if(p != null) {
            args.putInt("PageFragment_option", 1);
            args.putString("PageFragment_obamaPercent", p.obamaPercent);
            args.putString("PageFragment_romneyPercent", p.romneyPercent);
            args.putString("PageFragment_location", p.getLocation());
        }
        fragment.setArguments(args);
        fragment.mPageAdapter = pA;
        return fragment;
    }

    public static PageFragment create0(PageAdapter pA){
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("PageFragment_option", -1);
        fragment.setArguments(args);
        fragment.mPageAdapter = pA;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        int option = args.getInt("PageFragment_option");
        if (option == 0) {
            View result = inflater.inflate(R.layout.rep_layout, container, false);
            result.setOnClickListener(new View.OnClickListener() {
                private String findID(ViewGroup viewGroup) {
                    int count = viewGroup.getChildCount();
                    TextView t1 = (TextView) viewGroup.getChildAt(0); //R.id.rep_id VIEW
                    return t1.getText().toString();
                }

                @Override
                public void onClick(View v) {
                    // Create a data map and put data in it
                    String id = findID((ViewGroup) v);
                    Log.d("---Click Rep--", "CLICKED ON SOMEONE " + id);
                    mPageAdapter.mActivity.sendIDToPhone(id);

                }
            });
            if(args != null) {
                TextView id = (TextView) result.findViewById(R.id.rep_id);
                if (args.containsKey("PageFragment_id")) {
                    id.setText(args.getCharSequence("PageFragment_id"));
                    Log.d("F", "onCreateView: f");
                }
                ActionLabel name = (ActionLabel) result.findViewById(R.id.rep_name);
                if (args.containsKey("PageFragment_name")) {
                    name.setText(args.getCharSequence("PageFragment_name"));
                    Log.d("U", "onCreateView: u");
                }
                // TODO: 3/12/16 SET BACKGROUND OF PAGE FRAGMENT 
//                if (args.containsKey("PageFragment_id")) {
//
////                    Bitmap bitmap = (Bitmap) args.getParcelable("PageFragment_background");
////                    BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
//
//                    result.setBackground(null);
//                    Log.d("C", "onCreateView: c");
//                }
                if (args.containsKey("PageFragment_party")) {
                    TextView party = (TextView) result.findViewById(R.id.rep_party);
                    if (party != null) {
                        party.setText(args.getCharSequence("PageFragment_party"));
                        Log.d("K", "onCreateView: k");
                    }
                }
                ActionLabel title = (ActionLabel) result.findViewById(R.id.rep_title);
                if (args.containsKey("PageFragment_title") && title != null) {
                    title.setText(args.getCharSequence("PageFragment_title"));
                }
            }
                return result;
        } else if (option == 1){
            View result = inflater.inflate(R.layout.vote_layout, container, false);
            if(args != null) {
                TextView o = (TextView)result.findViewById(R.id.vote_obama_percent);
                if(args.containsKey("PageFragment_obamaPercent") && o != null) {
                    o.setText(args.getCharSequence("PageFragment_obamaPercent")+"%");
                }
                if(args.containsKey("PageFragment_romneyPercent")) {
                    TextView r = (TextView)result.findViewById(R.id.vote_romney_percent);
                    if(r != null) {
                        r.setText(args.getCharSequence("PageFragment_romneyPercent")+"%");
                    }
                }
                ActionLabel location = (ActionLabel)result.findViewById(R.id.vote_county_state);
                if(args.containsKey("PageFragment_location") && location != null) {
                    location.setText(args.getCharSequence("PageFragment_location"));
                }
            }
            return result;
        } else {
            View result = inflater.inflate(R.layout.no_layout, container, false);
            return result;
        }
    }
}
