package apiproject.csc258.com.traveleasy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by sumuk on 4/15/2017.
 */
@JsonIgnoreProperties({"distance","display_name","product_id", "high_estimate","duration","estimate","currency_code"})
public class UberResponseModel {
    String localized_display_name;
    double distance;
    String display_name;
    double low_estimate;

    public String getLocalized_display_name() {
        return localized_display_name;
    }

    public void setLocalized_display_name(String localized_display_name) {
        this.localized_display_name = localized_display_name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public double getLow_estimate() {
        return low_estimate;
    }

    public void setLow_estimate(double low_estimate) {
        this.low_estimate = low_estimate;
    }
}

