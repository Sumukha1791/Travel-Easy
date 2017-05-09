package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by sumuk on 5/5/2017.
 */
public class CordinatesAPI extends AsyncTask {
    Context c;
    String method;
    SecondActivity so ;

    @Override
    protected HashMap doInBackground(Object[] params) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        String address = (String)params[0];
        method = (String) params[2];
        c = (Context) params[1];
        so = (SecondActivity) params[3];
        final HashMap<String, Double> latitudeAndLogitude = new HashMap<String, Double>();
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams requestParams = new RequestParams();
            requestParams.put("address",address);
            requestParams.put("sensor","false");
            client.get("http://maps.google.com/maps/api/geocode/json",requestParams,new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    try {

                        Log.i("traveleasy","cordinates respoinse "+response);
                        JSONObject responseJSON = new JSONObject(response.toString());
                        JSONArray results = responseJSON.getJSONArray("results");
                        JSONObject firstResult = results.getJSONObject(0);
                        JSONObject geometry = firstResult.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Double latitude = location.getDouble("lat");
                        Double longitude = location.getDouble("lng");
                        latitudeAndLogitude.put("latitude", latitude);
                        latitudeAndLogitude.put("longitude", longitude);
                        if(method.equals("source")){
                            so.sourceCordinates(latitudeAndLogitude);
                        }else {
                            so.destCordinates(latitudeAndLogitude);
                        }
                    }catch (JSONException e){
                        Log.i("traveleasy","error in cordinates api call");
                    }
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if (statusCode == 404) {
                         Toast.makeText(c, "Requested resource not found", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                         Toast.makeText(c, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                         Toast.makeText(c, "error code" + statusCode, Toast.LENGTH_LONG).show();
                        Log.i("traveleasy", "error code " + statusCode + " content " + content);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
            /*StringBuilder sb = new StringBuilder("http://maps.google.com/maps/api/geocode/json");
            //sb.append("?key=" + API_KEY);

            sb.append("?address=" + address);
            sb.append("&sensor="+false);
            //sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                //   Log.i("traveleasy", " sucess from placesapi");
                jsonResults.append(buff, 0, read);
                // Log.i("traveleasy"," json places "+jsonResults.toString());
            }
        } catch (MalformedURLException e) {
            Log.i("traveleasy", "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.i("traveleasy", "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Log.d(TAG, jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject responseJSON = new JSONObject(jsonResults.toString());
            JSONArray results = responseJSON.getJSONArray("results");
            JSONObject firstResult = results.getJSONObject(0);
            JSONObject geometry = firstResult.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            Double latitude = location.getDouble("lat");
            Double longitude = location.getDouble("lng");
            latitudeAndLogitude.put("latitude",latitude);
            latitudeAndLogitude.put("longitude",longitude);

        } catch (JSONException e) {
            Log.i("traveleasy", "Cannot process JSON results", e);
        }
        //  Log.i("traveleasy"," last resultlist before return "+resultList.toString());*/
        return latitudeAndLogitude;
    }

}
