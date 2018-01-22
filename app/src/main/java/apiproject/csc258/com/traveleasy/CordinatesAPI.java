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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by sumuk on 5/5/2017.
 */
public class CordinatesAPI extends AsyncTask {
    private Context context;
    private String method;
    private SecondActivity secondActivity;
    private Properties properties = new Properties();

    @Override
    protected HashMap doInBackground(Object[] params) {
        String address = (String) params[0];
        method = (String) params[2];
        context = (Context) params[1];
        secondActivity = (SecondActivity) params[3];

        final HashMap<String, Double> latitudeAndLogitude = new HashMap<String, Double>();
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            InputStream input = context.getAssets().open("Cordinates.properties");
            properties.load(input);
            RequestParams requestParams = new RequestParams();
            requestParams.put("address", address);
            requestParams.put("sensor", "false");
            client.get(properties.getProperty("client.address"), requestParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    try {

                        Log.i("traveleasy", "cordinates response " + response);
                        JSONObject responseJSON = new JSONObject(response.toString());
                        JSONArray results = responseJSON.getJSONArray("results");
                        JSONObject firstResult = results.getJSONObject(0);
                        JSONObject geometry = firstResult.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Double latitude = location.getDouble("lat");
                        Double longitude = location.getDouble("lng");
                        latitudeAndLogitude.put("latitude", latitude);
                        latitudeAndLogitude.put("longitude", longitude);
                        if (method.equals("source")) {
                            secondActivity.sourceCordinates(latitudeAndLogitude);
                        } else {
                            secondActivity.destCordinates(latitudeAndLogitude);
                        }
                    } catch (JSONException e) {
                        Log.i("traveleasy", "error in cordinates api call");
                    }
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if (statusCode == 404) {
                        Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "error code" + statusCode, Toast.LENGTH_LONG).show();
                        Log.i("traveleasy", "error code " + statusCode + " content " + content);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latitudeAndLogitude;
    }

}
