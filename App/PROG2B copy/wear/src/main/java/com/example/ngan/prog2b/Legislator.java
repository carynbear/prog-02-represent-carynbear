package com.example.ngan.prog2b;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by ngan on 3/10/16.
 */
public class Legislator implements Serializable{
    String id = null;
    String firstName = null;
    String lastName = null;
    String party = null;
    String chamber = null;
    String profilePicture = null;

    public Legislator() {}

    
    public String getChamber(){
        if (chamber == null) {
            return "null";
        } else if (chamber.equals("house")) {
            return "Representative";
        } else {
            return "Senator";
        }
    }

    public String getParty(){
        if (party.equals("R")) {
            return "Republican";
        } else if (party.equals("D")){
            return "Democratic";
        } else {
            return "Independent";
        }
    }
    

    public Legislator(DataMap map) {
        id = map.getString("id", id);
        firstName = map.getString("firstname", firstName);
        lastName = map.getString("lastname", lastName);
        party = map.getString("party", party);
        chamber = map.getString("chamber", chamber);
        profilePicture = map.getString("profilePicture", profilePicture);
    }

    public static Asset getAsset(DataMap map) {
        return map.getAsset("pictureAsset");
    }
    
}

//    protected Legislator(Parcel in) {
//        id = in.readString();
//        firstName = in.readString();
//        lastName = in.readString();
//        party = in.readString();
//        chamber = in.readString();
//        email = in.readString();
//        website = in.readString();
//        twitter = in.readString();
//        termStart = in.readString();
//        termEnd = in.readString();
//        tweetText = in.readString();
//        profilePic = in.readString();
//        committees = in.createStringArrayList();
//    }

//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(firstName);
//        dest.writeString(lastName);
//        dest.writeString(party);
//        dest.writeString(chamber);
//        dest.writeString(email);
//        dest.writeString(website);
//        dest.writeString(twitter);
//        dest.writeString(termStart);
//        dest.writeString(termEnd);
//        dest.writeString(tweetText);
//        boolean[] bArray = {hasPic, hasTweet, hasCommittees, hasBills};
//        dest.writeBooleanArray(bArray);
//        dest.writeString(profilePic);
//        dest.writeStringList(committees);
//        ArrayList<String> stringBills = new ArrayList<String>();
//        for (String[] bill : bills) {
//            String newBill = bill[0] +"|" +bill[1];
//            stringBills.add(newBill);
//        }
//        dest.writeStringList(stringBills);
//    }
