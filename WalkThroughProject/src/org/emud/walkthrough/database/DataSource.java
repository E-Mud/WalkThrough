package org.emud.walkthrough.database;

import java.util.GregorianCalendar;

import org.emud.walkthrough.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSource implements UserDataSource{
	private static final int VERSION = 1;
	private SQLiteDatabase db;
	private SQLiteHelper helper;
	
	//FIXME Mejorar esto si o si
	private static final String[] PROFILE_COLS = new String[]{
		"wsId", "nickname", "name", "lastname", "borndate", "gender", "height", "weight"
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
	public void createProfile(User user) {
		ContentValues values = new ContentValues();
		
		values.put(PROFILE_COLS[0], user.getUsername());
		values.put(PROFILE_COLS[1], user.getWebServiceId());
		values.put(PROFILE_COLS[2], user.getName());
		values.put(PROFILE_COLS[3], user.getLastname());
		values.put(PROFILE_COLS[4], user.getBorndate().getTimeInMillis());
		values.put(PROFILE_COLS[5], user.getGender());
		values.put(PROFILE_COLS[6], user.getHeight());
		values.put(PROFILE_COLS[7], user.getWeight());
		
		db.insert(PROFILE_NAME, null, values);
	}

	@Override
	public User getProfile() {
		Cursor cursor;
		User result = new User();
		
		cursor = db.query(PROFILE_NAME, PROFILE_COLS, null, null, null, null, null);
		cursor.moveToFirst(); //XXX Esta linea es un poco estupida
		
		result.setWebServiceId(cursor.getInt(0));
		result.setUsername(cursor.getString(1));
		result.setName(cursor.getString(2));
		result.setLastname(cursor.getString(3));
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(cursor.getLong(4));
		result.setBorndate(calendar);
		result.setGender(cursor.getInt(5));
		result.setHeight(cursor.getInt(6));
		result.setWeight(cursor.getDouble(7));
		
		cursor.close();
		
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
