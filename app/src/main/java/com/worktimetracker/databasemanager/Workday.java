package com.worktimetracker.databasemanager;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Workday")
public class Workday
{
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(canBeNull = true)
	private String dateToday;
	
	@DatabaseField(canBeNull = true)
	private String timeIn;
	
	@DatabaseField(canBeNull = true)
	private String breakIn;
	
	@DatabaseField(canBeNull = true)
	private String breakOut;
	
	@DatabaseField(canBeNull = true)
	private String timeOut;
	
	public Workday()
	{
		
	}

	public String getDateToday() {
		return dateToday;
	}

	public void setDateToday(String dateToday) {
		this.dateToday = dateToday;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public String getBreakIn() {
		return breakIn;
	}

	public void setBreakIn(String breakIn) {
		this.breakIn = breakIn;
	}

	public String getBreakOut() {
		return breakOut;
	}

	public void setBreakOut(String breakOut) {
		this.breakOut = breakOut;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public String toString() 
	{
		return dateToday + " "+timeIn + " "+ breakIn + " "+ breakOut + " "+ timeOut; 
	}

	
	/*public void QueryConnection()
	{
		String csvFile = "C:/Users/Desktop/p-words.csv";
		BufferedReader br = null;
		String line = "";
		
		try 
		{
			RuntimeExceptionDao<PasswordsTable, Integer> dao = this.getHelper().getRuntimeExceptionDao(PasswordsTable.class);
	
			PasswordsTable table = null;
	 
			br = new BufferedReader(new FileReader(csvFile));
			
			while ((line = br.readLine()) != null) 
			{
				table = new PasswordsTable();
				
				// use comma as separator
				String[] dbfield = line.split(",");
				
				table.setWebSiteName(dbfield[0]);
				table.setUserName(dbfield[1]);
				table.setPassword(dbfield[2]);
				table.setUrl(dbfield[3]);
				dao.create(table);
			}
				 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
	  }*/
	
}
