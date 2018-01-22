package apiproject.csc258.com.traveleasy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sumuk on 3/26/2017.
 */
public class CheapestAmountModel implements Parcelable {
    private double amount;
    private String aggregator;
    private boolean isDeepLink;
    private String deepLink;
    private String vendor;
    private String pickUpAirport;

    public CheapestAmountModel(){

    }
    
    public CheapestAmountModel(Parcel in) {
        aggregator = in.readString();
        deepLink = in.readString();
        vendor = in.readString();
        pickUpAirport = in.readString();
        amount = in.readDouble();
        isDeepLink = in.readByte() != 0;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAggregator() {
        return aggregator;
    }

    public String getPickUpAirport() {
        return pickUpAirport;
    }

    public void setPickUpAirport(String pickUpAirport) {
        this.pickUpAirport = pickUpAirport;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public boolean isDeepLink() {
        return isDeepLink;
    }

    public void setIsDeepLink(boolean isDeepLink) {
        this.isDeepLink = isDeepLink;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public static final Parcelable.Creator<CheapestAmountModel> CREATOR = new Parcelable.Creator<CheapestAmountModel>() {
        public CheapestAmountModel createFromParcel(Parcel in) {
            return new CheapestAmountModel(in);
        }

        public CheapestAmountModel[] newArray(int size) {
            return new CheapestAmountModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aggregator);
        dest.writeString(deepLink);
        dest.writeString(vendor);
        dest.writeString(pickUpAirport);
        dest.writeDouble(amount);
        dest.writeByte((byte) (isDeepLink ? 1 : 0)); ;
    }
}
