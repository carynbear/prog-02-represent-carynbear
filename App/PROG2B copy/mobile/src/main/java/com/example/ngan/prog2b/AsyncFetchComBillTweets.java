package com.example.ngan.prog2b;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ngan on 3/10/16.
 */
public class AsyncFetchComBillTweets extends AsyncTask<String, Void, String[]> {
    ProgressDialog dialog;
    ProgressDialog tweetDialog;
    MainActivity activity;
    ArrayList<Legislator> mLegislators;

    public AsyncFetchComBillTweets(MainActivity activity,
                                   ArrayList<Legislator> legislators) {
        this.activity = activity;
        this.mLegislators = legislators;
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Loading your legislators...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if(mLegislators == null) {
            dialog.dismiss();
            Toast.makeText(activity, "Please try a different location", Toast.LENGTH_LONG).show();
            cancel(true);
        }
    }

    @Override
    protected String[] doInBackground(String... params) {
        ArrayList<String> returnArray = new ArrayList<String>();
        int i = 0;
        for (Legislator l : mLegislators) {
            String committeeResponse;
            String billsResponse;
            String query;
            String id = l.id;
            String [] concatToReturn;
            try {
                query = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + id +
                        "&apikey=2d8a83f7c18d43cba9c56c3df8b1cb96";
                Log.d("query", "" + query);
                committeeResponse = getDataByURL(query);
                Log.d("committeeResponse", id);
            } catch (Exception e) {
                concatToReturn = new String[]{"error"};
                continue;
            }

            try {
                query = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + id +
                        "&apikey=2d8a83f7c18d43cba9c56c3df8b1cb96";
                Log.d("query", "" + query);
                billsResponse = getDataByURL(query);
                Log.d("billsResponse", id);
            } catch (Exception e) {
                concatToReturn = new String[]{"error"};
                continue;
            }

            returnArray.add(committeeResponse);
            returnArray.add(billsResponse);
        }
        String[] template = new String[returnArray.size()];
        return returnArray.toArray(template);
    }

    @Override
    protected void onPostExecute(String... result) {
        String TAG = "--FETCH COM--";
        if (mLegislators.size() == 0) {
            dialog.dismiss();
            Toast.makeText(activity, "Please try a different location", Toast.LENGTH_LONG).show();
            return;
        }
        final int totalTweets = mLegislators.size();
        final ProgressDialog tweetDialog = new ProgressDialog(activity);
        tweetDialog.setMessage("Gathering tweet data...");
        tweetDialog.setCanceledOnTouchOutside(false);
        tweetDialog.show();
        TwitterCore.getInstance().logInGuest(new Callback() {
            @Override
            public void success(Result result) {
                AppSession session = (AppSession) result.data;
                Legislator.fetchTweets = new AtomicInteger(0);
                for (Legislator l : mLegislators) {
                    l.getLatestTweet(session, totalTweets, activity, tweetDialog);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("--TWITTER--", "displayLegislatorOutput(): failure to establish session");
            }
        });

        int l = 0;
        for (int r = 0; r < result.length; r += 2) {
            try {
                JSONObject jsonObject = new JSONObject(result[r]);
                JSONArray commitees = ((JSONArray) jsonObject.get("results"));
                ArrayList<String> returnCommittees = new ArrayList<String>();

                for (int i = 0; i < commitees.length(); i++) {
                    JSONObject lObj = commitees.getJSONObject(i);
                    returnCommittees.add(lObj.getString("name"));
                }
                mLegislators.get(l).committees = returnCommittees;
                Log.d(TAG, "Added " + String.valueOf(returnCommittees.size()) + " committees for " + mLegislators.get(l).firstName);

                jsonObject = new JSONObject(result[r+1]);
                JSONArray bills = ((JSONArray) jsonObject.get("results"));
                ArrayList<String[]> returnBills = new ArrayList<String[]>();
                for (int i = 0; i < bills.length(); i++) {
                    JSONObject lObj = bills.getJSONObject(i);
                    String[] entry = {lObj.getString("short_title"), lObj.getString("introduced_on")};
                    returnBills.add(entry);
                }
                mLegislators.get(l).bills = returnBills;
                Log.d(TAG, "Added " + String.valueOf(returnBills.size()) + " bills for " + mLegislators.get(l).firstName);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            l = l + 1;
        }

        activity.syncAsyncFetch(true, "ComBills");

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public String getDataByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.d("--CONNECT---", "Could not connect to "+ requestURL);
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}