package com.example.ngan.prog2b;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngan on 3/3/16.
 */
public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.details_fragment_layout, container, false);
        MainActivity activity = (MainActivity) getActivity();
        Legislator item = activity.passDataToDetailsFragment();

        DetailsViewHolder viewHolder = new DetailsViewHolder();

        viewHolder.tvTitle = (TextView) v.findViewById(R.id.detail_position);
        viewHolder.tvName = (TextView) v.findViewById(R.id.detail_name);
        viewHolder.tvParty = (TextView) v.findViewById(R.id.detail_party);
        viewHolder.ivPic = (ImageView) v.findViewById(R.id.detail_picture);
        viewHolder.tvEmail = (TextView) v.findViewById(R.id.detail_email);
        viewHolder.tvWeb = (TextView) v.findViewById(R.id.detail_web);
        viewHolder.tvTerm = (TextView) v.findViewById(R.id.detail_term);
        viewHolder.lvCommittees = (TextView) v.findViewById(R.id.detail_committees);
        viewHolder.lvBills = (TextView) v.findViewById(R.id.detail_bills);

        ImageLoader imageLoader = ImageLoader.getInstance();
        viewHolder.tvTitle.setText(item.getChamber());
        viewHolder.tvName.setText(item.firstName + " "+item.lastName);
        viewHolder.tvParty.setText(item.getParty());
        imageLoader.displayImage(item.profilePic.replace("_normal", ""), viewHolder.ivPic);
        viewHolder.tvEmail.setText(item.email);
        viewHolder.tvWeb.setText(item.website);
        viewHolder.tvTerm.setText(item.termStart + " to " + item.termEnd);
        viewHolder.lvCommittees.setText(item.getCommittees());
        viewHolder.lvBills.setText(item.getBills());
        return v;
    }

    private static class DetailsViewHolder {
        TextView tvTitle;
        TextView tvName;
        TextView tvParty;
        ImageView ivPic;
        TextView tvEmail;
        TextView tvWeb;
        TextView tvTerm;
        TextView lvCommittees;
        TextView lvBills;

    }



}
