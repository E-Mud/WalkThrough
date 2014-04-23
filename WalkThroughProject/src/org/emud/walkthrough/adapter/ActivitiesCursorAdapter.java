package org.emud.walkthrough.adapter;

import java.util.GregorianCalendar;

import org.emud.walkthrough.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivitiesCursorAdapter extends CursorAdapter {
	private GregorianCalendar date;

	/**
	 * Constructor de ActivitiesCursorAdapter.
	 * @param context Context en el que se ejecutará el objeto.
	 */
	public ActivitiesCursorAdapter(Context context) {
		super(context, null, 0);
		date = new GregorianCalendar();
	}

	
	/**
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(context);
		
		return inflater.inflate(R.layout.listitem_activity, parent, false);
	}


	/**
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		date.setTimeInMillis(cursor.getLong(1));
		((TextView) view.findViewById(R.id.item_activity_dateday)).setText(DateFormat.format("EEE d/M/yyyy", date));
		((TextView) view.findViewById(R.id.item_activity_datehour)).setText(DateFormat.format("h:m a", date));
	}

}
