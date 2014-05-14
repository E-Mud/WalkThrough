package org.emud.walkthrough.stub;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class ResultMaxMoveFactory implements ResultFactory {
	private static final String MAX_VALUE_KEY = "maxValue";
	
	private static final String DB_RESULT_MAX_MOVE_CREATE = "CREATE TABLE result_mm (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			MAX_VALUE_KEY + " REAL NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		ResultMaxMove result = new ResultMaxMove(bundle.getDouble(MAX_VALUE_KEY));
		return result;
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();		
		bundle.putDouble(MAX_VALUE_KEY, ((ResultMaxMove) result).getMaxAcceleration());
		bundle.putInt(RESULT_TYPE_KEY, result.getType().intValue());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		
		values.put(MAX_VALUE_KEY, ((ResultMaxMove) result).getMaxAcceleration());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		Result result = new ResultMaxMove(cursor.getDouble(cursor.getColumnIndex(MAX_VALUE_KEY)));
		
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
