package com.example.ngan.prog2b;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ngan on 3/10/16.
 */
public class AsyncFetchAddress extends AsyncTask<String, Void, String[]> {
    ProgressDialog dialog;
    MainActivity activity;
    EditText mEditText;

    public AsyncFetchAddress(MainActivity activity, EditText textInput) {
        this.activity = activity;
        mEditText = textInput;
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Getting your location...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        String query;
        Boolean isCoordinates = Boolean.valueOf(params[0]);
        if (isCoordinates) {
            String latitude = params[1];
            String longitude = params[2];
            try {
                query = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                        latitude + "," + longitude +
                        "&result_type=administrative_area_level_2&key=AIzaSyD9hdEcHEu3m7IdU--Hz1jmVfwICpBfAqQ";
                Log.d("query", "" + query);
                response = getCountyStateByURL(query);
                Log.d("response", "" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        } else {
            String zipcode = params[1];
            double lng= -1.0;
            double lat= -1.0;
            try {
                query = "https://maps.googleapis.com/maps/api/geocode/json?address="+zipcode;
                Log.d("query", "" + query);
                response = getCountyStateByURL(query);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    activity.longitude = lng;

                    lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    activity.latitude = lat;
                    activity.gotCoordinates = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (lat != -1.0 && lng != -1.0) {
                    try {
                        query = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                                lat + "," + lng +
                                "&result_type=administrative_area_level_2&key=AIzaSyD9hdEcHEu3m7IdU--Hz1jmVfwICpBfAqQ";
                        Log.d("query", "" + query);
                        response = getCountyStateByURL(query);
                        Log.d("response", "" + response);
                        return new String[]{response};
                    } catch (Exception e) {
                        return new String[]{"error"};
                    }
                } else {
                    Log.d("--ZIP TO COOR--","couldn't get longitude/latitude from zipcode");
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        String TAG = "--POSTFETCHADDRESS--";
        try {
            JSONObject jsonObject = new JSONObject(result[0]);
            String longCounty = null;
            String shortCounty = null;
            Boolean gotCounty = false;
            String longState = null;
            String shortState =null;
            Boolean gotState = false;

            JSONArray addressComponents = ((JSONArray) jsonObject.get("results"))
                    .getJSONObject(0)
                    .getJSONArray("address_components");

            for(int i = 0; i < addressComponents.length(); i++) {
                if (gotCounty && gotState) {
                    break;
                }
                JSONObject obj = addressComponents.getJSONObject(i);
                JSONArray types = obj.getJSONArray("types");
                Boolean isCounty = false;
                Boolean isState = false;
                for (int j = 0; j < types.length(); j ++) {
                    String type = types.getString(j);
                    Log.d("JSONADRESSTYPE", "" + type);
                    if (type.equals("administrative_area_level_2")) {
                        isCounty = true;
                        break;
                    }
                    if (type.equals("administrative_area_level_1")) {
                        isState = true;
                        break;
                    }
                }
                if (!isCounty && !isState) {
                    continue;
                } else if (isCounty) {
                    longCounty = obj.getString("long_name");
                    shortCounty = obj.getString("short_name");
                    gotCounty = true;
                } else {
                    longState = obj.getString("long_name");
                    shortState = obj.getString("short_name");
                    gotState = true;
                }

            }

            if (longCounty != null && shortState != null) {
                Log.d(TAG, "County:"+ " " + longCounty);
                Log.d(TAG, "State:" + " " + longState);
                activity.mAddressOutput = longCounty+", "+ shortState;
                activity.asyncFetchVoteData();
                if (mEditText!=null) {
                    mEditText.setError(null);
//                    mEditText.setHint(longCounty + ", " + shortState);
//                    mEditText.setText(longCounty + ", " + shortState);
                }
            } else {
                Log.d(TAG, "onPostExecute: could not get county and state");
                if (mEditText!=null) {
                    mEditText.setError("invalid location");
                }
            }
            activity.syncAsyncFetch(true, "addr");
        } catch (JSONException e) {
            activity.syncAsyncFetch(false, "addr");
            dialog.dismiss();
            e.printStackTrace();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public String getCountyStateByURL(String requestURL) {
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