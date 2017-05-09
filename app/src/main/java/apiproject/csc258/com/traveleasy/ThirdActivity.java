package apiproject.csc258.com.traveleasy;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.google.android.gms.maps.model.LatLng;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sumuk on 3/28/2017.
 */
public class ThirdActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener{
        private String sourceLatitude;
        private String sourceLongitude;
        private String destinationLatitude;
        private String destinationLongitude;
        private CheapestAmountModel detailsFromSecondActivity;
        private com.google.android.gms.maps.MapFragment mapFragment;
        private GoogleMap googleMap;
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.accent,R.color.primary_dark_material_light};

    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle extras = getIntent().getExtras();
            sourceLatitude = ((Double)extras.get("UberSourceLatitude")).toString();
            sourceLongitude =  ((Double) extras.get("UberSourceLongitude")).toString();
            destinationLatitude = ((Double)  extras.get("UberDestLatitude")).toString();
            destinationLongitude =  ((Double)  extras.get("UberDestLongitude")).toString();
            int radioID = Integer.parseInt(extras.get("checkedRadioID").toString());
            detailsFromSecondActivity = getIntent().getParcelableExtra("cheapestamount");
            Log.i("traveleasy","de "+detailsFromSecondActivity.getAggregator());

            if(R.id.uber == radioID){
                setContentView(R.layout.activity_third);
                Log.i("traveleasy","in 3rd activiy");
                calculateUberCost();
            }else{
                setContentView(R.layout.activity_thirdgmap);
                displayMap();
            }
        }

    private void displayMap() {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            Log.i("traveleasy", "map fragment " + mapFrag + "getsupp " + getSupportFragmentManager() + " frgid " + getSupportFragmentManager().findFragmentById(R.id.map));
            mapFrag.getMapAsync(this);
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();// do your map stuff here
    }

    private void setUpMap() {
        LatLng source = new LatLng(Double.parseDouble(sourceLatitude),Double.parseDouble(sourceLongitude));
        LatLng destination = new LatLng(Double.parseDouble(destinationLatitude),Double.parseDouble(destinationLongitude));

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(source,destination)
                .key("AIzaSyBwtsmpFhecLS-6OwoEz4bw9LrKordf5As")
                .build();
        routing.execute();

        polylines = new ArrayList<>();
    }

   // @Override
   /* public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        LatLng source = new LatLng(Double.parseDouble(sourceLatitude),Double.parseDouble(sourceLongitude));
    }*/


    private void calculateUberCost() {
            RequestParams params = new RequestParams();
            params.put("start_latitude",sourceLatitude);
            params.put("start_longitude",sourceLongitude);
            params.put("end_latitude",destinationLatitude);
            params.put("end_longitude", destinationLongitude);

           // params("client_id",eKE_chrSkTiT3JjDJLh2wm_O4yWfdfOs);
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization","Token PsyjruVvKrJZpBjuynhBL9nZ8UuWUfbPv5Vj8g-4");
            client.addHeader("Accept-Language","en_US");
            client.addHeader("Content-Type","application/json");
            Log.i("traveleasy", "in 3rd activiy before calling");
            client.get("https://api.uber.com/v1.2/estimates/price", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.i("traveleasy", "uber response " + response);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JSONObject responseJSON = new JSONObject(response);
                        JSONArray prices = responseJSON.getJSONArray("prices");
                        Collection<UberResponseModel> lowEstimates = mapper.readValue(prices.toString(), new TypeReference<Collection<UberResponseModel>>() {
                        });
                        Log.i("traveleasy", "uberresponse " + lowEstimates.iterator().next().getLow_estimate());
                        updateUI(lowEstimates.iterator().next().getLow_estimate());

                    } catch (Exception e) {
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

    private void updateUI(double uberCharges) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.UberCostsTV);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.BELOW, R.id.UberCostsTV);
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
        TextView uberCostsET = (TextView) findViewById(R.id.UberCosts);

        //   Log.i("traveleasy","ht")
       // cheapestAmount = businessLogic.CheapestFare(HotwireJSON.toString(), sabreResultjson, variant);

        if (detailsFromSecondActivity.getAggregator().equals("hotwire")) {
            aggregatorET.setText(detailsFromSecondActivity.getAggregator());
            Double amount = detailsFromSecondActivity.getAmount();
            uberCostsET.setText(amount+uberCharges+"");
            String pickupAirport = detailsFromSecondActivity.getPickUpAirport();
            totalRateET.setText(amount.toString());
            bookingLinkET.setText(detailsFromSecondActivity.getDeepLink());
            serviceProviderET.setVisibility(View.INVISIBLE);
            linkToServiceProviderET.setVisibility(View.INVISIBLE);
            linkToServiceProviderTV.setVisibility(View.INVISIBLE);
            serviceProviderET.setVisibility(View.INVISIBLE);
            serviceProviderTV.setVisibility(View.INVISIBLE);
            bookingLinkTV.setLayoutParams(params);
            //  bookingLinkET.setLayoutParams(param);

        } else if (detailsFromSecondActivity.getAggregator().equals("sabre")) {
            aggregatorET.setText(detailsFromSecondActivity.getAggregator());
            Double amount = detailsFromSecondActivity.getAmount();
            uberCostsET.setText(amount+uberCharges+"");
            String pickupAirport = detailsFromSecondActivity.getPickUpAirport();
            totalRateET.setText(amount.toString());
            bookingLinkET.setVisibility(View.INVISIBLE);
            bookingLinkTV.setVisibility(View.INVISIBLE);
            serviceProviderET.setText(detailsFromSecondActivity.getVendor());
            linkToServiceProviderET.setText("www." + detailsFromSecondActivity.getVendor() + ".com");
        } else {
            Toast.makeText(getApplicationContext(), "error in calculation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Log.i("traveleasy","in routing failure"+e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Log.i("traveleasy","in routing failure else");
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        LatLng source = new LatLng(Double.parseDouble(sourceLatitude),Double.parseDouble(sourceLongitude));
        LatLng destination = new LatLng(Double.parseDouble(destinationLatitude),Double.parseDouble(destinationLongitude));
        CameraUpdate center = CameraUpdateFactory.newLatLng(source);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);


      /*  if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }*/

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

        // Start` marker
        MarkerOptions options = new MarkerOptions();
        options.position(source);

        googleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(destination);

        googleMap.addMarker(options);


    }

    @Override
    public void onRoutingCancelled() {

    }
}
