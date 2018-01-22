package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.content.res.Resources;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by sumukha on 2/21/2017.
 */
public class SabreAPICall extends AsyncTask {

    public AsyncResponse delegate = null;
    Context context;
    JSONObject jsonpayload;
    SecondActivity secondActivityObj;
    String resultjson = "no json formed";
    Properties properties = new Properties();
    HotwireModel hotwireModel;

    public SabreAPICall(Context context, HotwireModel hotwireModel, SecondActivity secondActivity) {
        this.hotwireModel = hotwireModel;
        this.context = context;
        this.secondActivityObj = secondActivity;
    }

    @Override
    protected String doInBackground(Object... params) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams authenticationParams = new RequestParams();


        try {
            InputStream inputFileStream = context.getAssets().open("SabreAPI.properties");
            properties.load(inputFileStream);
            jsonpayload = new JSONObject().put("GeoCodeRQ", new JSONObject().put("PlaceById", new JSONObject().put("Id", "\"" + hotwireModel.getPickupAirport() + "\"").put("BrowseCategory", new JSONObject().put("name", "AIR"))));
            Log.i("traveleasy", "payload json " + jsonpayload.toString());

            authenticationParams.put("apiId", properties.getProperty("apiId"));
            authenticationParams.put("auth_flow", properties.getProperty("auth_flow"));
            authenticationParams.put("client_id", properties.getProperty("client_id"));
            authenticationParams.put("client_secret", properties.getProperty("client_secret"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        client.get(properties.getProperty("SABRE_API_URL"), authenticationParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                try {
                    //  Log.i("traveleasy", "response: " + response.toString());
                    JSONObject obj = new JSONObject(response);
                    Log.i("traveleasy", "obj response " + obj.toString());
                    if (obj.get("success").toString() == "true") {
                        String token = obj.getJSONObject("result").get("access_token").toString();
                        Log.i("traveleasy", "in token");
                        URL url = new URL("https://api.test.sabre.com/v2.4.0/shop/cars HTTP/1.1");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Authorization", "Bearer " + token);
                        connection.setRequestProperty("Content-Type", "application/json");
                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                        writer.write(jsonpayload.toString());
                        writer.close();

                        int status = connection.getResponseCode();
                        Log.i("traveleasy", "status sabre " + status);
                        InputStream inputStream;

                        if (status != HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getErrorStream();
                            Log.i("traveleasy", "error stream " + connection.getPermission());
                            InputStream inputFStream = context.getResources().openRawResource(R.raw.reference);
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFStream));
                            String resultjson = bufferedReader.readLine();
                            secondActivityObj.cheapestFare(resultjson);
                        } else {
                            inputStream = connection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuffer jsonString = new StringBuffer();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                jsonString.append(line);
                            }
                            bufferedReader.close();
                            connection.disconnect();
                            Log.i("traveleasy", "sabre json  " + jsonString);
                            resultjson = jsonString.toString();
                            secondActivityObj.cheapestFare(resultjson);

                        }
                    } else {
                        Log.i("traveleasy", "in else sabreapicall got false as status");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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

        Log.i("traveleasy", "returing from doinbackground " + resultjson);
        return resultjson;


    }

}
