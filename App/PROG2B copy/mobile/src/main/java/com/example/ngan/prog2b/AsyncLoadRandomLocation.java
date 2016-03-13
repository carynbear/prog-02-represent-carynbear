package com.example.ngan.prog2b;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by ngan on 3/10/16.
 */
public class AsyncLoadRandomLocation extends AsyncTask<String, Void, String[]> {
    MainActivity activity;

    public AsyncLoadRandomLocation(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... params) {
        try {
            InputStream stream = activity.getAssets().open("randomlocations43581.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            return new String[]{jsonString};
        } catch (IOException ioException) {
            //Do something here... couldn't open file
            Log.d("--RANDOM LOCATION--", "couldn't open election json");
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        String TAG = "--POST RANDOM LOC--";
        String latitude;
        String longitude;

        try {
            JSONArray locations = new JSONArray(result[0]);
            Random random = new Random();
            int randomInteger = random.nextInt(43581);
            JSONObject location = (JSONObject) locations.get(randomInteger);
            latitude = location.getString("Latitude");
            longitude = location.getString("Longitude");
            activity.tryNewRandomLocation(latitude, longitude);
            Log.d("--New Random Location--", "Latitude: " + latitude + "  Longitude: " + longitude);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse vote json");
            e.printStackTrace();
        }
    }
}
