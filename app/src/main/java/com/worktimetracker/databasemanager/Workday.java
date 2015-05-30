package com.worktimetracker.databasemanager;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Workday class represents the Workday table in the worktimetracker database
 */
@DatabaseTable(tableName = "Workday")
public class Workday
{
    public Workday() {}

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

	@Override
	public String toString() 
	{
		return dateToday + " "+timeIn + " "+ breakIn + " "+ breakOut + " "+ timeOut; 
	}
}
