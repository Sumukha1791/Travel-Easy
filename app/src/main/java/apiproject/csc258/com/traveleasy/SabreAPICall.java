package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.content.res.Resources;

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

/**
 * Created by sumukha on 2/21/2017.
 */
public class SabreAPICall extends AsyncTask {

    public AsyncResponse delegate = null;
    Context context;
    JSONObject jsonpayload;
    SecondActivity secondActivityObj ;
    String resultjson = "no json formed";

    public SabreAPICall(Context context, SecondActivity SO){
        secondActivityObj = SO;
        this.context = context;
    }

    @Override
    protected String doInBackground(Object... params) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams authenticationParams = new RequestParams();
        authenticationParams.put("apiId", "793");
        authenticationParams.put("auth_flow", "client_cred");
        authenticationParams.put("client_id", "VjE6ejR0ZDhwZnd5bTlrZnpzdDpERVZDRU5URVI6RVhU");
        authenticationParams.put("client_secret", "OUkybkJZcm0=");

       /* String[] splitpickupDateTime = secondActivityObj.startDate.split("/");
        String pickupDT = splitpickupDateTime[0]+"-"+splitpickupDateTime[1]+"T"+secondActivityObj.endTime;

        String[] splitdropoffDateTime = secondActivityObj.endDate.split("/");
        String dropoffDT = splitdropoffDateTime[0]+"-"+splitdropoffDateTime[1]+"T"+secondActivityObj.endTime;

        String location = secondActivityObj.source; */

        /*jsonpayload ={
                "OTA_VehAvailRateRQ":{
            "VehAvailRQCore":{
                "QueryType":"Shop",
                        "VehRentalCore":{
                    "PickUpDateTime":"12-21T09:00",
                            "ReturnDateTime":"12-29T11:00",
                            "PickUpLocation":{
                        "LocationCode":"DFW"
                    }
                }
            }
        }
        }*/
        //payload = "{ /" OTA_VehAvailRateRQ /": { /"VehAvailRQCore /": { /"QueryType /": /"Shop /", \ /"VehRentalCore /": { /"PickUpDateTime /": "04-07T09:00", "ReturnDateTime": "04-08T11:00", "PickUpLocation": {"LocationCode": "DFW"}}}}}" ;

        try {
           // jsonpayload = new JSONObject().put("OTA_VehAvailRateRQ",new JSONObject().put("VehAvailRQCore", new JSONObject().put("QueryType","Shop").put("VehRentalCore",new JSONObject().put("PickUpDateTime","04-03T13:00").put("ReturnDateTime","04-08T13:00").put("PickUpLocation", new JSONObject().put("LocationCode","SMF")))));
            jsonpayload = new JSONObject().put("GeoCodeRQ", new JSONObject().put("PlaceById", new JSONObject().put("Id", "SMF").put("BrowseCategory", new JSONObject().put("name", "AIR"))));
            // System.out.println("payload"+payloadjson.toString());
            Log.i("traveleasy","payload json "+jsonpayload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        client.get("https://developer.sabre.com/io-docs/getoauth2accesstoken", authenticationParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                //prgDialog.hide();
                try {
                    //  Log.i("traveleasy", "response: " + response.toString());
                    JSONObject obj = new JSONObject(response);
                    Log.i("traveleasy", "obj response " + obj.toString());
                    if (obj.get("success").toString() == "true") {

                        //  Toast.makeText(context, "sabre awesome", Toast.LENGTH_LONG).show();
                        String token = obj.getJSONObject("result").get("access_token").toString();
                        Log.i("traveleasy", "in token");
                        //calling sabre ground transportation API
                        URL  url = new URL("https://api.test.sabre.com/v2.4.0/shop/cars HTTP/1.1");

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
                        Log.i("traveleasy", "status sabre "+status);
                        InputStream inputStream;

                        if (status != HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getErrorStream();
                            Log.i("traveleasy","error stream "+connection.getPermission());
                            //throw new Exception("Http not ok of sabre");
                            InputStream inputFStream =  context.getResources().openRawResource(R.raw.reference);
                            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputFStream));
                            String resultjson = bufferedReader.readLine();
                           // Log.i("traveleasy", " each line " + eachline);
                            secondActivityObj.cheapestFare(resultjson);
                        } else {
                            inputStream = connection.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                            StringBuffer jsonString = new StringBuffer();
                            String line;
                            while ((line = br.readLine()) != null) {
                                jsonString.append(line);
                            }
                            br.close();
                            connection.disconnect();
                            Log.i("traveleasy", "sabre json  " + jsonString);
                            resultjson = jsonString.toString();
                            secondActivityObj.cheapestFare(resultjson);

                        }


                        // return resultjson;

                    } else {
                        Log.i("traveleasy", "in else sabreapicall got false as status");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    //Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                if (statusCode == 404) {
                    // Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    // Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    // Toast.makeText(context, "error code" + statusCode, Toast.LENGTH_LONG).show();
                    Log.i("traveleasy", "error code " + statusCode + " content " + content);
                }
            }
        });

        Log.i("traveleasy","returing from doinbackground "+resultjson);
        return resultjson;


    }

}
