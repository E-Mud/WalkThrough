package org.emud.walkthrough.cadence;

import java.util.GregorianCalendar;

import org.emud.walkthrough.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CadenceListAdapter extends ArrayAdapter<Cadence>{
	
	public CadenceListAdapter(Context context) {
		super(context, R.layout.listitem_cadence);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		TextView label;
		GregorianCalendar cal;
		Cadence result;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_cadence, null);
		}
	    
		result = getItem(position);
		cal = result.getDate();
		
	    label = (TextView) convertView.findViewById(R.id.listitem_cadence_dateday);
	    label.setText(DateFormat.format("d/M/yyyy", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_cadence_datehour);
	    label.setText(DateFormat.format("h:m a", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_cadence_value);
	    label.setText("" + String.format("%.2f", result.getCadence()));
	    
	    return convertView;
	}
}
