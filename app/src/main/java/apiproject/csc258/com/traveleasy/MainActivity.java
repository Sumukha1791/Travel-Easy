package apiproject.csc258.com.traveleasy;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();
    EditText startDateInput;
    EditText startTimeInput;
    EditText endTimeInput;
    EditText endDateInput;
    EditText sourceInput;
    EditText destinationInput;
    Button submit;
    Spinner variantInput;



    DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            SimpleDateFormat sdf = updateLabel(year, monthOfYear, dayOfMonth);
            startDateInput.setText(sdf.format(myCalendar.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            SimpleDateFormat sdf = updateLabel(year, monthOfYear, dayOfMonth);
            endDateInput.setText(sdf.format(myCalendar.getTime()));
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startDateInput = (EditText) findViewById(R.id.startDateInput);
        startTimeInput = (EditText) findViewById(R.id.startTimeInput);
        endDateInput = (EditText) findViewById(R.id.endDateInput);
        endTimeInput = (EditText) findViewById(R.id.endTimeInput);
        submit = (Button) findViewById(R.id.submit);
        sourceInput = (EditText) findViewById(R.id.source);
        destinationInput = (EditText) findViewById(R.id.destination);
        variantInput = (Spinner) findViewById(R.id.variant);

        /*submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                prgDialog = new ProgressDialog(getApplicationContext());
                prgDialog.setMessage("Wait maddi");
                prgDialog.setCancelable(false);


                //invokeWS(hotwireparams);


            }
        }); */

        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                 DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this,startdate,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

        endDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this,enddate,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

        startTimeInput.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {

                DialogFragment startTimeFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt("Rid",R.id.startTimeInput);
                startTimeFragment.setArguments(args);
                startTimeFragment.show(getFragmentManager(),"TimePicker");

            }
        });

        endTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment endTimeFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt("Rid",R.id.endTimeInput);
                endTimeFragment.setArguments(args);
                endTimeFragment.show(getFragmentManager(), "TimePicker");
            }
        });
    }


    private SimpleDateFormat updateLabel(int year, int monthOfYear, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf;
    }

    public void startActivityTwo(View v){

        String startDate =  startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();
        String source = sourceInput.getText().toString();
        String destination = destinationInput.getText().toString();
        String startTime = startTimeInput.getText().toString();
        String endTime = endTimeInput.getText().toString();
        String variant = variantInput.getSelectedItem().toString();

        TestInternet testnet = new TestInternet(v.getContext());
        testnet.execute();

        Log.i("traveleasy", "strtdate " + startdate + " enddate " + endDate + " source " + source + " dest " + destination + " strtti " + startTime + " endtim " + endTime + " var " + variant);

        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("startDate",startDate);
        intent.putExtra("endDate",endDate);
        intent.putExtra("source",source);
        intent.putExtra("destination",destination);
        intent.putExtra("startTime",startTime);
        intent.putExtra("endTime",endTime);
        intent.putExtra("variant",variant);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
