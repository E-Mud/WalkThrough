package org.emud.walkthrough.adapter;

import java.util.GregorianCalendar;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.WalkActivity;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ActivitiesAdapter extends ArrayAdapter<WalkActivity> {

	public ActivitiesAdapter(Context context) {
		super(context, R.layout.listitem_activity);
	}
	

	@Override
	public View getView(int position, View view, ViewGroup parent){
		WalkActivity act = getItem(position);
		GregorianCalendar date = act.getDate();

		if(view == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_activity, null);
		}
		
		((TextView) view.findViewById(R.id.item_activity_dateday)).setText(DateFormat.format("EEE d/M/yyyy", date));
		((TextView) view.findViewById(R.id.item_activity_datehour)).setText(DateFormat.format("h:m a", date));
		
		return view;
	}

}
