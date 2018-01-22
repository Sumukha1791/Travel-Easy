package apiproject.csc258.com.traveleasy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


/**
 * Created by sumukha on 2/7/2017.
 */
public class SecondActivity extends Activity {


    private ProgressDialog prgDialog;
    private CheapestAmountModel cheapestAmount;
    private HotwireModel hotwire = new HotwireModel();
    private String uberSourceDescription ;
    private BusinessLogic businessLogic = new BusinessLogic();
    private double sourceForUberlatitude;
    private double sourceForUberlongitude;
    private HashMap<String, Double> uberDestinationCordinates = new HashMap<>();
    private HotwireAPICall hotwireAPICall;
    JSONObject hotwireJSON;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("traveleasy", "sec created");

        prgDialog = new ProgressDialog(SecondActivity.this);
        prgDialog.setTitle("Processing...");
        prgDialog.setMessage("Please Wait");
        prgDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        hotwire.startDate = (String) extras.get("startDate");
        hotwire.endDate = (String) extras.get("endDate");
        hotwire.source = (String) extras.get("source");
        hotwire.destination = (String) extras.get("destination");
        hotwire.startTime = (String) extras.get("startTime");
        hotwire.endTime = (String) extras.get("endTime");
        hotwire.variant = (String)extras.get("variant");
        hotwireAPICall = new HotwireAPICall(hotwire, getApplicationContext(), prgDialog, this);


        prgDialog.show();

        Log.i("tr","befor auto");
        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uberSourceDescription = (String) parent.getItemAtPosition(position);
                Log.i("traveleasy"," places desc "+uberSourceDescription);
            }
        });

        hotwireAPICall.invokeWebService();
    }

    protected void onPause() {
        super.onPause();
        prgDialog.dismiss();
        Log.i("traveleasy", "activity two paused");
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
                cheapestAmount = businessLogic.CheapestFare(hotwireJSON.toString(), sabreResultjson, hotwire.variant, getApplicationContext());

                if (cheapestAmount.getAggregator().equals("hotwire")) {
                    aggregatorET.setText(cheapestAmount.getAggregator());
                    Double amount = cheapestAmount.getAmount();
                    hotwire.pickupAirport = cheapestAmount.getPickUpAirport();
                    totalRateET.setText(amount.toString());
                    String hyperLink = "<a href=\'"+cheapestAmount.getDeepLink()+"\'> click here to book your car </a>";
                    Spannable s = (Spannable) Html.fromHtml(hyperLink);
                    for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
                        s.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, s.getSpanStart(u), s.getSpanEnd(u), 0);
                    }
                    bookingLinkET.setText(s);
                    bookingLinkET.setMovementMethod(LinkMovementMethod.getInstance());
                    serviceProviderET.setVisibility(View.INVISIBLE);
                    linkToServiceProviderET.setVisibility(View.INVISIBLE);
                    linkToServiceProviderTV.setVisibility(View.INVISIBLE);
                    serviceProviderET.setVisibility(View.INVISIBLE);
                    serviceProviderTV.setVisibility(View.INVISIBLE);
                    bookingLinkTV.setLayoutParams(params);
                } else if (cheapestAmount.getAggregator().equals("sabre")) {
                    aggregatorET.setText(cheapestAmount.getAggregator());
                    Double amount = cheapestAmount.getAmount();
                    hotwire.pickupAirport = cheapestAmount.getPickUpAirport();
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
            if (gps.canGetLocation()) {
                sourceForUberlatitude = gps.getLatitude();
                sourceForUberlongitude = gps.getLongitude();
                CordinatesAPI cordinates = new CordinatesAPI();
                cordinates.execute(hotwire.getPickupAirport(), getApplicationContext(),"dest",this);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Enable GPS or enter the destination!!");
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
        }else{
            CordinatesAPI cordinates = new CordinatesAPI();
            cordinates.execute(uberSourceDescription,getApplicationContext(),"source",this);
        }
    }

    public void sourceCordinates(HashMap<String,Double> resultCordinates){
        try {
            HashMap<String, Double> resultCordinatesHM = (HashMap) resultCordinates;
            Log.i("traveleasy","lat "+resultCordinatesHM.get("latitude"));
            sourceForUberlatitude = resultCordinatesHM.get("latitude");
            sourceForUberlongitude = resultCordinatesHM.get("longitude");
            CordinatesAPI cordinates = new CordinatesAPI();
            cordinates.execute(hotwire.getPickupAirport(), getApplicationContext(),"dest",this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destCordinates(HashMap<String, Double> resultCordinates){
        try {
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



