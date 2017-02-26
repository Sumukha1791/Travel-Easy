package apiproject.csc258.com.traveleasy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by sumuk on 2/7/2017.
 */
public class SecondActivity extends AppCompatActivity {
    
    ProgressDialog prgDialog;
    String startDate, endDate, source, destination, startTime, endTime, variant ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("two activties", "sec created");

        Bundle extras = getIntent().getExtras();
        startDate = (String)extras.get("startDate");
        endDate = (String)extras.get("endDate");
        source = (String)extras.get("source");
        destination = (String)extras.get("destination");
        startTime = (String)extras.get("startTime");
        endTime = (String)extras.get("endTime");
        variant = (String)extras.get("variant");

        RequestParams hotwireparams = new RequestParams();
        hotwireparams.put("apikey", "p3dzxhwpsyb4z5hnm59qx47q");
        hotwireparams.put("dest","smf");
        hotwireparams.put("startdate", "03/03/2017");
        hotwireparams.put("enddate","03/08/2017");
        hotwireparams.put("pickuptime", "13:00");
        hotwireparams.put("dropofftime", "13:00");
        hotwireparams.put("format", "JSON");


        prgDialog = new ProgressDialog(SecondActivity.this);
        prgDialog.setMessage("Wait maddi");
        prgDialog.setCancelable(false);

        try {
            invokeWS(hotwireparams);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    protected void onPause(){
        super.onPause();
        prgDialog.dismiss();
        Log.i("two activities","activity two paused");
    }

    private void invokeWS(RequestParams params) throws IOException {
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://api.hotwire.com/v1/search/car", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                try {
                    Log.i("traveleasy", "response: " + response.toString());
                    JSONObject obj = new JSONObject(response);
                    Log.i("stat code", obj.getString("StatusCode"));
                    if (obj.get("StatusCode").toString().equals("0")) {
                        Log.i("traveleasy", "success!!!!!");
                        Toast.makeText(getApplicationContext(), "awesome", Toast.LENGTH_LONG).show();

                    } else {
                        Log.i("traveleasy", "in else");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "error code" + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });

        SabreAPICall sabre = new SabreAPICall(getBaseContext());
        sabre.execute();


    }
}


