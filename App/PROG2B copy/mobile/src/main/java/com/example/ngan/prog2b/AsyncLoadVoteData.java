package com.example.ngan.prog2b;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ngan on 3/10/16.
 */
public class AsyncLoadVoteData extends AsyncTask<String, Void, String[]> {
    MainActivity activity;

    public AsyncLoadVoteData(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... params) {
        try {
            InputStream stream = activity.getAssets().open("new-election-county-2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            return new String[]{jsonString, params[0]};
        } catch (IOException ioException) {
            //Do something here... couldn't open file
            Log.d("--VOTE DATA--", "couldn't open election json");
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        String TAG = "--POST LOAD VOTE--";
        String addr = result[1];
        String[] voteInfo;
        try {
            JSONObject votes = new JSONObject(result[0]);
            JSONObject mVoteJSON = votes.getJSONObject(addr);
            String obamaVote = mVoteJSON.getString("obama");
            String romneyVote = mVoteJSON.getString("romney");
            voteInfo = new String[]{obamaVote, romneyVote};
            activity.mVoteInfo = voteInfo;
            activity.syncAsyncFetch(true, "vote");
            Log.d("--VOTE DATA--", "Obama: " + voteInfo[0] + "  Romney: " + voteInfo[1]);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse vote json");
            e.printStackTrace();
        }
    }
}
