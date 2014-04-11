package org.emud.walkthrough.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.emud.walkthrough.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivitiesCursorAdapter extends CursorAdapter {
	private DateFormat dateFormat;
	private GregorianCalendar date;

	public ActivitiesCursorAdapter(Context context) {
		super(context, null, 0);
		
		dateFormat = new SimpleDateFormat("EEE d/M/yyyy");
		date = new GregorianCalendar();
	}

	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(context);
		
		return inflater.inflate(R.layout.item_activity, parent, false);
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		date.setTimeInMillis(cursor.getLong(1));
		((TextView) view.findViewById(R.id.item_activity_datetext)).setText(""+date.get(Calendar.DAY_OF_MONTH)+"/"+date.get(Calendar.MONTH));
	}

}
