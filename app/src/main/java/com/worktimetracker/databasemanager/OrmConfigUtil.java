package com.worktimetracker.databasemanager;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 *Utility class for configuring database schema for Ormlite
 */
public class OrmConfigUtil extends OrmLiteConfigUtil
{
	public static void main(String[] args) throws SQLException, IOException 
	{
		writeConfigFile("../orm_config.txt");
	}
}
