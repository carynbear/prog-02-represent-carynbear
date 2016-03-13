package com.example.ngan.prog2b;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ngan on 3/10/16.
 */
public class Legislator{

    static AtomicInteger fetchTweets;
    String id = null;
    String firstName = null;
    String lastName = null;
    String party = null;
    String chamber = null;
    String email = null;
    String website = null;
    String twitter = null;
    String termStart = null;
    String termEnd = null;

    Boolean hasPic = false;
    Boolean hasTweet = false;
    Boolean hasCommittees = false;
    Boolean hasBills = false;
    String profilePic = null;
    String tweetText = "Latest tweet does not exist or is not yet loaded";
    ArrayList<String> committees = null;
    ArrayList<String[]> bills = null;

    Asset picture = null;

    public Legislator() {}

    protected Legislator(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        party = in.readString();
        chamber = in.readString();
        email = in.readString();
        website = in.readString();
        twitter = in.readString();
        termStart = in.readString();
        termEnd = in.readString();
        tweetText = in.readString();
        profilePic = in.readString();
        committees = in.createStringArrayList();
    }

    protected void syncTweetFetch(int done, MainActivity activity, Dialog tweetDialog) {
        int compare = fetchTweets.incrementAndGet();
        if (compare == done) {
            Log.d("-TWEET-", "syncTweetFetch: DONE");
            activity.syncAsyncFetch(true, "tweets");
            tweetDialog.dismiss();
        }
    }

    public void getLatestTweet(AppSession session, final int totalTweets, final MainActivity activity, final Dialog tweetDialog) {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        StatusesService statusesService = twitterApiClient.getStatusesService();
        if (twitter == null) {
            Log.d("--TWITTER--", "No twitter handle");
            syncTweetFetch(totalTweets, activity, tweetDialog);
        }
        statusesService.userTimeline(null, twitter, null, null, null, null, true, null, true, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                List<Tweet> tweets = result.data;
                Log.d("--TWITTER--", "Retrieved tweets: " + String.valueOf(tweets.size()));
                Tweet tweet = tweets.get(0);
                tweetText = tweet.text;
                User tweetUser = tweet.user;
                profilePic = tweetUser.profileImageUrlHttps;
                hasTweet = true;
                hasPic = true;
                Log.d("-LEGISLATOR-", firstName + ", " + tweetText + ", " + profilePic);
                syncTweetFetch(totalTweets, activity, tweetDialog);
            }

            public void failure(TwitterException exception) {
                Log.d("--TWITTER--", "Failure to retrieve tweets");
                tweetDialog.dismiss();
                Toast.makeText(activity, "Could not process request.\nPlease try a different location", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void retrieveProfilePicture(AppSession session){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        twitterApiClient.getAccountService().verifyCredentials(true, false, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                User user = userResult.data;
                profilePic = user.profileImageUrl.replace("_normal", "");
                hasPic = true;
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("--TWITTER--", "Failure to retrieve picture");
            }

        });
    }

    public Bitmap getProfilePicture(){
        // TODO: 3/11/16
        final Bitmap[] myBitmap = new Bitmap[1];
        if (hasPic && profilePic != null) {
            Log.d("--DOWNLOAD PICTURE--", "getProfilePicture: " + profilePic);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(profilePic.replace("_normal",""));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        myBitmap[0] = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        // Log exception
                        myBitmap[0] = null;
                    }
                }
            });
        } else {
            Log.d("--DOWNLOAD PICTURE--", "getProfilePicture: has no link");
        }
        return myBitmap[0];
    }

    public String getChamber(){
        if (chamber == null) {
            return "null";
        } else if (chamber.equals("house")) {
            return "Representative";
        } else {
            return "Senator";
        }
    }


    public String getCommittees(){
        String ans = "";
        String new_line = "\n";
        for (String c : committees) {
            if (c == null) {
                continue;
            }
            ans += c;
            ans += new_line;
            ans += new_line;
        }
        return ans;
    }

    public String getParty(){
        if (party.equals("R")) {
            return "Republican Party";
        } else if (party.equals("D")){
            return "Democratic Party";
        } else {
            return "Independent Party";
        }
    }

    public String getBills(){
        String ans = "";
        String date_indent = "    ";
        String new_line = "\n";
        String open = " [";
        String close = "] ";
        for (String[] b : bills) {
            if (b[0] == null || b[0].equals("null")) {
                continue;
            }
            ans += b[0];
            ans += new_line;
            ans += date_indent;
            ans += open;
            ans += b[1];
            ans += close;
            ans += new_line;
            ans += new_line;
        }
        return ans;
    }

    public Legislator(DataMap map) {
        id = map.getString("id", id);
        firstName = map.getString("firstname", firstName);
        lastName = map.getString("lastname", lastName);
        party = map.getString("party", party);
        chamber = map.getString("chamber", chamber);
        email = map.getString("email", email);
        website = map.getString("website", website);
        twitter = map.getString("twitter", twitter);
        termStart = map.getString("termStart", termStart);
        termEnd = map.getString("termEnd", termEnd);
        tweetText = map.getString("tweetText", tweetText);
        hasPic = map.getBoolean("hasPic", hasPic);
        hasTweet = map.getBoolean("hasTweet", hasTweet);
        hasCommittees = map.getBoolean("hasCommittees", hasCommittees);
        hasBills = map.getBoolean("hasBills", hasBills);
        profilePic = map.getString("profilePic", profilePic);
        committees = map.getStringArrayList("committees");
        ArrayList<String> stringBills = map.getStringArrayList("stringBills");
        bills = new ArrayList<String[]>();
        for (String s : stringBills) {
            String[] bill = s.split("|");
            bills.add(bill);
        }


    }

//    private static Asset createAssetFromBitmap(Bitmap bitmap) {
//        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
//        return Asset.createFromBytes(byteStream.toByteArray());
//    }

    public DataMap putToDataMap(DataMap map) {
        map.putString("id", id);
        map.putString("firstname", firstName);
        map.putString("lastname", lastName);
        map.putString("party", party);
        map.putString("chamber", chamber);
//        map.putString("email", email);
//        map.putString("website", website);
//        map.putString("twitter", twitter);
//        map.putString("termStart", termStart);
//        map.putString("termEnd", termEnd);
//        map.putString("tweetText", tweetText);
//        map.putBoolean("hasPic", hasPic);
//        map.putBoolean("hasTweet", hasTweet);
//        map.putBoolean("hasCommittees", hasCommittees);
//        map.putBoolean("hasBills", hasBills);
        map.putString("profilePicture", profilePic);
//        map.putStringArrayList("committees", committees);
//        ArrayList<String> stringBills = new ArrayList<String>();
//        for (String[] bill : bills) {
//            String newBill = bill[0] +"|" +bill[1];
//            stringBills.add(newBill);
//        }
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        map.putStringArrayList("stringBills", stringBills);
//        picture = createAssetFromBitmap(imageLoader.loadImageSync(profilePic));
//        Log.d("--CREATE ASSET--", "putToDataMap: asset" + picture.toString());
//        map.putAsset("pictureAsset", picture);
        return map;
    }
}
