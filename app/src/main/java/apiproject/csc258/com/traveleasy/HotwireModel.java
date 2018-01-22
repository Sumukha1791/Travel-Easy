package apiproject.csc258.com.traveleasy;

/**
 * Created by sumuk on 12/26/2017.
 */
public class HotwireModel {
    String startDate;
    String endDate;
    String source;
    String destination;
    String startTime;
    String endTime;
    String variant;
    String pickupAirport = new String();

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
}
