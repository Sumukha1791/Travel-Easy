package apiproject.csc258.com.traveleasy;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    public static final int TIME_PICKER_INTERVAL=30;
    private boolean mIgnoreEvent=false;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        //Create and return a new instance of TimePickerDialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
                , this, hour, minute, DateFormat.is24HourFormat(getActivity())){

            public void onTimeChanged(TimePicker timePicker,
                                      int hourOfDay,
                                      int minute){
                super.onTimeChanged(timePicker, hourOfDay, minute);
                this.setTitle("2. Select Time");
                if (!mIgnoreEvent){
                    minute = getRoundedMinute(minute);
                    mIgnoreEvent=true;
                    timePicker.setCurrentMinute(minute);
                    mIgnoreEvent=false;
                }
            }
        };

        //You can set a simple text title for TimePickerDialog
        //tpd.setTitle("Title Of Time Picker Dialog");

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("Start Time");
        tvTitle.setBackgroundColor(Color.parseColor("#EEE8AA"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        /*.........End custom title section........*/

        return tpd;
    }

    public static int getRoundedMinute(int minute){
        if(minute % TIME_PICKER_INTERVAL != 0){
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)  minute=0;
        }

        return minute;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
       // TextView tv = (TextView) getActivity().findViewById(R.id.tv);
        Bundle args = getArguments();
        int index = args.getInt("Rid");

        EditText startTimeInput = (EditText) getActivity().findViewById(index);
        //Log.i("timepickerfragment","r: "+R.id.startTimeInput+" getact:"+);

        startTimeInput.setText("");
        //Set a message for user
        //tv.setText("Your chosen time is...\n\n");
        //Display the user changed time on TextView
        startTimeInput.setText(startTimeInput.getText()+ String.valueOf(hourOfDay)
                + " : " + String.valueOf(minute) + "\n");
    }
}