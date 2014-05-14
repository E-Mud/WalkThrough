package org.emud.walkthrough.speedometer;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class SpeedFactory implements ResultFactory {
	private static final String SPEED_KEY = "speed", TABLE_NAME = "result_speed";
	
	private static final String DB_RESULT_SPEED_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			SPEED_KEY + " REAL NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		double speed = bundle.getDouble(SPEED_KEY, 0.0d);
		
		return new Speed(speed);
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();		
		bundle.putDouble(SPEED_KEY, ((Speed) result).getSpeed());
		bundle.putInt(RESULT_TYPE_KEY, result.getType().intValue());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		values.put(SPEED_KEY, ((Speed) result).getSpeed());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		Speed speedResult = new Speed();
		speedResult.setSpeed(cursor.getDouble(cursor.getColumnIndex(SPEED_KEY)));
			
		return speedResult;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_SPEED_CREATE;
	}

}
