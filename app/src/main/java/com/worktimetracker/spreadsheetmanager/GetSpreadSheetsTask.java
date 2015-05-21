package com.worktimetracker.spreadsheetmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.worktimetracker.worktimetracker.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetSpreadSheetsTask  extends AsyncTask<Void, Void, ArrayList<SpreadSheet>>  
{
	SpreadSheetFactory factory;
	Context context;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	EditText etSpreadSheetName, etWorkSheetName;
	ProgressDialog dialog;
	
	public GetSpreadSheetsTask(Context context, ExpandableListView expListView, EditText etSpreadSheetName, EditText etWorkSheetName)
	{
		this.context = context;
		this.expListView = expListView;
		this.etSpreadSheetName = etSpreadSheetName;
		this.etWorkSheetName = etWorkSheetName;
		factory = SpreadSheetFactory.getInstance("pmm0501@gmail.com", "pmpirateNC");
	}
	
	@Override
	protected void onPreExecute() 
	{
		dialog = ProgressDialog.show(context, "Time Tracker", "Getting Spreadsheets");
	}

	@Override
	protected ArrayList<SpreadSheet> doInBackground(Void... urls) 
	{
		return factory.getAllSpreadSheets();
	}
	
	@Override
	protected void onPostExecute(ArrayList<SpreadSheet> spreadsheets) 
	{
		if (dialog.isShowing())
			dialog.dismiss();
		
		try 
		{
			ArrayList<String> listDataHeader = new ArrayList<String>();
			HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

            if (spreadsheets != null)
            {
                for (int i = 0; i < spreadsheets.size(); i++)
				{
                    listDataHeader.add(spreadsheets.get(i).getTitle());
                }
            }
			for(int j = 0; j < listDataHeader.size(); j++)
			{
				GetWorkSheetsTask wstask = new GetWorkSheetsTask(listDataHeader.get(j), listDataChild);
				wstask.execute();
			}
			
			listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
			expListView.setAdapter(listAdapter);

			expListView.setOnChildClickListener(new OnChildClickListener() 
			{
				
				@Override
				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) 
				{
					String spreadsheetName = listAdapter.getGroup(groupPosition).toString();
					String worksheetName = listAdapter.getChild(groupPosition, childPosition).toString();
					
					etSpreadSheetName.setText(spreadsheetName);
					etSpreadSheetName.setSelection(etSpreadSheetName.length());
					
					etWorkSheetName.setText(worksheetName);
					etWorkSheetName.setSelection(etWorkSheetName.length());

					return false;
				}
			});
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} 
	}

}

