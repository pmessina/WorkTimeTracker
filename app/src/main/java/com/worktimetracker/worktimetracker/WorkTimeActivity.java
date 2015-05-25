package com.worktimetracker.worktimetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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
import com.pras.SpreadSheet;
import com.pras.WorkSheet;
import com.worktimetracker.R;
import com.worktimetracker.databasemanager.DatabaseManager;
import com.worktimetracker.spreadsheetmanager.ExcelSpreadSheetManager;
import com.worktimetracker.spreadsheetmanager.TimeSetListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class WorkTimeActivity extends OrmLiteBaseActivity<DatabaseManager> implements OnClickListener
{
	SharedPreferences pref;

    Editor editor;	

	private TextView tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut;

	private ImageButton btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut;

	private Button btnAddSpreadSheet;
	
	EditText etSpreadSheetName, etWorkSheetName;

	ExpandableListView expListView;

    DateTimeFormatter fmtDate, fmtTime;

    DateTime dtToday, dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut;

    private boolean isSpreadSheetAddSuccessful = false;

    String[] columns = new String[]{ "Date", "Time In", "Break In", "Break Out", "Time Out", "Hours"};

    String directoryName;

    @SuppressLint("NewApi") 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabcontainer);

        //Initialization of UI Widgets
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

        ExcelSpreadSheetManager spreadSheetManager = new ExcelSpreadSheetManager();

        ArrayList<String> listDataHeader = new ArrayList<String>();
        HashMap<String, List<WritableSheet>> listDataChild = new HashMap<>();

        directoryName = spreadSheetManager.getWorkbookDirectory() + "/WorkTimeSpreadsheets/";

        //In the future, change directory name in preference activity
        File directory = new File(directoryName);
        if (!directory.exists())
        {
            directory.mkdir();
        }

        ArrayList<File> workBooks = new ArrayList<File>();
        File[] filesInDir = directory.listFiles();
        if (filesInDir.length != 0)
        {
            for (File file : filesInDir)
            {
                workBooks.add(file);
                listDataHeader.add(file.getName());

                ArrayList<WritableSheet> writableSheets = new ArrayList<>();
                try
                {
                    WritableWorkbook wkbk = spreadSheetManager.createOrGetWorkBook(directoryName, file.getName());
                    if (wkbk.getSheets().length == 0)
                    {
                        //Set sheet name
                        spreadSheetManager.createWorkSheet(wkbk, "TimeSheet");
                    }

                    for (WritableSheet sheet : wkbk.getSheets())
                    {
                        writableSheets.add(sheet);
                    }
                    listDataChild.put(file.getName(), writableSheets);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        else
        {
//            try
//            {
//                WritableWorkbook wkbk = spreadSheetManager.createOrGetWorkBook(directoryName, "TimeSpreadSheet");
//                spreadSheetManager.createWorkSheet(wkbk, "TimeWorkSheet");
//                spreadSheetManager.writeCloseWorkBook(wkbk);
//            }
//            catch (IOException | BiffException | WriteException e)
//            {
//                e.printStackTrace();
//            }

        }

        final ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                String spreadsheetName = listAdapter.getGroup(groupPosition).toString();
                WritableSheet sheet = (WritableSheet)listAdapter.getChild(groupPosition, childPosition);
                String worksheetName = sheet.getName();

                etSpreadSheetName.setText(spreadsheetName);
                etSpreadSheetName.setSelection(etSpreadSheetName.length());

                etWorkSheetName.setText(worksheetName);
                etWorkSheetName.setSelection(etWorkSheetName.length());

                return false;
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

//     	dtToday = storedPrefToDateTime(null, spreadSheetColumns[0]);
//
//     	dtTimeIn = storedPrefToDateTime(tvTimeIn, spreadSheetColumns[1]);
//     	if (dtTimeIn != null)
//     	{
//     		setTimeInState();
//     		btnAddSpreadSheet.setEnabled(false);
//     	}
//
//        dtBreakIn = storedPrefToDateTime(tvBreakIn, spreadSheetColumns[2]);
//        if (dtBreakIn != null)
//        {
//        	setBreakInState();
//        	btnAddSpreadSheet.setEnabled(false);
//        }
//
//        dtBreakOut = storedPrefToDateTime(tvBreakOut, spreadSheetColumns[3]);
//        if (dtBreakOut != null)
//        {
//        	setBreakOutState();
//        	btnAddSpreadSheet.setEnabled(false);
//        }
//
//        dtTimeOut = storedPrefToDateTime(tvTimeOut, spreadSheetColumns[4]);
//        if (dtTimeOut != null)
//        {
//        	setTimeOutState();
//        	btnAddSpreadSheet.setEnabled(true);
//        }


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

		storedPrefToSpreadWorkSheet(etSpreadSheetName, "SpreadSheetName");
		storedPrefToSpreadWorkSheet(etWorkSheetName, "WorkSheetName");
    }

    public boolean addSpreadSheetHeader(ExcelSpreadSheetManager esm)
    {
        try
        {
            WritableWorkbook ww = esm.createOrGetWorkBook(esm.getDirectoryName(), esm.getSpreadSheetName());
            WritableSheet ws = esm.createWorkSheet(ww, esm.getWorkSheetName());

            //Populate Header on first row
            for(int i = 0; i < columns.length; i++)
            {
                esm.addLabelWithCells(ws, i, 0, columns[i]);
            }

            esm.writeCloseWorkBook(ww);

            isSpreadSheetAddSuccessful = true;
        }
        catch (IOException | jxl.write.WriteException | jxl.read.biff.BiffException e)
        {   //Creation or writing of workbook fails
            isSpreadSheetAddSuccessful = false;
            e.printStackTrace();
        }

        return isSpreadSheetAddSuccessful;
    }

    public boolean addSpreadSheetData(ExcelSpreadSheetManager esm, DateTime dtTimeIn, DateTime dtTimeOut, DateTime dtBreakIn, DateTime dtBreakOut)
    {
        try
        {
            WritableWorkbook ww = esm.createOrGetWorkBook(esm.getDirectoryName(), esm.getSpreadSheetName());
            WritableSheet ws = esm.createWorkSheet(ww, esm.getWorkSheetName());

            //Populate Header on first row
            for(int i = 0; i < columns.length; i++)
            {
                esm.addLabelWithCells(ws, i, 0, columns[i]);
            }

            esm.addDateWithCells(ws, 0, 0, dtTimeIn.toDate());
            esm.addDateWithCells(ws, 1, 0, dtBreakIn.toDate());
            esm.addDateWithCells(ws, 2, 0, dtBreakOut.toDate());
            esm.addDateWithCells(ws, 3, 0, dtTimeOut.toDate());
            esm.writeCloseWorkBook(ww);

            isSpreadSheetAddSuccessful = true;
        }
        catch (IOException | jxl.write.WriteException | jxl.read.biff.BiffException e)
        {   //Creation or writing of workbook fails
            isSpreadSheetAddSuccessful = false;
            e.printStackTrace();
        }

    	return isSpreadSheetAddSuccessful;
    }

    public void addTimeRecord(SpreadSheet spreadsheet, WorkSheet worksheet, DateTime dtTimeIn, DateTime dtBreakIn, DateTime dtBreakOut, DateTime dtTimeOut)
    {
//		HashMap<String, String> rec = new HashMap<String, String>();
//
//		rec.put(spreadSheetColumns[0], fmtDate.print(dtToday));
//		rec.put(spreadSheetColumns[1], fmtTime.print(dtTimeIn));
//		rec.put(spreadSheetColumns[2], fmtTime.print(dtBreakIn));
//		rec.put(spreadSheetColumns[3], fmtTime.print(dtBreakOut));
//		rec.put(spreadSheetColumns[4], fmtTime.print(dtTimeOut));
//		rec.put(spreadSheetColumns[5], Double.toString(calculateWorkHours(dtTimeIn, dtBreakIn, dtBreakOut, dtTimeOut)));
//
//		WorkSheet ws = spreadsheet.getWorkSheet(worksheet.getTitle(), true).get(0);
//
//		WorkSheetRow row = ws.addListRow(rec);
//        ws.getRecords().get(0).setData(rec);
//        ArrayList<WorkSheetCell> workSheetCellArrayList = new ArrayList<>();
//        WorkSheetCell workSheetCell = new WorkSheetCell();
//        workSheetCell.setName("Column 1");
//        workSheetCell.setValue("Column 1");
//        workSheetCell.setRow(1);
//        workSheetCell.setCol(1);
//        workSheetCellArrayList.add(workSheetCell);
//        row.setCells(workSheetCellArrayList);

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
        TimeSetListener listener = new TimeSetListener(this, tvTimeIn, tvBreakIn, tvBreakOut, tvTimeOut, btnSelTimeIn, btnSelBreakIn, btnSelBreakOut, btnSelTimeOut);

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

                if (etSpreadSheetName.getText().toString().isEmpty() || etWorkSheetName.getText().toString().isEmpty())
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
                    String workSheetName = etWorkSheetName.getText().toString();
                    String spreadSheetName = etSpreadSheetName.getText().toString();

                    ExcelSpreadSheetManager esm = new ExcelSpreadSheetManager(directoryName, spreadSheetName, workSheetName);

                    addSpreadSheetData(esm, dtTimeIn, dtTimeOut, dtBreakIn, dtBreakOut);
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
	

    
	@Override
	protected void onPause() 
	{
		super.onPause();
		
//		if (dtToday != null)
//			dateTimeToStoredPref(spreadSheetColumns[0], dtToday);
//
//		if (dtTimeIn != null)
//			dateTimeToStoredPref(spreadSheetColumns[1], dtTimeIn);
//
//		if (dtBreakIn != null)
//			dateTimeToStoredPref(spreadSheetColumns[2], dtBreakIn);
//
//		if (dtBreakOut != null)
//			dateTimeToStoredPref(spreadSheetColumns[3], dtBreakOut);
//
//		if (dtTimeOut != null)
//			dateTimeToStoredPref(spreadSheetColumns[4], dtTimeOut);
		
		spreadWorkSheetToStoredPref(etSpreadSheetName, "SpreadSheetName");
		spreadWorkSheetToStoredPref(etWorkSheetName, "WorkSheetName");
	}

}


