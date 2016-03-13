package com.example.ngan.prog2b;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by ngan on 3/4/16.
 */
public class HomeFragment extends Fragment {

    HomeActionListener mCallback;

    public interface HomeActionListener {
        public void onLocButtonClick();
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, viewGroup, false);
        return view;
    }

    public void locationButtonClick() {mCallback.onLocButtonClick();}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        Make button listener for location button
        super.onActivityCreated(savedInstanceState);
        Button loc_button = (Button)getView().findViewById(R.id.location_button);
        loc_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCallback.onLocButtonClick();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (HomeActionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement HomeActionListener");
            }
        } else throw new IllegalArgumentException(context.toString()
                + " is not an Activity; HomeFragment.onAttach()");
    }
}
