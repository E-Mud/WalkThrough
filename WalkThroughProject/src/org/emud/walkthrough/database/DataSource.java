package org.emud.walkthrough.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.emud.content.DataSubject;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.ActivitiesDataSource;
import org.emud.walkthrough.UserDataSource;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.resulttype.ResultFactory;
import org.emud.walkthrough.resulttype.ResultType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSource implements UserDataSource, ActivitiesDataSource{
	private static final int VERSION = 16;
	protected SQLiteDatabase db;
	private SQLiteHelper helper;
	private Context context;
	
	private DataSubject userSubject, activitiesSubject;
	
	//FIXME Mejorar esto si o si
	private static final String[] PROFILE_COLS = new String[]{
		"wsId", "username", "leglength"
	};
	private static final String[] ACTIVITY_COLS = new String[]{
		"date", "wsId"
	};
	private static final String[] RESULT_COLS = new String[]{
		"activity_id", "resultType", "wsId"
	};
	private static final String PROFILE_NAME = "profile",
			ACTIVITY_NAME = "activity",
			RESULT_NAME = "result";

	public DataSource(Context cont, String userName){
		String database_name = buildDatabaseName(userName);
		context = cont;
		helper = buildHelper(context, database_name, VERSION);
		userSubject = new DataSubject();
		activitiesSubject = new DataSubject();
		openDatabase();
	}
	
	protected SQLiteHelper buildHelper(Context cont, String name, int version){
		return new SQLiteHelper(cont, name, version);
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
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROFILE_COLS[0] + " INTEGER NOT NULL, " +
                PROFILE_COLS[1] + " TEXT NOT NULL , " + 
                PROFILE_COLS[2] + " REAL NOT NULL);";
		private static final String DB_ACTIVITY_CREATE = "CREATE TABLE " + ACTIVITY_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ACTIVITY_COLS[0] + " LONG NOT NULL, " +
				ACTIVITY_COLS[1] + " INTEGER NOT NULL);";

		private static final String DB_RESULT_CREATE = "CREATE TABLE " + RESULT_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				RESULT_COLS[0] + " LONG NOT NULL REFERENCES " + ACTIVITY_NAME + "(_id) ON DELETE CASCADE, " + 
				RESULT_COLS[1] + " INTEGER NOT NULL, " +
				RESULT_COLS[2] + " INTEGER NOT NULL);";

		
		public SQLiteHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_PROFILE_CREATE);
			db.execSQL(DB_ACTIVITY_CREATE);
			db.execSQL(DB_RESULT_CREATE);
			
			for(ResultType type : ResultType.values()){
				ResultFactory factory = type.getFactory();
				db.execSQL(factory.getSQLCreateTableStatement());				
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+ PROFILE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ ACTIVITY_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ RESULT_NAME);
			
			for(ResultType type : ResultType.values()){
				ResultFactory factory = type.getFactory();
				db.execSQL("DROP TABLE IF EXISTS "+ factory.getTableName());
			}
			
            onCreate(db);
			
			//db.execSQL(ResultType.RT_CADENCE.getFactory().getSQLCreateTableStatement());
		}
	}


	@Override
	public void createProfile(User user) {
		ContentValues values = new ContentValues();

		values.put(PROFILE_COLS[0], user.getWebServiceId());
		values.put(PROFILE_COLS[1], user.getUsername());
		values.put(PROFILE_COLS[2], user.getLegLength());
		
		db.insert(PROFILE_NAME, null, values);
	}

	@Override
	public User getProfile() {
		Cursor cursor;
		User result = new User();
		
		cursor = db.query(PROFILE_NAME, PROFILE_COLS, null, null, null, null, null);
		cursor.moveToFirst();
		
		result.setWebServiceId(cursor.getInt(0));
		result.setUsername(cursor.getString(1));
		result.setLegLength(cursor.getDouble(2));
		
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
	public Subject getUserSubject() {
		return userSubject;
	}

	@Override
	public List<WalkActivity> getActivities(GregorianCalendar startDate, GregorianCalendar endDate) {
		Cursor cursor;
		ArrayList<WalkActivity> list = new ArrayList<WalkActivity>();
		
		if(startDate == null && endDate == null){
			cursor = db.query(ACTIVITY_NAME, null, null, null, null, null, ACTIVITY_COLS[0] + " DESC");
		}else{
			cursor = db.query(ACTIVITY_NAME, null, buildFilter(startDate, endDate), null, null, null, ACTIVITY_COLS[0] + " DESC");
		}
		
		if(cursor.moveToFirst()){
			do{
				GregorianCalendar cal = new GregorianCalendar();
				WalkActivity act;
				long activity_id = cursor.getLong(0);
				List<Result> results = this.getActivityResults(activity_id);
				
				cal.setTimeInMillis(cursor.getLong(1));
				act = new WalkActivity(cal, results);
				act.setId(activity_id);
				act.setWebId(cursor.getInt(2));
				list.add(act);
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		
		return list;
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
		values.put(ACTIVITY_COLS[1], act.getWebId());
		activity_id = db.insert(ACTIVITY_NAME, null, values);

		android.util.Log.d("DS", "inserting activity: " + activity_id);
		
		if(results.isEmpty())
			android.util.Log.d("DS", "results empty");
		
		for(Result result : results){
			valuesResult.put(RESULT_COLS[0], activity_id);
			valuesResult.put(RESULT_COLS[1], result.getType().intValue());
			valuesResult.put(RESULT_COLS[2], result.getWebId());
			result_id = db.insert(RESULT_NAME, null, valuesResult);
			
			ResultFactory factory = result.getType().getFactory();
			valuesSubResult = factory.buildContentValuesFromResult(result);
			valuesSubResult.put(ResultFactory.RESULT_ID_COLUMN, result_id);
			db.insert(factory.getTableName(), null, valuesSubResult);
			result.setId(result_id);
		}
		
		getActivitiesSubject().notifyObservers();
		
		act.setId(activity_id);
		
		return activity_id;
	}

	@Override
	public List<Result> getActivityResults(long activity_id) {
		Cursor cursorResult, cursorSubResult;
		ArrayList<Result> results = new ArrayList<Result>();
		
		cursorResult = db.query(RESULT_NAME, null, RESULT_COLS[0] + " = " + activity_id, null, null, null, null);
		
		android.util.Log.d("DS", "activity id: " + activity_id);
		
		if(!cursorResult.moveToFirst()){
			android.util.Log.d("DS", "no results");
			cursorResult.close();
			return results;
		}
		
		do{
			int type = cursorResult.getInt(cursorResult.getColumnIndex(RESULT_COLS[1]));
			ResultType resultType = ResultType.valueOf(type);
			String table;
			Result result;
			ResultFactory factory;
			
			factory = resultType.getFactory();
			table = factory.getTableName();
			
			cursorSubResult = db.query(table, null, ResultFactory.RESULT_ID_COLUMN + " = " + cursorResult.getLong(0), null, null, null, null);
			cursorSubResult.moveToFirst();
			
			result = factory.buildResultFromCursor(cursorSubResult);
			result.setId(cursorResult.getLong(0));
			results.add(result);
			
			cursorSubResult.close();
		}while(cursorResult.moveToNext());
		
		cursorResult.close();
		
		return results;
	}

	@Override
	public WalkActivity getActivity(long activity_id){
		WalkActivity act = null;
		Cursor cursor;
		
		cursor = db.query(ACTIVITY_NAME, null, "_id = " + activity_id, null, null, null, null);
		
		if(cursor.moveToFirst()){
			GregorianCalendar cal = new GregorianCalendar();
			List<Result> results = this.getActivityResults(activity_id);
			
			cal.setTimeInMillis(cursor.getLong(1));
			act = new WalkActivity(cal, results);
			act.setId(activity_id);
			act.setWebId(cursor.getInt(2));
		}
		
		return act;
	}
	
	public void updateActivity(long activity_id, WalkActivity activity){
		ContentValues values = new ContentValues();
		
		values.put(ACTIVITY_COLS[0], activity.getDate().getTimeInMillis());
		values.put(ACTIVITY_COLS[1], activity.getWebId());
		
		android.util.Log.d("DS", "update activity id: " + activity_id);
		
		android.util.Log.d("DS", "update activity id: " + (db == null));
		db.update(ACTIVITY_NAME, values, "_id = " + activity_id, null);
		
		List<Result> results = activity.getResults();
		
		for(Result result : results)
			updateResult(result);
	}
	
	private void updateResult(Result result){
		ResultFactory factory = result.getType().getFactory();
		ContentValues values = factory.buildContentValuesFromResult(result);
		
		android.util.Log.d("DS", "update result id: " + result.getId());
		
		db.update(factory.getTableName(), values, ResultFactory.RESULT_ID_COLUMN + " = " + result.getId(), null);
	}

	@Override
	public List<Result> getResults(int type, GregorianCalendar startDate,
			GregorianCalendar endDate) {
		Cursor cursor;
		GregorianCalendar date;
		HashMap<Long,Long> dateMap = new HashMap<Long,Long>();
		ArrayList<Result> results = new ArrayList<Result>();
		String sqlQuery = "SELECT " + ACTIVITY_COLS[0] + " AS activityDate, " + RESULT_NAME + "._id AS ID FROM " +
						ACTIVITY_NAME + ", " + RESULT_NAME;
		String whereClause;
		
		if(startDate == null && endDate == null){
			whereClause = RESULT_NAME + "." + RESULT_COLS[1] + " = " + type + " AND " +
						RESULT_NAME + "." + RESULT_COLS[0] + " = " + ACTIVITY_NAME + "._id";
		}else{
			whereClause = RESULT_NAME + "." + RESULT_COLS[1] + " = " + type + " AND " +
						RESULT_NAME + "." + RESULT_COLS[0] + " = " + ACTIVITY_NAME + "._id AND " 
						+ buildFilter(startDate, endDate);
		}
						
		cursor = db.rawQuery(sqlQuery + " WHERE " + whereClause, null);		
		
		StringBuilder builder = new StringBuilder();
		if(cursor.moveToFirst()){
			int dateIndex = cursor.getColumnIndex("activityDate");
			do{
				long result_id = cursor.getLong(cursor.getColumnIndex("ID"));
				dateMap.put(Long.valueOf(result_id), Long.valueOf(cursor.getLong(dateIndex)));
				builder.append("" + result_id + ", ");
			}while(cursor.moveToNext());
			builder.delete(builder.length()-2, builder.length());
			cursor.close();
		}else{
			cursor.close();
			return results;
		}
		
		ResultFactory factory = ResultType.valueOf(type).getFactory();
		cursor = db.query(factory.getTableName(), null, "result_id IN("+builder.toString()+")", null, null, null, null);
		
		cursor.moveToFirst();
		
		do{
			Result result = factory.buildResultFromCursor(cursor);
			date = new GregorianCalendar();
			date.setTimeInMillis(dateMap.get(cursor.getLong(cursor.getColumnIndex("result_id"))).longValue());
			result.setDate(date);
			results.add(result);
		}while(cursor.moveToNext());
		
		cursor.close();
		
		Collections.sort(results, new Comparator<Result>(){
			@Override
			public int compare(Result res1, Result res2) {
				return res2.getDate().compareTo(res1.getDate());
			}
		});
		
		return results;
	}

	@Override
	public List<Result> getResults(int type) {
		return getResults(type, null, null);
	}

	private String buildFilter(GregorianCalendar startDate, GregorianCalendar endDate){
		StringBuilder builder = new StringBuilder();
		
		if(startDate != null)
			builder.append(ACTIVITY_COLS[0] + " >= " + startDate.getTimeInMillis());
		
		if(endDate != null){
			if(startDate != null)
				builder.append(" AND ");
			
			builder.append(ACTIVITY_COLS[0] + " <= " + endDate.getTimeInMillis());
		}
		
		return builder.toString();
	}
}
