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
        RepListItem repItem = activity.passDataToDetailsFragment();
//        String[] committees = repItem.committees;
//        final List<RepListItem.Bill> bills = repItem.bills;
//        List placeholder = new ArrayList();

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

        viewHolder.tvTitle.setText(repItem.title);
        viewHolder.tvName.setText(repItem.name);
        viewHolder.tvParty.setText(repItem.getParty());
        viewHolder.ivPic.setImageDrawable(repItem.pic);
        viewHolder.tvEmail.setText(repItem.email);
        viewHolder.tvWeb.setText(repItem.web);
        viewHolder.tvTerm.setText(repItem.term);
        viewHolder.lvCommittees.setText(repItem.getCommittees());
        viewHolder.lvBills.setText(repItem.getBills());

//        //Populate the committees list
//        ArrayAdapter<String> committeesAdapter = new ArrayAdapter<String>(activity,
//                android.R.layout.simple_list_item_1, android.R.id.text1, committees);
//        viewHolder.lvCommittees.setAdapter(committeesAdapter);
//
//        //Populate the bills list
//        ArrayAdapter billsAdapter = new ArrayAdapter(activity,
//                android.R.layout.simple_list_item_2, android.R.id.text1, placeholder) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                text1.setText(bills.get(position).getTitle());
//                text2.setText(bills.get(position).getDate());
//                return view;
//            }
//        };
//        viewHolder.lvBills.setAdapter(billsAdapter);

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
