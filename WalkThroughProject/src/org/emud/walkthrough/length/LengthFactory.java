package org.emud.walkthrough.length;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class LengthFactory implements ResultFactory {
	private static final String LENGTH_KEY = "length", TABLE_NAME = "result_length";
	
	private static final String DB_RESULT_LENGTH_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			LENGTH_KEY + " REAL NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		double length = bundle.getDouble(LENGTH_KEY);
		
		return new Length(length);
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();
		
		bundle.putDouble(LENGTH_KEY, ((Length) result).getLength());
		bundle.putInt(RESULT_TYPE_KEY, result.getType().intValue());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		
		values.put(LENGTH_KEY, ((Length) result).getLength());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		double length = cursor.getDouble(cursor.getColumnIndex(LENGTH_KEY));
		
		return new Length(length);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_LENGTH_CREATE;
	}
}
