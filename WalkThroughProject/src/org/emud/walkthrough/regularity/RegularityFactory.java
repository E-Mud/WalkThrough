package org.emud.walkthrough.regularity;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class RegularityFactory implements ResultFactory {
	private static final String REGULARITY_KEY = "regularity", TABLE_NAME = "result_regularity";
	
	private static final String DB_RESULT_REGULARITY_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			REGULARITY_KEY + " REAL NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		double regularity = bundle.getDouble(REGULARITY_KEY);
		
		return new Regularity(regularity);
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();
		
		bundle.putDouble(REGULARITY_KEY, ((Regularity) result).getRegularity());
		bundle.putInt(RESULT_TYPE_KEY, result.getType().intValue());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		
		values.put(REGULARITY_KEY, ((Regularity) result).getRegularity());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		double regularity = cursor.getDouble(cursor.getColumnIndex(REGULARITY_KEY));
		
		return new Regularity(regularity);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_REGULARITY_CREATE;
	}

}
