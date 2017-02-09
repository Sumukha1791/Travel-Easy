package apiproject.csc258.com.traveleasy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by sumuk on 2/7/2017.
 */
public class SecondActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("two activties", "sec created");
    }

    protected void onPause(){
        super.onPause();
        Log.i("two activities","activity two paused");
    }

}
