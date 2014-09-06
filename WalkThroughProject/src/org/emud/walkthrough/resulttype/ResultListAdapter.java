package org.emud.walkthrough.resulttype;

import java.util.GregorianCalendar;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultListAdapter<T extends Result> extends ArrayAdapter<T> {

	public ResultListAdapter(Context context) {
		super(context, R.layout.listitem_result);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		TextView label;
		GregorianCalendar cal;
		Result result;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_result, null);
		}
	    
		result = getItem(position);
		cal = result.getDate();
		
	    label = (TextView) convertView.findViewById(R.id.listitem_result_dateday);
	    label.setText(DateFormat.format("d/M/yyyy", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_result_datehour);
	    label.setText(DateFormat.format("h:m a", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_result_value);
	    label.setText(result.valueAsString());
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_result_unit);
	    label.setText(result.getType().getGUIResolver().getUnitResource());
	    
	    return convertView;
	}
}
