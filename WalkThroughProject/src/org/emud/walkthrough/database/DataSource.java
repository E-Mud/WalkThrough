package org.emud.walkthrough.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.emud.content.DataSubject;
import org.emud.content.observer.Subject;
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
	private static final int VERSION = 11;
	private SQLiteDatabase db;
	private SQLiteHelper helper;
	private Context context;
	
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

	public DataSource(Context cont, String userName){
		String database_name = buildDatabaseName(userName);
		context = cont;
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
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROFILE_COLS[0] + " LONG NOT NULL, " +
                PROFILE_COLS[1] + " TEXT NOT NULL , " + 
                PROFILE_COLS[2] + " TEXT NOT NULL, " +
                PROFILE_COLS[3] + " TEXT NOT NULL , " +
                PROFILE_COLS[4] + " LONG NOT NULL , " +
                PROFILE_COLS[5] + " INTEGER NOT NULL, " +
                PROFILE_COLS[6] + " INTEGER NOT NULL, " +
                PROFILE_COLS[7] + " REAL );";
		private static final String DB_ACTIVITY_CREATE = "CREATE TABLE " + ACTIVITY_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ACTIVITY_COLS[0] + " LONG NOT NULL);";

		private static final String DB_RESULT_CREATE = "CREATE TABLE " + RESULT_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				RESULT_COLS[0] + " LONG NOT NULL REFERENCES " + ACTIVITY_NAME + "(_id) ON DELETE CASCADE, " + 
				RESULT_COLS[1] + " INTEGER NOT NULL);";

		
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
			/*db.execSQL("DROP TABLE IF EXISTS "+ PROFILE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ ACTIVITY_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+ RESULT_NAME);
			
			for(ResultType type : ResultType.values()){
				ResultFactory factory = type.getFactory();
				db.execSQL("DROP TABLE IF EXISTS "+ factory.getTableName());
			}
			
            onCreate(db);*/
			db.execSQL(ResultType.RT_CADENCE.getFactory().getSQLCreateTableStatement());
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
		cursor.moveToFirst();
		
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
				
				cal.setTimeInMillis(cursor.getLong(1));
				act = new WalkActivity(cal);
				act.setId(cursor.getLong(0));
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
		activity_id = db.insert(ACTIVITY_NAME, null, values);

		android.util.Log.d("DS", "inserting activity: " + activity_id);
		
		if(results.isEmpty())
			android.util.Log.d("DS", "results empty");
		
		for(Result result : results){
			valuesResult.put(RESULT_COLS[0], activity_id);
			valuesResult.put(RESULT_COLS[1], result.getType().intValue());
			result_id = db.insert(RESULT_NAME, null, valuesResult);
			
			ResultFactory factory = result.getType().getFactory();
			valuesSubResult = factory.buildContentValuesFromResult(result);
			valuesSubResult.put(ResultFactory.RESULT_ID_COLUMN, result_id);
			db.insert(factory.getTableName(), null, valuesSubResult);
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
			results.add(result);
			
			cursorSubResult.close();
		}while(cursorResult.moveToNext());
		
		cursorResult.close();
		
		return results;
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
