package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.DataSubject;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.ResultBuilder;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSource implements UserDataSource, ActivitiesDataSource{
	private static final int VERSION = 2;
	private SQLiteDatabase db;
	private SQLiteHelper helper;
	
	private DataSubject userSubject, activitiesSubject;
	
	//FIXME Mejorar esto si o si
	private static final String[] PROFILE_COLS = new String[]{
		"wsId", "nickname", "name", "lastname", "borndate", "gender", "height", "weight"
	};
	private static final String[] ACTIVITY_COLS = new String[]{
		"date"
	};
	private static final String[] RESULT_COLS = new String[]{
		"activity_id", "resultType"
	};
	private static final String PROFILE_NAME = "profile",
			ACTIVITY_NAME = "activity",
			RESULT_NAME = "result";

	public DataSource(Context context, String userName){
		String database_name = buildDatabaseName(userName);
		helper = new SQLiteHelper(context, database_name, VERSION);
		userSubject = new DataSubject();
		activitiesSubject = new DataSubject();
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
		private static final String DB_ACTIVITY_CREATE = "CREATE TABLE " + ACTIVITY_NAME + " (" +
                "_id LONG PRIMARY KEY, " +
				ACTIVITY_COLS[0] + " LONG NOT NULL);";

		private static final String DB_RESULT_CREATE = "CREATE TABLE " + RESULT_NAME + " (" +
                "_id LONG PRIMARY KEY, " +
				RESULT_COLS[0] + " LONG NOT NULL REFERENCES " + ACTIVITY_NAME + "(_id) ON DELETE CASCADE, " + 
				RESULT_COLS[1] + " INTEGER NOT NULL);";
		
		private static final String DB_RESULT_MAX_MOVE_CREATE = "CREATE TABLE result_mm (" +
                "_id LONG PRIMARY KEY, " +
				"result_id LONG NOT NULL REFERENCES " + RESULT_NAME + "(_id) ON DELETE CASCADE, " + 
				"maxValue REAL NOT NULL);";

		public SQLiteHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_PROFILE_CREATE);
			db.execSQL(DB_ACTIVITY_CREATE);
			db.execSQL(DB_RESULT_CREATE);
			db.execSQL(DB_RESULT_MAX_MOVE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ PROFILE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ ACTIVITY_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ RESULT_NAME);
			db.execSQL("DROP TABLE IF EXISTS result_mm");
            onCreate(db);
		}
	}


	@Override
	public void createProfile(User user) {
		ContentValues values = new ContentValues();

		values.put(PROFILE_COLS[0], user.getWebServiceId());
		values.put(PROFILE_COLS[1], user.getUsername());
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
	
	@Override
	public Subject getUserSubject() {
		return userSubject;
	}

	@Override
	public Cursor getActivities(GregorianCalendar startDate, GregorianCalendar endDate) {
		StringBuilder builder = new StringBuilder();
		Cursor cursor;
		
		if(startDate != null)
			builder.append(ACTIVITY_COLS[0] + " >= " + startDate.getTimeInMillis());
		
		if(endDate != null){
			if(startDate != null)
				builder.append(" AND ");
			
			builder.append(ACTIVITY_COLS[0] + " <= " + endDate.getTimeInMillis());
		}
		
		if(builder.length() > 0){
			cursor = db.query(ACTIVITY_NAME, null, builder.toString(), null, null, null, ACTIVITY_COLS[0] + " DESC");
		}else{
			cursor = db.query(ACTIVITY_NAME, null, null, null, null, null, ACTIVITY_COLS[0] + " DESC");
		}
		
		cursor.moveToFirst();
		
		return cursor;
	}

	@Override
	public Cursor getAllActivities() {
		return getActivities(null, null);
	}

	@Override
	public Subject getActivitiesSubject() {
		return activitiesSubject;
	}

	@Override
	public long createNewActivity(WalkActivity act) {
		ContentValues values = new ContentValues(), valuesResult = new ContentValues(), valuesSubResult;
		long activity_id, result_id;
		List<Result> results = act.getResults();
		
		values.put(ACTIVITY_COLS[0], act.getDate().getTimeInMillis());
		activity_id = db.insert(ACTIVITY_NAME, null, values);
		
		for(Result result : results){
			valuesResult.put(RESULT_COLS[0], activity_id);
			valuesResult.put(RESULT_COLS[1], result.getType());
			result_id = db.insert(RESULT_NAME, null, valuesResult);
			
			valuesSubResult = ResultBuilder.buildContentValuesFromResult(result);
			valuesSubResult.put("result_id", result_id);
			db.insert("result_mm", null, valuesSubResult);
		}
		
		getActivitiesSubject().notifyObservers();
		
		return activity_id;
	}

	
}
