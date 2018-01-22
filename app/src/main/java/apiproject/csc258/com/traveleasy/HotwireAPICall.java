package apiproject.csc258.com.traveleasy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by sumuk on 12/26/2017.
 */
public class HotwireAPICall {

    private final SecondActivity secondActivity;
    private HotwireModel hotwire;
    private ProgressDialog prgDialog;
    private JSONObject hotwireJSON;
    private Properties properties = new Properties();
    private Context context;

    public HotwireAPICall(HotwireModel hotwire, Context context, ProgressDialog prgDialog, SecondActivity secondActivity) {
        this.hotwire = hotwire;
        this.context = context;
        this.prgDialog = prgDialog;
        this.secondActivity = secondActivity;
    }

    public void invokeWebService(){

        RequestParams hotwireparams = new RequestParams();
        hotwireparams.put("apikey","p3dzxhwpsyb4z5hnm59qx47q");
        hotwireparams.put("dest", hotwire.source);
        hotwireparams.put("startdate", hotwire.startDate);
        hotwireparams.put("enddate", hotwire.endDate);
        hotwireparams.put("pickuptime", hotwire.startTime);
        hotwireparams.put("dropofftime", hotwire.endTime);
        hotwireparams.put("format", "JSON");

        InputStream inputFileStream = null;
        try {
            inputFileStream = context.getAssets().open("Activity.properties");
            properties.load(inputFileStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final SabreAPICall sabre = new SabreAPICall(context, hotwire, secondActivity);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i("traveleasy","hotwire request "+properties.getProperty("HOTWIRE_URL")+" "+hotwireparams.toString());
        client.get(properties.getProperty("HOTWIRE_URL"), hotwireparams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {

                try {
                    Log.i("traveleasy", "hotwire response: " + response.toString());
                    hotwireJSON = new JSONObject(response);
                    secondActivity.hotwireJSON =hotwireJSON;

                    if (hotwireJSON.get("StatusCode").toString().equals("0")) {
                        Log.i("traveleasy", "success!!!!!");
                        Toast.makeText(context, "Get set go!!!", Toast.LENGTH_LONG).show();
                        try {
                            sabre.execute().get();
                            prgDialog.hide();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("traveleasy", "in else and status code is " + hotwireJSON.get("StatusCode").toString());
                        prgDialog.hide();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Error please try again!!");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // finish();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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
                }
            }
        });
    }
}
