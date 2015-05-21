package com.worktimetracker.spreadsheetmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;

public class GetWorkSheetsTask  extends AsyncTask<Void, Void, ArrayList<WorkSheet>>  
{
	SpreadSheetFactory factory;
	String spreadSheetName;

	HashMap<String, List<String>> listDataChild;
		
	public GetWorkSheetsTask(String spreadSheetName, HashMap<String, List<String>> listDataChild)
	{
		this.spreadSheetName = spreadSheetName;
		this.factory = SpreadSheetFactory.getInstance("pmm0501@gmail.com", "pmpirateNC");
		this.listDataChild = listDataChild;
		
	}

	@Override
	protected ArrayList<WorkSheet> doInBackground(Void... urls) 
	{
		ArrayList<SpreadSheet> spreadsheets = new ArrayList<SpreadSheet>();
		ArrayList<WorkSheet> worksheets = new ArrayList<WorkSheet>();
		
		spreadsheets = factory.getSpreadSheet(spreadSheetName, true);
			
		worksheets = spreadsheets.get(0).getAllWorkSheets();
		
		return worksheets;
		
	}
	
	@Override
	protected void onPostExecute(ArrayList<WorkSheet> worksheets) 
	{
		List<String> items = new ArrayList<String>();
			
		for(int i = 0; i < worksheets.size(); i++)
    	{
			items.add(worksheets.get(i).getTitle());
    	}
		
		listDataChild.put(spreadSheetName, items);
									
	}	

}
