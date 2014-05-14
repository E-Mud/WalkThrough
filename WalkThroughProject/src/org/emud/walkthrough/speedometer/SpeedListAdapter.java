package org.emud.walkthrough.speedometer;

import java.util.GregorianCalendar;

import org.emud.walkthrough.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpeedListAdapter extends ArrayAdapter<Speed> {

	public SpeedListAdapter(Context context) {
		super(context, R.layout.listitem_speed);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		TextView label;
		GregorianCalendar cal;
		Speed result;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_speed, null);
		}
	    
		result = getItem(position);
		cal = result.getDate();
		
	    label = (TextView) convertView.findViewById(R.id.listitem_speed_dateday);
	    label.setText(DateFormat.format("d/M/yyyy", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_speed_datehour);
	    label.setText(DateFormat.format("h:m a", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_speed_value);
	    label.setText("" + String.format("%.2f", result.getSpeed()) + " m/s");
	    
	    return convertView;
	}
}
