package org.emud.walkthrough.model;

import org.emud.walkthrough.stub.ResultMaxMove;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class ResultBuilder {
	private static final String RESULT_TYPE_KEY = "resultType";
	
	private ResultBuilder(){
	}
	
	public static Result buildResultFromBundle(Bundle bundle){
		int type = bundle.getInt(RESULT_TYPE_KEY, -1);
		Result result;
		
		if(type == -1)
			return null;
		
		switch(type){
		case Result.RT_MAX_MOVE:
			result = new ResultMaxMove();
			break;
		default: return null;
		}
		
		result.fromBundle(bundle);
		
		return result;
	}
	
	public static Bundle buildBundleFromResult(Result result){
		Bundle bundle = result.toBundle();		
		bundle.putInt(RESULT_TYPE_KEY, result.getType());
		
		return bundle;
	}

	public static ContentValues buildContentValuesFromResult(Result result) {
		return result.toContentValues();
	}

	public static Result buildResultFromCursor(Cursor cursor, int type) {
		Result result;
		
		if(type == -1)
			return null;
		
		switch(type){
		case Result.RT_MAX_MOVE:
			result = new ResultMaxMove();
			result.set(Double.valueOf(cursor.getDouble(cursor.getColumnIndex("maxValue"))));
			break;
		default: return null;
		}
		
		return result;
	}
	
	public static String getTableName(int type){
		switch(type){
		case Result.RT_MAX_MOVE:
			return "result_mm";
		default: return "";
		}
	}
}
