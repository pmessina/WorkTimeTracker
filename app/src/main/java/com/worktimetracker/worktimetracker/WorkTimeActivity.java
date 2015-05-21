package com.worktimetracker.worktimetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.pras.SpreadSheet;
import com.pras.WorkSheet;
import com.pras.WorkSheetRow;
import com.worktimetracker.R;
import com.worktimetracker.databasemanager.DatabaseManager;
import com.worktimetracker.spreadsheetmanager.GetSpreadSheetsTask;
import com.worktimetracker.spreadsheetmanager.SpreadSheetManager;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;

public class WorkTimeActivity extends OrmLiteBaseActivity<DatabaseManager> implements OnClickListener
{
	SharedPreferences pref;

    Editor editor;	
	
	TimePickerDialog timeInDialog, breakInDialog, breakOutDialog, timeOutDialog; 
	
	TextView tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut;
	
	ImageButton btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut;

	Button btnAddSpreadSheet;
	
	EditText etSpreadSheetName, etWorkSheetName;
	
	DateTime dtToday, dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut;

	ExpandableListView expListView;

	DateTimeFormatter fmtDate, fmtTime; 

    TabHost host;
    
    TabSpec workTimeTab, settingsTab;
    
    SpreadSheetManager spreadSheetManager;
    
    String[] spreadSheetColumns;
        
    private boolean isSpreadSheetAddSuccessful = true;

    @SuppressLint("NewApi") 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabcontainer);
        //Initialization
        tvTimeIn = (TextView)findViewById(R.id.tvTimeIn);
        tvBreakIn  = (TextView)findViewById(R.id.tvBreakIn);
        tvBreakOut  = (TextView)findViewById(R.id.tvBreakOut);
        tvTimeOut  = (TextView)findViewById(R.id.tvTimeOut);
    	btnSelTimeIn = (ImageButton)findViewById(R.id.btnSelTimeIn);
    	btnSelBreakIn  = (ImageButton)findViewById(R.id.btnSelBreakIn);
    	btnSelBreakOut = (ImageButton)findViewById(R.id.btnSelBreakOut);
    	btnSelTimeOut = (ImageButton)findViewById(R.id.btnSelTimeOut);
    	btnAddSpreadSheet = (Button)findViewById(R.id.btnAddSpreadSheet);
    	etSpreadSheetName = (EditText)findViewById(R.id.etSpreadSheetName);
    	etWorkSheetName = (EditText)findViewById(R.id.etWorkSheetName);
        host = (TabHost)this.findViewById(R.id.tabhost);
    	expListView = (ExpandableListView)findViewById(R.id.expandableListView1);
        fmtDate = DateTimeFormat.shortDate();
        fmtTime = DateTimeFormat.shortTime();

    	expListView.setOnGroupExpandListener(new OnGroupExpandListener() 
    	{
    		int lastExpandedPosition = -1;
    	    
            @Override
            public void onGroupExpand(int groupPosition) 
            {
            	if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition)
            	{
            		expListView.collapseGroup(lastExpandedPosition);
            	}
            	lastExpandedPosition = groupPosition;
            }
        });

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		
		spreadSheetManager = new SpreadSheetManager();
		spreadSheetColumns = spreadSheetManager.getColumns();
        //For all Time Dialog Listeners: Get the current time, store time in new object,
        // store in shared preferences, set TextView to current time
        //Set the state of enabled and disabled textviews
		OnTimeSetListener timeInTSListener = new OnTimeSetListener() 
    	{
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
            {

            	DateTime now = DateTime.now();
           	 	DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
           	 	dtTimeIn = dt;
           	 	dateTimeToStoredPref(spreadSheetColumns[1], dt);
           	 	tvTimeIn.setText(fmtTime.print(dt));
           		setTimeInState();	
            }
        };
        
    	OnTimeSetListener breakInTSListener = new OnTimeSetListener() 
    	{
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
            {
            	DateTime now = DateTime.now();
           	 	DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
           	 	dtBreakIn = dt;
           	 	dateTimeToStoredPref(spreadSheetColumns[2], dt);
           	 	tvBreakIn.setText(fmtTime.print(dt));
           	 	setBreakInState();
            }
        };
  
        OnTimeSetListener breakOutTSListener = new OnTimeSetListener() 
    	{
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
            {
            	DateTime now = DateTime.now();
           	 	DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
           	 	dtBreakOut = dt;
           	 	dateTimeToStoredPref(spreadSheetColumns[3], dt);
           	 	tvBreakOut.setText(fmtTime.print(dt));           	 	
           		setBreakOutState();
			
            }
        };
       	
    	OnTimeSetListener timeOutTSListener  = new OnTimeSetListener() 
    	{
    		
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
            {
            	DateTime now = DateTime.now();
           	 	DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute, now.getSecondOfMinute());
           	 	dtTimeOut = dt;
           	 	dateTimeToStoredPref(spreadSheetColumns[4], dt);
           	 	tvTimeOut.setText(fmtTime.print(dt));           	 	
				setTimeOutState();
            }
        };

    	timeInDialog = new TimePickerDialog(this, timeInTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
        breakInDialog = new TimePickerDialog(this, breakInTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
    	breakOutDialog = new TimePickerDialog(this, breakOutTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
     	timeOutDialog = new TimePickerDialog(this, timeOutTSListener, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour(), false);
     	
     	dtToday = storedPrefToDateTime(null, spreadSheetColumns[0]);
     	
     	dtTimeIn = storedPrefToDateTime(tvTimeIn, spreadSheetColumns[1]);
     	if (dtTimeIn != null)
     	{
     		setTimeInState();
     		btnAddSpreadSheet.setEnabled(false);
     	}

        dtBreakIn = storedPrefToDateTime(tvBreakIn, spreadSheetColumns[2]);
        if (dtBreakIn != null)
        {
        	setBreakInState();
        	btnAddSpreadSheet.setEnabled(false);
        }

        dtBreakOut = storedPrefToDateTime(tvBreakOut, spreadSheetColumns[3]);
        if (dtBreakOut != null)
        {
        	setBreakOutState();
        	btnAddSpreadSheet.setEnabled(false);
        }
        
        dtTimeOut = storedPrefToDateTime(tvTimeOut, spreadSheetColumns[4]);
        if (dtTimeOut != null)
        {
        	setTimeOutState();
        	btnAddSpreadSheet.setEnabled(true);
        }

        host.setup();
        
        workTimeTab = host.newTabSpec("Time Tracker");
        workTimeTab.setIndicator("Time Tracker");
        workTimeTab.setContent(R.id.worktime_tab);
        
        settingsTab = host.newTabSpec("Settings");
        settingsTab.setIndicator("Settings");
        settingsTab.setContent(R.id.settings_tab);
        
        host.addTab(workTimeTab);
        host.addTab(settingsTab);

		this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetSpreadSheetsTask task = new GetSpreadSheetsTask(WorkTimeActivity.this, expListView, etSpreadSheetName, etWorkSheetName);
                task.execute();
            }
        });
		
		storedPrefToSpreadWorkSheet(etSpreadSheetName, "SpreadSheetName");
		storedPrefToSpreadWorkSheet(etWorkSheetName, "WorkSheetName");
		
    }
    
    public OnTimeSetListener setOnTimeSetListener(final TextView tv)
    {
    	OnTimeSetListener tsListener = new OnTimeSetListener() 
    	{		
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
            {
           	 	DateTime now = DateTime.now();
           	 	DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hourOfDay, minute);
           	 	switch(view.getId())
           	 	{
	           	 	case R.id.setTimeIn:
	           	 		dateTimeToStoredPref(spreadSheetColumns[1], dt);
	           	 		break;
	           	 	case R.id.setBreakIn:
	           	 		dateTimeToStoredPref(spreadSheetColumns[2], dt);
	           	 		break;
	           	 	case R.id.setBreakOut:
	           	 		dateTimeToStoredPref(spreadSheetColumns[3], dt);
	           	 		break;
	           	 	case R.id.setTimeOut:
	           	 		dateTimeToStoredPref(spreadSheetColumns[4], dt);
	           	 		break;
           	 	}
                tv.setText(fmtTime.print(dt));
            }
        };
        
        return tsListener;
    }

    public boolean addSpreadSheetData(final DateTime dtTimeIn, final DateTime dtTimeOut, final DateTime dtBreakIn, final DateTime dtBreakOut) throws InterruptedException
    {
		Thread th = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Looper.prepare();
				try
				{
					String spreadSheetName = etSpreadSheetName.getText().toString();
					String workSheetName = etWorkSheetName.getText().toString();

					if (!spreadSheetName.isEmpty() && !workSheetName.isEmpty())
					{
						isSpreadSheetAddSuccessful = true;
						SpreadSheet sh = spreadSheetManager.createNewSpreadsheet(spreadSheetName);
						WorkSheet ws = spreadSheetManager.createNewWorksheet(sh, workSheetName);

						addTimeRecord(sh, ws, dtTimeIn, dtTimeOut, dtBreakIn, dtBreakOut);
	
						Toast.makeText(WorkTimeActivity.this, "Work Times Added Successfully", Toast.LENGTH_LONG).show();
					}
					else
					{
						isSpreadSheetAddSuccessful = false;
						AlertDialog dialog = new AlertDialog.Builder(WorkTimeActivity.this).create();
						dialog.setTitle("Missing Information");
						dialog.setMessage("You must enter a spreadsheet name and worksheet name!");
						dialog.setIcon(R.drawable.clock);
						dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() 
						{	
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.dismiss();
							}
						});

						dialog.show();
					}
				}
				catch(IllegalAccessError ex)
				{
					isSpreadSheetAddSuccessful = false;
					AlertDialog dialog = new AlertDialog.Builder(WorkTimeActivity.this).create();
					dialog.setTitle("Spreadsheet Not Prepared");
					dialog.setMessage(ex.getMessage());
					dialog.setIcon(R.drawable.clock);
					dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() 
					{	
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.dismiss();
						}
					});
					dialog.show();
				}
				
				Looper.loop();
				Looper.getMainLooper().quit();

			}

		});
    	th.start();
    	Thread.sleep(5000);
    	
    	return isSpreadSheetAddSuccessful;
    }

    public void addTimeRecord(SpreadSheet spreadsheet, WorkSheet worksheet, DateTime dtTimeIn, DateTime dtBreakIn, DateTime dtBreakOut, DateTime dtTimeOut)
    {
		HashMap<String, String> rec = new HashMap<String, String>(); 

		rec.put(spreadSheetColumns[0], fmtDate.print(dtToday));
		rec.put(spreadSheetColumns[1], fmtTime.print(dtTimeIn));
		rec.put(spreadSheetColumns[2], fmtTime.print(dtBreakIn));
		rec.put(spreadSheetColumns[3], fmtTime.print(dtBreakOut));
		rec.put(spreadSheetColumns[4], fmtTime.print(dtTimeOut));
		rec.put(spreadSheetColumns[5], Double.toString(calculateWorkHours(dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut)));

		WorkSheet ws = spreadsheet.getWorkSheet(worksheet.getTitle(), true).get(0);
		WorkSheetRow row = ws.addListRow(rec);


		//worksheet.addRecord(spreadsheet.getKey(), rec);
    }	
    
    public double calculateWorkHours(DateTime dtTimeIn, DateTime dtBreakIn, DateTime dtBreakOut, DateTime dtTimeOut)
    {
    	Period perWorkTime = new Period(dtTimeIn, dtTimeOut, PeriodType.time());
    	Period perBreakTime = new Period(dtBreakIn, dtBreakOut, PeriodType.time());

    	int workHours = perWorkTime.getHours();
    	int breakHours = perBreakTime.getHours();
    	double workMinutes = (double)perWorkTime.getMinutes() / 60;
    	double breakMinutes = (double)perBreakTime.getMinutes() / 60;
    	    	
    	return (workHours + workMinutes) - (breakHours + breakMinutes);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.work_time, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) 
    {
    	switch(item.getItemId())
    	{
    		case R.id.menu_item_reset:
    			
    			setResetState();

				editor.clear();
				editor.commit();
				
				dtTimeIn = null;
				dtBreakIn = null;
				dtBreakOut = null;
				dtTimeOut = null;
			break;
				
    		case R.id.menu_view_data:
            break;
				
    		case R.id.menu_delete_data:

    		break;
    	}	

    	return super.onMenuItemSelected(featureId, item);
    }

    //Execute on textview and button click
	@Override
	public void onClick(View v) 
	{
		dtToday = DateTime.now().toLocalDate().toDateTimeAtStartOfDay();
		
		switch(v.getId())
		{
			case R.id.tvTimeIn:
				dtTimeIn = DateTime.now();
				tvTimeIn.setText(fmtTime.print(dtTimeIn));
				setTimeInState();
				break;
			case R.id.tvBreakIn:
				dtBreakIn = DateTime.now();
				tvBreakIn.setText(fmtTime.print(dtBreakIn));
				setBreakInState();
				break;
			case R.id.tvBreakOut:
				dtBreakOut = DateTime.now();
				tvBreakOut.setText(fmtTime.print(dtBreakOut));
				setBreakOutState();
				break;
			case R.id.tvTimeOut:
				btnAddSpreadSheet.setEnabled(true);
				dtTimeOut = DateTime.now();
				tvTimeOut.setText(fmtTime.print(dtTimeOut));
				setTimeOutState();
				break;
			case R.id.btnAddSpreadSheet:
				try 
				{
					if (addSpreadSheetData(dtTimeIn, dtTimeOut, dtBreakIn, dtBreakOut))
					{
						setResetState();
					}
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				
				break;
			case R.id.btnSelTimeIn:
				timeInDialog.show();
				break;
			case R.id.btnSelBreakIn:
				breakInDialog.show();
				break;
			case R.id.btnSelBreakOut:
				breakOutDialog.show();
				break;
			case R.id.btnSelTimeOut:
				timeOutDialog.show();
				break;
				
		}
		
	}

	public void dateTimeToStoredPref(String column, DateTime dt)
	{
		editor.putString(column, dt.toString());
		editor.commit();
	}
	
	public void spreadWorkSheetToStoredPref(TextView tv, String key)
	{ 
		editor.putString(key, tv.getText().toString());
		editor.commit();
	}
	
	public String storedPrefToSpreadWorkSheet(TextView tv, String key)
	{
		String str = pref.getString(key, "");
		
		tv.setText(str);
		
		return str;
	}
	
	public DateTime storedPrefToDateTime(TextView tv, String column)
	{	
		String str = pref.getString(column, "");

		DateTime dt = null;
		
		if (!str.equals(""))
		{
			dt = DateTime.parse(str);
		 
			if (tv != null)
			{
				tv.setText(fmtTime.print(dt));
			}
		}
		
		return dt;
	}
	
	public void setResetState()
	{
		dtTimeIn = dtBreakIn = dtBreakOut = dtTimeOut = null;
		
		tvTimeIn.setEnabled(true);
		tvBreakIn.setEnabled(false);
		tvBreakOut.setEnabled(false);
		tvTimeOut.setEnabled(false);
		
		btnAddSpreadSheet.setEnabled(false);
		
		btnSelTimeIn.setEnabled(true);
		btnSelBreakIn.setEnabled(false);
		btnSelBreakOut.setEnabled(false);
		btnSelTimeOut.setEnabled(false);
	
		tvTimeIn.setText("");
		tvBreakIn.setText("");
		tvBreakOut.setText("");
		tvTimeOut.setText("");
		etSpreadSheetName.setText("");
		etWorkSheetName.setText("");
		
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
	}
    
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		if (dtToday != null)
			dateTimeToStoredPref(spreadSheetColumns[0], dtToday);
		
		if (dtTimeIn != null)
			dateTimeToStoredPref(spreadSheetColumns[1], dtTimeIn);
		
		if (dtBreakIn != null)
			dateTimeToStoredPref(spreadSheetColumns[2], dtBreakIn);
		
		if (dtBreakOut != null)
			dateTimeToStoredPref(spreadSheetColumns[3], dtBreakOut);
		
		if (dtTimeOut != null)
			dateTimeToStoredPref(spreadSheetColumns[4], dtTimeOut);
		
		spreadWorkSheetToStoredPref(etSpreadSheetName, "SpreadSheetName");
		spreadWorkSheetToStoredPref(etWorkSheetName, "WorkSheetName");
	}

}


