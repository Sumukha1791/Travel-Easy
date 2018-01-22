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



public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    public static final int TIME_PICKER_INTERVAL=30;
    private boolean mIgnoreEvent=false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
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

        TextView titletv = new TextView(getActivity());
        titletv.setText("Start Time");
        titletv.setBackgroundColor(Color.parseColor("#EEE8AA"));
        titletv.setPadding(5, 3, 5, 3);
        titletv.setGravity(Gravity.CENTER_HORIZONTAL);
        timePickerDialog.setCustomTitle(titletv);
        return timePickerDialog;
    }

    public static int getRoundedMinute(int minute){
        if(minute % TIME_PICKER_INTERVAL != 0){
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)  minute=0;
        }
        return minute;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        Bundle args = getArguments();
        int index = args.getInt("Rid");

        EditText startTimeInput = (EditText) getActivity().findViewById(index);
        startTimeInput.setText("");
        startTimeInput.setText(startTimeInput.getText()+ String.valueOf(hourOfDay)
                + " : " + String.valueOf(minute) + "\n");
    }
}