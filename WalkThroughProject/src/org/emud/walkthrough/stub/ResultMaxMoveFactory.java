package org.emud.walkthrough.stub;

import org.emud.walkthrough.ResultFactory;
import org.emud.walkthrough.model.Result;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class ResultMaxMoveFactory implements ResultFactory {
	private static final String RESULT_TYPE_KEY = "resultType",
					MAX_VALUE_KEY = "maxValue";
	
	private static final String DB_RESULT_MAX_MOVE_CREATE = "CREATE TABLE result_mm (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			MAX_VALUE_KEY + " REAL NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		int type = bundle.getInt(RESULT_TYPE_KEY, -1);
		Result result;
		
		if(type == -1)
			return null;
		
		result = new ResultMaxMove();
		result.set(Double.valueOf(bundle.getDouble(MAX_VALUE_KEY)));
		
		return result;
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = result.toBundle();		
		bundle.putDouble(MAX_VALUE_KEY, ((Double) result.get()).doubleValue());
		bundle.putInt(RESULT_TYPE_KEY, result.getType());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		
		values.put(MAX_VALUE_KEY, (Double) result.get());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		Result result = new ResultMaxMove();
		
		result.set(Double.valueOf(cursor.getDouble(cursor.getColumnIndex(MAX_VALUE_KEY))));
			
		return result;
	}

	@Override
	public String getTableName() {
		return "result_mm";
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_MAX_MOVE_CREATE;
	}

}
