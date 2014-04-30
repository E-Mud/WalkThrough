package org.emud.walkthrough.stub;

import java.text.DecimalFormat;
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

public class MaxMoveListAdapter extends ArrayAdapter<Result> {
	private DecimalFormat df;

	public MaxMoveListAdapter(Context context) {
		super(context, R.layout.listitem_maxmove_result);
		df = new DecimalFormat("#.00");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		TextView label;
		GregorianCalendar cal;
		ResultMaxMove result;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_maxmove_result, null);
		}
	    
		result = (ResultMaxMove) getItem(position);
		cal = result.getDate();
		
	    label = (TextView) convertView.findViewById(R.id.listitem_maxmove_dateday);
	    label.setText(DateFormat.format("d/M/yyyy", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_maxmove_datehour);
	    label.setText(DateFormat.format("h:m a", cal));
	    
	    label = (TextView) convertView.findViewById(R.id.listitem_maxmove_value);
	    //double value = ((Double) result.get()).doubleValue();
	    //label.setText(Double.valueOf(df.format(value)).toString());
	    label.setText(String.format("%.2f", result.get()));
	    
	    return convertView;
	}
}
