package com.worktimetracker.spreadsheetmanager;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;

import java.util.ArrayList;

public class SpreadSheetManager 
{
    String[] columns = new String[]{ "Date", "Time In", "Break In", "Break Out", "Time Out", "Hours"};
    
	SpreadSheetFactory factory;

	public SpreadSheetManager()
	{
		factory = SpreadSheetFactory.getInstance("pmm0501@gmail.com", "pmpirateNC");
	}
	
	public String[] getColumns()
	{
		return columns;
	}
    
    public SpreadSheet createNewSpreadsheet(String spreadSheetName)
    {
    	ArrayList<SpreadSheet> spreadsheets = factory.getSpreadSheet(spreadSheetName, true);
    	
    	if (spreadsheets == null)
    	{
    		factory.createSpreadSheet(spreadSheetName);
    		spreadsheets = factory.getSpreadSheet(spreadSheetName, true);
    	}

    	return spreadsheets.get(0);
    }
    
    public WorkSheet createNewWorksheet(SpreadSheet sheet, String worksheetName)
    {
    	ArrayList<WorkSheet> worksheets = sheet.getWorkSheet(worksheetName, true);

		//worksheets.get(0).getEntry()
		//ArrayList<WorkSheet> worksheets = sheet.getAllWorkSheets(false);


    	if (worksheets == null)
		{
			sheet.addListWorkSheet(worksheetName, 35, columns);


    		//sheet.addWorkSheet(worksheetName, "Description", 35, columns);
    		worksheets = sheet.getWorkSheet(worksheetName, true);

    	}	
    	
    	return worksheets.get(0);
    }

}
