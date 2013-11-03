package org.minyanmate.minyanmate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MinyanMateDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "minyanmate.db";
	private static final int DATABASE_VERSION = 1;
	
	public MinyanMateDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys=ON;");
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		

		
		MinyanSchedulesTable.onCreate(database);
		MinyanDaysTable.onCreate(database);
		MinyanContactsTable.onCreate(database);
		MinyanEventsTable.onCreate(database);
		MinyanGoersTable.onCreate(database);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		MinyanDaysTable.onUpgrade(database, oldVersion, newVersion);
		MinyanSchedulesTable.onUpgrade(database, oldVersion, newVersion);
		MinyanContactsTable.onUpgrade(database, oldVersion, newVersion);
		MinyanEventsTable.onUpgrade(database, oldVersion, newVersion);
		MinyanGoersTable.onUpgrade(database, oldVersion, newVersion);
	}
}
