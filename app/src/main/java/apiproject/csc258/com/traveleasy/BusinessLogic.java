package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sumuk on 3/4/2017.
 */
public class BusinessLogic {
    private String variant;
    private String hotwireDeepLink;
    private double hotwireAmount = 0;
    private String hotwirePickupAirport;
    private String sabreVendor;
    private double sabreAmount = 0;
    private String sabrePickupAirport;
    private CheapestAmountModel cheapestAmount = new CheapestAmountModel();
    private Context applicationContext;

    CheapestAmountModel CheapestFare(String hotwireJSONString, String sabreResultJson, String variant, Context context) {
        this.variant = variant;
        this.applicationContext = context;
        JSONObject hotwireJSON = null;
        try {
            hotwireJSON = new JSONObject(hotwireJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CheapestInSabre(sabreResultJson);
        CheapestInHotwire(hotwireJSON);
        if (hotwireAmount != 0 && sabreAmount != 0) {
            if (hotwireAmount > sabreAmount) {
                cheapestAmount.setAggregator("sabre");
                cheapestAmount.setAmount(sabreAmount);
                cheapestAmount.setVendor(sabreVendor);
                cheapestAmount.setIsDeepLink(false);
                cheapestAmount.setPickUpAirport(sabrePickupAirport);
            } else {
                cheapestAmount.setIsDeepLink(true);
                cheapestAmount.setAmount(hotwireAmount);
                cheapestAmount.setDeepLink(hotwireDeepLink);
                cheapestAmount.setPickUpAirport(hotwirePickupAirport);
                cheapestAmount.setAggregator("hotwire");
            }
        } else {
            Log.i("traveleasy", "Error!!! cheapest amount not calculated");
        }
        return cheapestAmount;
    }

    private void CheapestInHotwire(JSONObject hotwireJSON) {
        try {
            JSONArray result = hotwireJSON.getJSONArray("Result");
            for (int i = 0; i < result.length(); i++) {
                if (result.getJSONObject(i).getString("CarTypeCode").equals(variant)) {
                    hotwireAmount = result.getJSONObject(i).getDouble("TotalPrice");
                    hotwireDeepLink = result.getJSONObject(i).getString("DeepLink");
                    hotwirePickupAirport = result.getJSONObject(i).getString("PickupAirport");
                    Log.i("traveleasy", "ht cheapest " + hotwireAmount + " deeplink " + hotwireDeepLink);
                }
            }
            if (hotwireAmount == 0) {
                Toast.makeText(applicationContext, "No car found of type " + variant, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CheapestInSabre(String sabreResultJson) {
        TreeMap<Double, String> minAmountHM = new TreeMap<>();
        try {
            JSONObject sabreJSON = new JSONObject(sabreResultJson);
            JSONObject vehAvailRate = sabreJSON.getJSONObject("OTA_VehAvailRateRS");
            JSONObject vehAvailRSCore = vehAvailRate.getJSONObject("VehAvailRSCore");
            JSONObject vehRentalCore = vehAvailRSCore.getJSONObject("VehRentalCore");
            JSONObject dropOffLocationDetails = vehRentalCore.getJSONObject("DropOffLocationDetails");
            sabrePickupAirport = dropOffLocationDetails.getString("LocationCode");
            JSONObject vehVendorAvails = vehAvailRSCore.getJSONObject("VehVendorAvails");
            JSONArray vehVendorAvail = vehVendorAvails.getJSONArray("VehVendorAvail");
            double minamount = 0;

            for (int i = 0; i < vehVendorAvail.length(); i++) {
                JSONObject vehVendorAvailJSONArray = vehVendorAvail.getJSONObject(i);
                JSONObject vehAvailCore = vehVendorAvailJSONArray.getJSONObject("VehAvailCore");
                JSONObject renatlRate = vehAvailCore.getJSONObject("RentalRate");
                JSONObject vehicle = renatlRate.getJSONObject("Vehicle");
                JSONArray vehType = vehicle.getJSONArray("VehType");
                String typeOfVehicle = vehType.toString();

                if (typeOfVehicle.substring(2, 6).equals(variant)) {
                    JSONObject vehicleCharges = vehAvailCore.getJSONObject("VehicleCharges");
                    JSONObject vehicleCharge = vehicleCharges.getJSONObject("VehicleCharge");
                    JSONObject totalCharge = vehicleCharge.getJSONObject("TotalCharge");
                    double amount = totalCharge.getDouble("Amount");
                    JSONObject vendor = vehVendorAvailJSONArray.getJSONObject("Vendor");
                    String companyName = vendor.getString("CompanyShortName");
                    minAmountHM.put(amount, companyName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sabreAmount = minAmountHM.firstEntry().getKey();
        sabreVendor = minAmountHM.firstEntry().getValue();
    }
}
