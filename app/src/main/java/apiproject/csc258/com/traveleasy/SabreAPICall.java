package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by sumuk on 2/21/2017.
 */
public class SabreAPICall extends AsyncTask {

    Context context;
    String payload;
    SecondActivity secondActivityObj = new SecondActivity();
    public SabreAPICall(Context context){

        this.context = context;
    }

    @Override
    protected Void doInBackground(Object... params) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams authenticationParams = new RequestParams();
        authenticationParams.put("apiId", "793");
        authenticationParams.put("auth_flow", "client_cred");
        authenticationParams.put("client_id", "VjE6ejR0ZDhwZnd5bTlrZnpzdDpERVZDRU5URVI6RVhU");
        authenticationParams.put("client_secret", "OUkybkJZcm0=");

        String[] splitpickupDateTime = secondActivityObj.startDate.split("/");
        String pickupDT = splitpickupDateTime[0]+"-"+splitpickupDateTime[1]+"T"+secondActivityObj.startTime;

        String payload2 =

        payload = "{\n" +
                "\n" +
                "    \"OTA_VehAvailRateRQ\": {\n" +
                "\n" +
                "      \"VehAvailRQCore\": {\n" +
                "\n" +
                "        \"QueryType\": \"Shop\",\n" +
                "\n" +
                "        \"VehRentalCore\": {\n" +
                "\n" +
                "          \"PickUpDateTime\":" +
                pickupDT + "\"\n" +
                "\n" +
                "          \"ReturnDateTime\": \"04-08T11:00\",\n" +
                "\n" +
                "          \"PickUpLocation\": {\n" +
                "\n" +
                "            \"LocationCode\": \"DFW\"\n" +
                "\n" +
                "          }\n" +
                "\n" +
                "        }\n" +
                "\n" +
                "      }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "}";
        URL url = null;

        client.get("https://developer.sabre.com/io-docs/getoauth2accesstoken", authenticationParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                //prgDialog.hide();
                try {
                    //  Log.i("traveleasy", "response: " + response.toString());
                    JSONObject obj = new JSONObject(response);
                    // Log.i("stat code", obj.get("StatusCode").toString());
                    if (obj.get("success") == true) {
                        Log.i("traveleasy", "success!!!!!");
                        //  Toast.makeText(context, "sabre awesome", Toast.LENGTH_LONG).show();
                        String token = obj.get("access_token").toString();

                        //calling sabre ground transportation API
                        URL url = new URL("https://api.test.sabre.com/v2.4.0/shop/cars");

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Authorization", "Bearer T1RLAQIXz6QJX2TblnR7q8omsSuQG0qBDxBLptJRotyFfD+8uFs1AIlMAADACSDj6rUQUnMOWjJ+I8EUxr+B3ddGU/7ExuHfczsbLLc3rFH4lyxYe1/eAVxdl9S2ms+lE7wkB6TmqnvJoTCRbbRdvLLTBsCC26H+w1mfumjiV3BdGdvrx2riwKCZia6ze4DoGkhh6I0xnLW+mg45384gIarGNPktnE6OBFqh1e2e1mlJqVqF0F05B7F/oA+ezHB0gSqp9vqFP2JivNbHAtGWOzarUyU3jui61Wf5MctMHipEggpg3//WbD0+YPMe");
                        connection.setRequestProperty("Content-Type", "application/json");
                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                        writer.write(payload);
                        writer.close();
                        int status = connection.getResponseCode();
                        InputStream inputStream;

                        if (status != HttpURLConnection.HTTP_OK) {
                            inputStream = connection.getErrorStream();
                        } else {
                            inputStream = connection.getInputStream();
                        }
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuffer jsonString = new StringBuffer();
                        String line;
                        while ((line = br.readLine()) != null) {
                            jsonString.append(line);
                        }
                        br.close();
                        connection.disconnect();
                        Log.i("traveleasy", "sabre json  " + jsonString);

                    } else {
                        Log.i("traveleasy", "in else");
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

    return null;
    }
}
