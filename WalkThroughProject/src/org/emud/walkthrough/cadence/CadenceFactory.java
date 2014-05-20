package org.emud.walkthrough.cadence;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class CadenceFactory implements ResultFactory {
	private static final String CADENCE_KEY = "steps";
	
	private static final String DB_RESULT_CADENCE_CREATE = "CREATE TABLE result_cadence (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESULT_ID_COLUMN + " LONG NOT NULL REFERENCES result(_id) ON DELETE CASCADE, " + 
			CADENCE_KEY + " INTEGER NOT NULL);";

	@Override
	public Result buildResultFromBundle(Bundle bundle) {
		double cadence = bundle.getDouble(CADENCE_KEY, 0);
		
		return new Cadence(cadence);
	}

	@Override
	public Bundle buildBundleFromResult(Result result) {
		Bundle bundle = new Bundle();		
		bundle.putDouble(CADENCE_KEY, ((Cadence) result).getCadence());
		bundle.putInt(RESULT_TYPE_KEY, result.getType().intValue());
		
		return bundle;
	}

	@Override
	public ContentValues buildContentValuesFromResult(Result result) {
		ContentValues values = new ContentValues();
		values.put(CADENCE_KEY, ((Cadence) result).getCadence());
		
		return values;
	}

	@Override
	public Result buildResultFromCursor(Cursor cursor) {
		Cadence result = new Cadence();
		result.setCadence(cursor.getDouble(cursor.getColumnIndex(CADENCE_KEY)));
			
		return result;
	}

	@Override
	public String getTableName() {
		return "result_cadence";
	}

	@Override
	public String getSQLCreateTableStatement() {
		return DB_RESULT_CADENCE_CREATE;
	}
}
