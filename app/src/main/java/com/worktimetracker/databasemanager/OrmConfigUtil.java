package com.worktimetracker.databasemanager;
import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;


public class OrmConfigUtil extends OrmLiteConfigUtil
{
	public static void main(String[] args) throws SQLException, IOException 
	{
		writeConfigFile("../orm_config.txt");
	}
}
