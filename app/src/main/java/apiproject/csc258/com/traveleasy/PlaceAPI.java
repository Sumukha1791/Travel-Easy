package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by sumuk on 5/2/2017.
 */
public class PlaceAPI {

    Properties properties = new Properties();
    private String placesAPI;
    private String typeAutoComplete;
    private String outJSON;
    private String APIKey;

    PlaceAPI(){
        placesAPI = properties.getProperty("PLACES_API_BASE");
        typeAutoComplete = properties.getProperty("TYPE_AUTOCOMPLETE");
        outJSON = properties.getProperty("OUT_JSON");
        APIKey = properties.getProperty("API_KEY");
    }

    public ArrayList<String> autocomplete (String input, Context context) {
        ArrayList<String> resultList = null;
        HttpURLConnection connection = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            InputStream inputFileStream = context.getAssets().open("Places.properties");
            properties.load(inputFileStream);
            placesAPI = properties.getProperty("PLACES_API_BASE");
            typeAutoComplete = properties.getProperty("TYPE_AUTOCOMPLETE");
            outJSON = properties.getProperty("OUT_JSON");
            APIKey = properties.getProperty("API_KEY");
            StringBuilder stringBuilder = new StringBuilder(placesAPI + typeAutoComplete + outJSON);
            stringBuilder.append("?key=" + APIKey);
            stringBuilder.append("&input=" + URLEncoder.encode(input, "utf8"));

            Log.i("traveleasy","string builder url "+stringBuilder.toString());
            URL url = new URL(stringBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.i("traveleasy", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.i("traveleasy", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predictionsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList<String>(predictionsJsonArray.length());
            for (int i = 0; i < predictionsJsonArray.length(); i++) {
                resultList.add(predictionsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.i("traveleasy", "Cannot process JSON results", e);
        }
        return resultList;
    }
}
