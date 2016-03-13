package com.example.ngan.prog2b;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by ngan on 3/2/16.
 * The list adapter class extends the ArrayAdapter class.
 * It overrides the getView() method of the ArrayAdapter in which the row view is created,
 * and uses the View Holder pattern which prevents using findViewById() repeatedly.
 */
public class RepListAdapter extends ArrayAdapter<Legislator> {
    RepListFragment fragment;

    public RepListAdapter(Context context, List<Legislator> legislators, RepListFragment fragment){
        super(context, R.layout.replist_item, legislators);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the item layout
            // create ViewHolder if null
            LayoutInflater inflater = LayoutInflater.from(getContext());

            //ToDo:Which one?
            convertView = inflater.inflate(R.layout.replist_item, parent, false);
//            convertView = inflater.inflate(R.layout.replist_item, null);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.rep_pic);
            viewHolder.ivPic2 = (ImageView) convertView.findViewById(R.id.rep_pic2);
            viewHolder.tvParty = (TextView) convertView.findViewById(R.id.rep_party);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.rep_name);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.rep_title);
            viewHolder.tvEmail = (TextView) convertView.findViewById(R.id.rep_email);
            viewHolder.tvWeb = (TextView) convertView.findViewById(R.id.rep_web);
            viewHolder.tvTweet = (TextView) convertView.findViewById(R.id.rep_tweet);
            viewHolder.vSwitch = (ViewSwitcher) convertView.findViewById(R.id.list_switcher);
            viewHolder.bemail = (Button) convertView.findViewById(R.id.b_email_in_list);
            viewHolder.bemail.setTag(position);
            viewHolder.bweb = (Button) convertView.findViewById(R.id.b_web_in_list);
            viewHolder.bweb.setTag(position);
//            viewHolder.bmore = (Button) convertView.findViewById(R.id.b_more_in_list);
//            viewHolder.bmore.setTag(position);

            viewHolder.bemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int position = (Integer) v.getTag();
                    fragment.bemailPressed(position);
                }
            });

            viewHolder.bweb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int position = (Integer)v.getTag();
                    fragment.bwebPressed(position);
                }
            });

//            viewHolder.bmore.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    int position = (Integer)v.getTag();
//                    fragment.bmorePressed(position);
//                }
//            });

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view (update viewHolder fields)
        Legislator item = getItem(position); //Todo: REPLACE WITH LEGISLATOR


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.profilePic.replace("_normal", ""), viewHolder.ivPic);
        imageLoader.displayImage(item.profilePic.replace("_normal", ""), viewHolder.ivPic2);
//        viewHolder.ivPic.setImageBitmap(item.getProfilePicture());
//        viewHolder.ivPic2.setImageDrawable(null);
        viewHolder.tvParty.setText(item.party);
        viewHolder.tvName.setText(item.firstName +" "+ item.lastName);
        viewHolder.tvTitle.setText(item.getChamber());
        viewHolder.tvEmail.setText(item.email);
        viewHolder.tvWeb.setText(item.website);
        viewHolder.tvTweet.setText(item.tweetText);
        return convertView;
    }


    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see ://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView ivPic;
        ImageView ivPic2;
        TextView tvParty;
        TextView tvName;
        TextView tvTitle;
        TextView tvEmail;
        TextView tvWeb;
        TextView tvTweet;
        ViewSwitcher vSwitch;
        Button bemail;
        Button bweb;
        Button bmore;

    }
}
