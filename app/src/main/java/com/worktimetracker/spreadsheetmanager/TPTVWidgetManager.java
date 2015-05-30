package com.worktimetracker.spreadsheetmanager;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility class for managing the TimePicker Dialog listeners and TextView disabled states
 */
public class TPTVWidgetManager
{
    private Context context;

    private TimePickerDialog timeInDialog, breakInDialog, breakOutDialog, timeOutDialog;

    private SharedPreferences.Editor editor;

    private String[] spreadSheetColumns;

    private DateTimeFormatter fmtDate, fmtTime;

    //private DateTime dtToday, dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut;

    private ImageButton btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut;

    private Button btnSubmit;

    private TextView tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut;

    public TPTVWidgetManager(Context context,
                             TextView tvTimeIn,
                             TextView tvBreakIn,
                             TextView tvBreakOut,
                             TextView tvTimeOut,
                             ImageButton btnSelTimeIn,
                             ImageButton btnSelBreakIn,
                             ImageButton btnSelBreakOut,
                             ImageButton btnSelTimeOut,
                             Button btnSubmit)
    {
        this.context = context;
        this.tvTimeIn = tvTimeIn;
        this.tvBreakIn = tvBreakIn;
        this.tvBreakOut = tvBreakOut;
        this.tvTimeOut = tvTimeOut;

        this.btnSelTimeIn = btnSelTimeIn;
        this.btnSelBreakIn = btnSelBreakIn;
        this.btnSelBreakOut = btnSelBreakOut;
        this.btnSelTimeOut = btnSelTimeOut;

        this.btnSubmit = btnSubmit;

        setOnTimeSetListener(tvTimeIn);
        setOnTimeSetListener(tvBreakIn);
        setOnTimeSetListener(tvBreakOut);
        setOnTimeSetListener(tvTimeOut);

        timeInDialog = new TimePickerDialog(context, timeInTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
        breakInDialog = new TimePickerDialog(context, breakInTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
        breakOutDialog = new TimePickerDialog(context, breakOutTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
        timeOutDialog = new TimePickerDialog(context, timeOutTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);

        btnSubmit.setEnabled(false);
    }

    private TimePickerDialog.OnTimeSetListener setOnTimeSetListener(final TextView tv)
    {
        TimePickerDialog.OnTimeSetListener tsListener = new TimePickerDialog.OnTimeSetListener()
        {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                DateTime now = DateTime.now();
                DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute);
//                switch(view.getId())
//                {
//                    case R.id.setTimeIn:
//                        dateTimeToStoredPref("", dt);
//                        break;
//                    case R.id.setBreakIn:
//                        dateTimeToStoredPref("", dt);
//                        break;
//                    case R.id.setBreakOut:
//                        dateTimeToStoredPref("", dt);
//                        break;
//                    case R.id.setTimeOut:
//                        dateTimeToStoredPref("", dt);
//                        break;
//                }
                tv.setText(fmtTime.print(dt));
            }
        };

        return tsListener;
    }

    private TimePickerDialog.OnTimeSetListener timeInTSListener = new TimePickerDialog.OnTimeSetListener()
    {
        //For all Time Dialog Listeners: Get the current time, store time in new object,
        // store in shared preferences, set TextView to current time
        //Set the state of enabled and disabled textviews
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            DateTime now = DateTime.now();
            DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
            //dtTimeIn = dt;
            //dateTimeToStoredPref(spreadSheetColumns[1], dt);
            tvTimeIn.setText(fmtTime.print(dt));
            setTimeInState();
        }
    };

    TimePickerDialog.OnTimeSetListener breakInTSListener = new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            DateTime now = DateTime.now();
            DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
            //dtBreakIn = dt;
            //dateTimeToStoredPref(spreadSheetColumns[2], dt);
            tvBreakIn.setText(fmtTime.print(dt));
            setBreakInState();
        }
    };

    private TimePickerDialog.OnTimeSetListener breakOutTSListener = new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            DateTime now = DateTime.now();
            DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
            //dtBreakOut = dt;
            //dateTimeToStoredPref(spreadSheetColumns[3], dt);
            tvBreakOut.setText(fmtTime.print(dt));
            setBreakOutState();

        }
    };

    private TimePickerDialog.OnTimeSetListener timeOutTSListener  = new TimePickerDialog.OnTimeSetListener()
    {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            DateTime now = DateTime.now();
            DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
            //dtTimeOut = dt;
            //dateTimeToStoredPref(spreadSheetColumns[4], dt);
            tvTimeOut.setText(fmtTime.print(dt));
            setTimeOutState();
        }
    };

    public void showTimeInDialog()
    {
        timeInDialog.show();
    }

    public void showBreakInDialog()
    {
        breakInDialog.show();
    }

    public void showBreakOutDialog()
    {
        breakOutDialog.show();
    }

    public void showTimeOutDialog()
    {
        timeOutDialog.show();
    }

    private void dateTimeToStoredPref(String column, DateTime dt)
    {
        editor.putString(column, dt.toString());
        editor.commit();
    }

    public void setResetState()
    {
        //dtTimeIn = dtBreakIn = dtBreakOut = dtTimeOut = null;

        tvTimeIn.setEnabled(true);
        tvBreakIn.setEnabled(false);
        tvBreakOut.setEnabled(false);
        tvTimeOut.setEnabled(false);

        //btnAddSpreadSheet.setEnabled(false);

        btnSelTimeIn.setEnabled(true);
        btnSelBreakIn.setEnabled(false);
        btnSelBreakOut.setEnabled(false);
        btnSelTimeOut.setEnabled(false);

        tvTimeIn.setText("");
        tvBreakIn.setText("");
        tvBreakOut.setText("");
        tvTimeOut.setText("");
//        etSpreadSheetName.setText("");
//        etWorkSheetName.setText("");

        editor.clear().commit();

    }

    public void setTimeInState()
    {
        tvTimeIn.setEnabled(false);
        tvBreakIn.setEnabled(true);
        tvBreakOut.setEnabled(false);
        tvTimeOut.setEnabled(false);

        btnSelTimeIn.setEnabled(false);
        btnSelBreakIn.setEnabled(true);
        btnSelBreakOut.setEnabled(false);
        btnSelTimeOut.setEnabled(false);

        btnSubmit.setEnabled(false);

    }

    public void setBreakInState()
    {
        tvTimeIn.setEnabled(false);
        tvBreakIn.setEnabled(false);
        tvBreakOut.setEnabled(true);
        tvTimeOut.setEnabled(false);

        btnSelTimeIn.setEnabled(false);
        btnSelBreakIn.setEnabled(false);
        btnSelBreakOut.setEnabled(true);
        btnSelTimeOut.setEnabled(false);

        btnSubmit.setEnabled(false);

    }

    public void setBreakOutState()
    {
        tvTimeIn.setEnabled(false);
        tvBreakIn.setEnabled(false);
        tvBreakOut.setEnabled(false);
        tvTimeOut.setEnabled(true);

        btnSelTimeIn.setEnabled(false);
        btnSelBreakIn.setEnabled(false);
        btnSelBreakOut.setEnabled(false);
        btnSelTimeOut.setEnabled(true);

        btnSubmit.setEnabled(false);

    }

    public void setTimeOutState()
    {
        tvTimeIn.setEnabled(false);
        tvBreakIn.setEnabled(false);
        tvBreakOut.setEnabled(false);
        tvTimeOut.setEnabled(false);

        btnSelTimeOut.setEnabled(false);
        btnSelBreakIn.setEnabled(false);
        btnSelBreakOut.setEnabled(false);
        btnSelTimeOut.setEnabled(false);

        btnSubmit.setEnabled(true);

    }
}
