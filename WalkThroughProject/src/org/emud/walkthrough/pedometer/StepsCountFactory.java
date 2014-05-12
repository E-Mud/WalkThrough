package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.ResultFactory;
import org.emud.walkthrough.model.Result;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class StepsCountFactory implements ResultFactory {
	private static final String STEPS_KEY = "steps";
	
	private static final String DB_RESULT_STEPS_CREATE = "CREATE TABLE result_steps (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			STEPS_KEY + " INTEGER NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		int steps = bundle.getInt(STEPS_KEY, 0);
		
		return new StepsCount(steps);
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();		
		bundle.putInt(STEPS_KEY, ((StepsCount) result).getSteps());
		bundle.putInt(RESULT_TYPE_KEY, result.getType());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		values.put(STEPS_KEY, ((StepsCount) result).getSteps());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		StepsCount result = new StepsCount();
		result.setSteps(cursor.getInt(cursor.getColumnIndex(STEPS_KEY)));
			
		return result;
	}

	@Override
	public String getTableName() {
		return "result_steps";
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_STEPS_CREATE;
	}

}
