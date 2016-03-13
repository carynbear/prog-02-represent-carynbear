package com.example.ngan.prog2b;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ngan on 3/10/16.
 */
public class AsyncFetchLegislators extends AsyncTask<String, Void, String[]> {
    ProgressDialog dialog;
    MainActivity activity;
    Boolean isCoordinates;

    public AsyncFetchLegislators(MainActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Getting your legislators...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        String query;
        isCoordinates = Boolean.valueOf(params[0]);
        if (isCoordinates) {
            String latitude = params[1];
            String longitude = params[2];
            try {
                query = "https://congress.api.sunlightfoundation.com/legislators/locate?latitude=" + latitude +
                        "&longitude=" + longitude +
                        "&apikey=2d8a83f7c18d43cba9c56c3df8b1cb96";
                Log.d("query", "" + query);
                response = getRepsByURL(query);
                Log.d("response", "" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        } else {
            String zipcode = params[1];
            try {
                query = "https://congress.api.sunlightfoundation.com/legislators/locate?zip="
                        +zipcode+ "&apikey=2d8a83f7c18d43cba9c56c3df8b1cb96";
                Log.d("query", "" + query);
                response = getRepsByURL(query);
                if (response.equals("{\"results\":[],\"count\":0}")) {
                    String latitude = params[3];
                    String longitude = params[4];
                    try {
                        query = "https://congress.api.sunlightfoundation.com/legislators/locate?latitude=" + latitude +
                                "&longitude=" + longitude +
                                "&apikey=2d8a83f7c18d43cba9c56c3df8b1cb96";
                        Log.d("query", "" + query);
                        response = getRepsByURL(query);
                        Log.d("response", "" + response);
                        return new String[]{response};
                    } catch (Exception e) {
                        return new String[]{"error"};
                    }
                }
                Log.d("response", "" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        String TAG = "--POSTFETCHREPS--";
        try {
            JSONObject jsonObject = new JSONObject(result[0]);
            JSONArray legislators = ((JSONArray) jsonObject.get("results"));
            activity.mLegislators= new ArrayList<Legislator>();
            activity.mLegislatorsMap = new HashMap<String, Legislator>();
            for(int i = 0; i < legislators.length(); i++) {
                JSONObject lObj = legislators.getJSONObject(i);
                Legislator l = new Legislator();
                l.id = lObj.getString("bioguide_id");
                l.firstName = lObj.getString("first_name");
                l.lastName = lObj.getString("last_name");
                l.email = lObj.getString("oc_email");
                l.website = lObj.getString("website");
                l.twitter = lObj.getString("twitter_id");
                l.party = lObj.getString("party");
                l.chamber = lObj.getString("chamber");
                l.termStart = lObj.getString("term_start");
                l.termEnd = lObj.getString("term_end");
                activity.mLegislators.add(l);
                activity.mLegislatorsMap.put(l.id, l);
            }
            activity.syncAsyncFetch(true, "legislators");
            activity.asyncFetchLegislatorData();
            Log.d(TAG, "Added " + String.valueOf(activity.mLegislators.size()) + " legislators to mLegislators");
        } catch (JSONException e) {
            e.printStackTrace();
            String error = "zipcodeLegislators";
            if (isCoordinates) {
                error = "legislators";
                Toast.makeText(activity, "Please try a different location", Toast.LENGTH_LONG).show();
            }
            activity.syncAsyncFetch(false, error);
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public String getRepsByURL(String requestURL) {
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
        Log.d("--RESPONSE--", response);
        return response;
    }
}