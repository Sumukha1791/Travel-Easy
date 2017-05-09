package apiproject.csc258.com.traveleasy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * Created by sumukha on 2/7/2017.
 */
public class SecondActivity extends AppCompatActivity {

    JSONObject HotwireJSON;
    ProgressDialog prgDialog;
    CheapestAmountModel cheapestAmount;
    String startDate;
    String endDate;
    String source;
    String destination;
    String startTime;
    String endTime;
    String variant;
    String pickupAirport = new String();
    String uberSourceDescription ;
    BusinessLogic businessLogic = new BusinessLogic();
    double sourceForUberlatitude;
    double sourceForUberlongitude;
    HashMap<String, Double> uberDestinationCordinates = new HashMap<>();

        public String getPickupAirport() { return pickupAirport; }

    public void setPickupAirport(String pickupAirport) { this.pickupAirport = pickupAirport; }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getVariant() {
        return variant;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("traveleasy", "sec created");

        Bundle extras = getIntent().getExtras();
        startDate = (String) extras.get("startDate");
        endDate = (String) extras.get("endDate");
        source = (String) extras.get("source");
        destination = (String) extras.get("destination");
        startTime = (String) extras.get("startTime");
        endTime = (String) extras.get("endTime");
        variant = (String)extras.get("variant");
       // variant = "ECAR";

        RequestParams hotwireparams = new RequestParams();
        hotwireparams.put("apikey", "p3dzxhwpsyb4z5hnm59qx47q");
        hotwireparams.put("dest", "smf");
        hotwireparams.put("startdate", "05/23/2017");
        hotwireparams.put("enddate", "05/25/2017");
        hotwireparams.put("pickuptime", "13:00");
        hotwireparams.put("dropofftime", "13:00");
        hotwireparams.put("format", "JSON");

        prgDialog = new ProgressDialog(SecondActivity.this);
        prgDialog.setTitle("Processing...");
        prgDialog.setMessage("Please Wait");
        prgDialog.setCancelable(false);

        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
               // Log.i("traveleasy","in adapter call");
                //Log.i("travl easy"," uber sou "+uberSourceDescription);
                uberSourceDescription = (String) parent.getItemAtPosition(position);
                Log.i("traveleasy"," places desc "+uberSourceDescription);
               // Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
        });

        try {
            invokeWS(hotwireparams);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();
        prgDialog.dismiss();
        Log.i("two activities", "activity two paused");
    }

    private void invokeWS(RequestParams params) throws IOException {
        prgDialog.show();

        final SabreAPICall sabre = new SabreAPICall(getBaseContext(), this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://api.hotwire.com/v1/search/car", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                // prgDialog.hide();
                try {
                    Log.i("traveleasy", "htwire response: " + response.toString());
                    HotwireJSON = new JSONObject(response);
                    Log.i("stat code", HotwireJSON.getString("StatusCode"));
                    if (HotwireJSON.get("StatusCode").toString().equals("0")) {
                        Log.i("traveleasy", "success!!!!!");
                        Toast.makeText(getApplicationContext(), "awesome", Toast.LENGTH_LONG).show();

                        // sabre.delegate = this;
                        try {
                            sabre.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
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

    }


    public void cheapestFare(final String sabreResultjson) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                prgDialog.hide();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.totalRateTV);

                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                param.addRule(RelativeLayout.BELOW, R.id.totalRateET);
                param.addRule(RelativeLayout.ALIGN_END, R.id.bookingLinkTV);
                param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.bookingLinkTV);
                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                param.addRule(RelativeLayout.ALIGN_BASELINE, R.id.bookingLinkTV);
                param.addRule(RelativeLayout.ALIGN_RIGHT, R.id.bookingLinkTV);

                TextView aggregatorET = (TextView) findViewById(R.id.aggregatorET);
                TextView totalRateET = (TextView) findViewById(R.id.totalRateET);
                TextView bookingLinkET = (TextView) findViewById(R.id.linkToBookET);
                TextView serviceProviderET = (TextView) findViewById(R.id.linkToServiceProviderET);
                TextView linkToServiceProviderET = (TextView) findViewById(R.id.linkToServiceProviderET);
                TextView serviceProviderTV = (TextView) findViewById(R.id.serviceProviderTV);
                TextView linkToServiceProviderTV = (TextView) findViewById(R.id.linkToProviderTV);
                TextView bookingLinkTV = (TextView) findViewById(R.id.bookingLinkTV);


                //   Log.i("traveleasy","ht")
                cheapestAmount = businessLogic.CheapestFare(HotwireJSON.toString(), sabreResultjson, variant, getApplicationContext());

                if (cheapestAmount.getAggregator().equals("hotwire")) {
                    aggregatorET.setText(cheapestAmount.getAggregator());
                    Double amount = cheapestAmount.getAmount();
                    pickupAirport = cheapestAmount.getPickUpAirport();
                    totalRateET.setText(amount.toString());
                    bookingLinkET.setText(cheapestAmount.getDeepLink());
                    serviceProviderET.setVisibility(View.INVISIBLE);
                    linkToServiceProviderET.setVisibility(View.INVISIBLE);
                    linkToServiceProviderTV.setVisibility(View.INVISIBLE);
                    serviceProviderET.setVisibility(View.INVISIBLE);
                    serviceProviderTV.setVisibility(View.INVISIBLE);
                    bookingLinkTV.setLayoutParams(params);
                    //  bookingLinkET.setLayoutParams(param);

                } else if (cheapestAmount.getAggregator().equals("sabre")) {
                    aggregatorET.setText(cheapestAmount.getAggregator());
                    Double amount = cheapestAmount.getAmount();
                    pickupAirport = cheapestAmount.getPickUpAirport();
                    totalRateET.setText(amount.toString());
                    bookingLinkET.setVisibility(View.INVISIBLE);
                    bookingLinkTV.setVisibility(View.INVISIBLE);
                    serviceProviderET.setText(cheapestAmount.getVendor());
                    linkToServiceProviderET.setText("www." + cheapestAmount.getVendor() + ".com");
                } else {
                    Toast.makeText(getApplicationContext(), "error in calculation", Toast.LENGTH_LONG).show();
                }
            }


        });
    }






    public void startThirdActivity(View v) {

        if(uberSourceDescription == null) {
            GPSTracker gps = new GPSTracker(this.getApplicationContext());

            // check if GPS enabled
            if (gps.canGetLocation()) {

                sourceForUberlatitude = gps.getLatitude();
                sourceForUberlongitude = gps.getLongitude();

                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + sourceForUberlatitude + "\nLong: " + sourceForUberlongitude, Toast.LENGTH_LONG).show();

                CordinatesAPI cordinates = new CordinatesAPI();
                cordinates.execute(getPickupAirport(), getApplicationContext(),"dest",this);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();

            }
        }else{

            CordinatesAPI cordinates = new CordinatesAPI();
            cordinates.execute(uberSourceDescription,getApplicationContext(),"source",this);

        }



    }

    public void sourceCordinates(HashMap<String,Double> resultCordinates){
        try {
            //Object resultCordinates = cordinates.get();
            HashMap<String, Double> resultCordinatesHM = (HashMap) resultCordinates;
            Log.i("traveleasy","lat "+resultCordinatesHM.get("latitude"));
            sourceForUberlatitude = resultCordinatesHM.get("latitude");
            sourceForUberlongitude = resultCordinatesHM.get("longitude");

            CordinatesAPI cordinates = new CordinatesAPI();
            cordinates.execute(getPickupAirport(), getApplicationContext(),"dest",this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destCordinates(HashMap<String, Double> resultCordinates){
        //HashMap<String, Double> resultCordinates = null;
        try {
            //resultCordinates = (HashMap<String, Double>) cordinates.get();
            uberDestinationCordinates.put("latitude",resultCordinates.get("latitude"));
            uberDestinationCordinates.put("longitude",resultCordinates.get("longitude"));
            startNewActivity();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startNewActivity(){
            RadioGroup radioGroupOfTravelToCarRentalLocation = (RadioGroup) findViewById(R.id.radioGroup);
            int checkedRadioID = radioGroupOfTravelToCarRentalLocation.getCheckedRadioButtonId();
            Log.i("traveleasy", " ck radio id " + checkedRadioID);

            Intent thirdActivityIntent = new Intent(SecondActivity.this,ThirdActivity.class);
            thirdActivityIntent.putExtra("cheapestamount",cheapestAmount);
            thirdActivityIntent.putExtra("UberSourceLatitude",sourceForUberlatitude);
            thirdActivityIntent.putExtra("UberSourceLongitude",sourceForUberlongitude);
            thirdActivityIntent.putExtra("UberDestLatitude",uberDestinationCordinates.get("latitude"));
            thirdActivityIntent.putExtra("UberDestLongitude",uberDestinationCordinates.get("longitude"));
            thirdActivityIntent.putExtra("checkedRadioID", checkedRadioID);
            Log.i("traveleasy", "ubrla " + uberDestinationCordinates.get("latitude") + " ubrln " + uberDestinationCordinates.get("longitude"));
            startActivity(thirdActivityIntent);
    }

}



