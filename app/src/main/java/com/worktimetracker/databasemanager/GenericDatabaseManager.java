package com.worktimetracker.databasemanager;

import java.lang.reflect.Type;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class GenericDatabaseManager extends DatabaseManager
{

	/*private static String dbName = "worktimetracker.db";
	
	private static int dbVersion = 1;
	
	private Dao<?, Integer> simpleDao = null;
	private RuntimeExceptionDao<?, Integer> simpleRuntimeDao = null;*/

	public GenericDatabaseManager(Context context)
	{
		super(context);		
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource cs)
	{	
		try 
		{
			TableUtils.createTable(cs, Workday.class);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) 
	{
		try 
		{
			TableUtils.dropTable(cs, Workday.class, true);
			onCreate(db, cs);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}

/*	@Override
	public Dao<?, Integer> getDao() throws SQLException 
	{
		if (simpleDao == null) {
			simpleDao = getDao(Workday.class);
		}
		return simpleDao;
	}

	@Override
	public RuntimeExceptionDao<?, Integer> getSimpleDataDao() 
	{
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(Workday.class);
		}
		return simpleRuntimeDao;
	}
*/

}
	



