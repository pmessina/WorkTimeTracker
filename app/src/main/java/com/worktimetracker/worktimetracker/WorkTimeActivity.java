package com.worktimetracker.worktimetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.worktimetracker.R;
import com.worktimetracker.databasemanager.DatabaseManager;
import com.worktimetracker.spreadsheetmanager.ExcelSpreadSheetManager;
import com.worktimetracker.spreadsheetmanager.TPTVWidgetManager;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class WorkTimeActivity extends OrmLiteBaseActivity<DatabaseManager> implements OnClickListener
{
	private SharedPreferences pref;

    private SharedPreferences.Editor editor;

	private TextView tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut;

	private ImageButton btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut;

	private Button btnAddSpreadSheet;
	
	private EditText etSpreadSheetName, etWorkSheetName;

	private ExpandableListView expListView;

    private DateTimeFormatter fmtDate, fmtTime;

    private DateTime dtToday, dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut;

    private boolean isSpreadSheetAddSuccessful = false;

    private String[] workbookColumnHeaders = new String[]{ "Date", "Time In", "Break In", "Break Out", "Time Out", "Hours"};

    private String directoryName;

    private TPTVWidgetManager listener;

    @SuppressLint("NewApi") 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabcontainer);

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
        expListView = (ExpandableListView)findViewById(R.id.expandableListView1);
        fmtDate = DateTimeFormat.shortDate();
        fmtTime = DateTimeFormat.shortTime();

        listener = new TPTVWidgetManager(this, tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut, btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut, btnAddSpreadSheet);

        directoryName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WorkTimeSpreadsheets/";

        ExcelSpreadSheetManager spreadSheetManager = new ExcelSpreadSheetManager(directoryName);

        //In the future, allow user to change directory name in settings
        ArrayList<String> retrievedFiles = spreadSheetManager.getFilesFromDirectory(directoryName);

        HashMap<String, ArrayList<String>> retrievedSheets = spreadSheetManager.fetchSheetsFromDirectory(retrievedFiles);

        final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, retrievedFiles, retrievedSheets);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                String spreadsheetName = listAdapter.getGroup(groupPosition).toString();
                String worksheetName = (String)listAdapter.getChild(groupPosition, childPosition);
                //String worksheetName = sheet.getName();

                etSpreadSheetName.setText(spreadsheetName);
                etSpreadSheetName.setSelection(etSpreadSheetName.length());

                etWorkSheetName.setText(worksheetName);
                etWorkSheetName.setSelection(etWorkSheetName.length());

                return true;
            }
        });

        //When child in expandable listview is expanded, collapse the previous child expanded
    	expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
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

        //Set up Tabs for Clock In Screen and Settings
        TabHost host = (TabHost)this.findViewById(R.id.tabhost);
        host.setup();

        TabSpec workTimeTab = host.newTabSpec("Time Tracker");
        workTimeTab.setIndicator("Time Tracker");
        workTimeTab.setContent(R.id.worktime_tab);

        TabSpec settingsTab = host.newTabSpec("Settings");
        settingsTab.setIndicator("Settings");
        settingsTab.setContent(R.id.settings_tab);
        
        host.addTab(workTimeTab);
        host.addTab(settingsTab);

        btnAddSpreadSheet.setEnabled(false);

    }

    public boolean addSpreadSheetHeader(ExcelSpreadSheetManager esm, WritableWorkbook ww, WritableSheet ws)
    {
        try
        {
            //Populate Header on first row
            for(int i = 0; i < workbookColumnHeaders.length; i++)
            {
                esm.addLabelWithCells(ws, i, 0, workbookColumnHeaders[i]);
            }

            isSpreadSheetAddSuccessful = true;
        }
        catch (WriteException e)//| jxl.read.biff.BiffException e)
        {   //Creation or writing of workbook fails
            isSpreadSheetAddSuccessful = false;
            e.printStackTrace();
        }

        return isSpreadSheetAddSuccessful;
    }

    public boolean addSpreadSheetData(ExcelSpreadSheetManager esm, WritableWorkbook ww, WritableSheet ws, DateTime today, DateTime dtTimeIn, DateTime dtTimeOut, DateTime dtBreakIn, DateTime dtBreakOut)
    {
        try
        {
            int rows = ws.getRows();

            String dateTimeFormat = "hh:mm:ss";
            String dateFormat = "mm/dd/yyyy";

            esm.addDateTimeWithCells(ws, 0, rows, today.toDate(), dateFormat);
            esm.addDateTimeWithCells(ws, 1, rows, dtTimeIn.toDate(), dateTimeFormat);
            esm.addDateTimeWithCells(ws, 2, rows, dtBreakIn.toDate(), dateTimeFormat);
            esm.addDateTimeWithCells(ws, 3, rows, dtBreakOut.toDate(), dateTimeFormat);
            esm.addDateTimeWithCells(ws, 4, rows, dtTimeOut.toDate(), dateTimeFormat);

            double totalTime = this.calculateWorkHours(dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut);

            esm.addNumberWithCells(ws, 5, rows, totalTime);

            isSpreadSheetAddSuccessful = true;
        }
        catch (WriteException e)
        {   //Creation or writing of workbook fails
            isSpreadSheetAddSuccessful = false;
            e.printStackTrace();
        }

    	return isSpreadSheetAddSuccessful;
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
    			
    			//setResetState();

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
				listener.setTimeInState();
				break;
			case R.id.tvBreakIn:
				dtBreakIn = DateTime.now();
				tvBreakIn.setText(fmtTime.print(dtBreakIn));
                listener.setBreakInState();
				break;
			case R.id.tvBreakOut:
				dtBreakOut = DateTime.now();
				tvBreakOut.setText(fmtTime.print(dtBreakOut));
                listener.setBreakOutState();
				break;
			case R.id.tvTimeOut:
				btnAddSpreadSheet.setEnabled(true);
				dtTimeOut = DateTime.now();
				tvTimeOut.setText(fmtTime.print(dtTimeOut));
                listener.setTimeOutState();
				break;
			case R.id.btnAddSpreadSheet:

                String workSheetName = etWorkSheetName.getText().toString().trim();
                String spreadSheetName = etSpreadSheetName.getText().toString().trim();

                if (spreadSheetName.isEmpty() || workSheetName.isEmpty())
                {
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
                else
                {
                    ExcelSpreadSheetManager esm = new ExcelSpreadSheetManager(directoryName, spreadSheetName, workSheetName);

                    try
                    {
                        File spreadSheetFile = new File(directoryName, spreadSheetName);

                        WritableWorkbook writableWorkbook;
                        WritableSheet writableSheet;

                        if (spreadSheetFile.exists())
                        {
                            Workbook wkbk = esm.getWorkbook(spreadSheetFile);
                            //Convert existing workbook to writable workbook to prepare for writing
                            writableWorkbook = esm.createWorkbook(spreadSheetFile, wkbk);
                            writableSheet = esm.createWorkSheet(writableWorkbook, wkbk.getSheet(0));
                        }
                        else
                        {
                            spreadSheetFile = new File(directoryName, spreadSheetName + ".xls");
                            writableWorkbook = esm.createWorkbook(spreadSheetFile);
                            writableSheet = esm.createWorkSheet(writableWorkbook, workSheetName);
                            addSpreadSheetHeader(esm, writableWorkbook, writableSheet);
                        }

                        addSpreadSheetData(esm, writableWorkbook, writableSheet, dtToday, dtTimeIn, dtTimeOut, dtBreakIn, dtBreakOut);
                        esm.writeCloseWorkBook(writableWorkbook);

                    }
                    catch (IOException | WriteException | BiffException e)
                    {
                        e.printStackTrace();
                    }
                }
				break;
			case R.id.btnSelTimeIn:
				listener.showTimeInDialog();
				break;
			case R.id.btnSelBreakIn:
				listener.showBreakInDialog();
				break;
			case R.id.btnSelBreakOut:
				listener.showBreakOutDialog();
				break;
			case R.id.btnSelTimeOut:
				listener.showTimeOutDialog();
				break;
				
		}
		
	}

	public void spreadWorkSheetToStoredPref(TextView tv, String key)
	{ 
		editor.putString(key, tv.getText().toString());
		editor.commit();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();

		spreadWorkSheetToStoredPref(etSpreadSheetName, "SpreadSheetName");
		spreadWorkSheetToStoredPref(etWorkSheetName, "WorkSheetName");
	}

}


