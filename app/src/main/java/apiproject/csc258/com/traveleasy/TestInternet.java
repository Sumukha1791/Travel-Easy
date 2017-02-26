package apiproject.csc258.com.traveleasy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sumuk on 2/18/2017.
 */
public class TestInternet extends AsyncTask {

    private Exception exception;
    Context newcontext;

    TestInternet(Context oldcontext){
        newcontext = oldcontext;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    Log.i("traveleasy","connected successfuly");

                    return "connected"; }
            } catch (IOException e) {
                Log.e("traveleasy", "Error checking internet connection", e);
            }
        } else {
            Log.d("traveleasy", "No network available!");
        }
  //      Toast.makeText(newcontext,"unsucessful connection",Toast.LENGTH_LONG).show();
        Log.i("traveleasy","not connected");
        return "unsuccessfull in connection";
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) newcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void onPostExecute(){
        Log.i("traveleasy", "connected successfuly in post exe");
    }
}
