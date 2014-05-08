package org.emud.walkthrough;

import org.emud.walkthrough.model.Result;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public interface ResultFactory {
	public final String RESULT_ID_COLUMN = "result_id";
	
	public Result buildResultFromBundle(Bundle bundle);
	
	public Bundle buildBundleFromResult(Result result);

	public ContentValues buildContentValuesFromResult(Result result);

	public Result buildResultFromCursor(Cursor cursor);
	
	public String getTableName();
	
	public String getSQLCreateTableStatement();
}
