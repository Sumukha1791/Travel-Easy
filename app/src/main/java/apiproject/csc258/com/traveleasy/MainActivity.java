package apiproject.csc258.com.traveleasy;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
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

public class MainActivity extends Activity {

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
            SimpleDateFormat sdf = updateLabel(year, monthOfYear, dayOfMonth);
            startDateInput.setText(sdf.format(myCalendar.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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



       switch(variant){
            case "economy": variant = "ECAR";
                            break;
            case "mid-size suv": variant = "IFAR";
                            break;
            case "premium": variant = "PCAR";
                            break;
            case "mini van": variant = "MVAR";
                            break;
            case "convertible": variant = "STAR";
                            break;
            default: variant = "ECAR";
        }

        Log.i("traveleasy", "strtdate " + startdate + " enddate " + endDate + " source " + source + " dest " + destination + " strtti " + startTime + " endtim " + endTime + " var " + variant);

        if(startDate.equals("") || endDate.equals("") || source.equals("") || destination.equals("") || startTime.equals("") || endTime.equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("All the input fields are compulsory!!");
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
        else{
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("source", source);
            intent.putExtra("destination", destination);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("variant", variant);

            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
