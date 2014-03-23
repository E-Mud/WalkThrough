package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSource implements UserDataSource{
	private static final int VERSION = 0;
	private SQLiteDatabase db;
	private SQLiteHelper helper;
	
	//FIXME Mejorar esto si o si
	private static final String[] PROFILE_COLS = new String[]{
		"_id", "wsId", "nickname", "name", "lastname", "borndate", "gender", "height", "weight"
	};
	private static final String PROFILE_NAME = "profile";

	public DataSource(Context context, String userName){
		String database_name = buildDatabaseName(userName);
		helper = new SQLiteHelper(context, database_name, VERSION);
		openDatabase();
	}
	
	private void openDatabase() {
		if(db == null)
			db = helper.getWritableDatabase();
	}

	public void closeDatabase(){
		if(db != null){
			db.close();
			db = null;
		}
	}
	
	public static String buildDatabaseName(String userName){
		return new String("db_" + userName.replaceAll(" ", "_"));
	}

	private static class SQLiteHelper extends SQLiteOpenHelper{
		private static final String DB_PROFILE_CREATE="CREATE TABLE " + PROFILE_NAME +
                " ("+
                "_id LONG PRIMARY KEY, " +
                PROFILE_COLS[0] + " LONG NOT NULL, " +
                PROFILE_COLS[1] + " TEXT NOT NULL , " + 
                PROFILE_COLS[2] + " TEXT NOT NULL, " +
                PROFILE_COLS[3] + " TEXT NOT NULL , " +
                PROFILE_COLS[4] + " LONG NOT NULL , " +
                PROFILE_COLS[5] + " INTEGER NOT NULL, " +
                PROFILE_COLS[6] + " INTEGER NOT NULL, " +
                PROFILE_COLS[7] + " REAL );";

		public SQLiteHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_PROFILE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ DB_PROFILE_CREATE);
            onCreate(db);
		}
	}

	@Override
	public void createProfile(String nickname, int wsId, String name, String lastName,
			GregorianCalendar borndate, int gender, int height, double weight) {
		ContentValues values = new ContentValues();
		
		values.put(PROFILE_COLS[0], nickname);
		values.put(PROFILE_COLS[1], wsId);
		values.put(PROFILE_COLS[2], name);
		values.put(PROFILE_COLS[3], lastName);
		values.put(PROFILE_COLS[4], borndate.getTimeInMillis());
		values.put(PROFILE_COLS[5], gender);
		values.put(PROFILE_COLS[6], height);
		values.put(PROFILE_COLS[7], weight);
		
		db.insert(PROFILE_NAME, null, values);
	}

	@Override
	public Map<String, Object> getProfile() {
		Cursor cursor;
		Map<String, Object> result = new HashMap<String, Object>();
		
		cursor = db.query(PROFILE_NAME, PROFILE_COLS, null, null, null, null, null);
		cursor.moveToFirst(); //XXX Esta linea es un poco estupida
		
		result.put(PROFILE_COLS[0], cursor.getInt(0));
		result.put(PROFILE_COLS[1], cursor.getString(1));
		result.put(PROFILE_COLS[2], cursor.getString(2));
		result.put(PROFILE_COLS[3], cursor.getString(3));
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(cursor.getLong(4));
		result.put(PROFILE_COLS[4], calendar);
		result.put(PROFILE_COLS[5], cursor.getInt(5));
		result.put(PROFILE_COLS[6], cursor.getInt(6));
		result.put(PROFILE_COLS[7], cursor.getString(7));
		
		return result;
	}

	@Override
	public void updateProfile(String name, String lastName, int gender, int height, double weight) {
		ContentValues values = new ContentValues();
		
		values.put(PROFILE_COLS[2], name);
		values.put(PROFILE_COLS[3], lastName);
		values.put(PROFILE_COLS[5], gender);
		values.put(PROFILE_COLS[6], height);
		values.put(PROFILE_COLS[7], weight);
		
		db.update(PROFILE_NAME, values, null, null);
	}

	@Override
	public String[] getColumns() {
		return PROFILE_COLS;
	}
}
