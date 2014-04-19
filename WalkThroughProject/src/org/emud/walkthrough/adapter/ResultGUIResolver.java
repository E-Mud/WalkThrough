package org.emud.walkthrough.adapter;

import org.emud.walkthrough.model.Result;

import org.emud.walkthrough.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

public class ResultGUIResolver {
	private ResultGUIResolver(){
		
	}

	public static View getDetailView(LayoutInflater inflater, Result result) {
		int type = result.getType();
		View view = null;
		
		switch(type){
		case Result.RT_MAX_MOVE:
			view = inflater.inflate(R.layout.base_result_detail, null);
			break;
			default: return null;
		}
		
		int icon = 0, title = 0, unit = 0;
		String value = "";
		
		switch(type){
		case Result.RT_MAX_MOVE:
			icon = R.drawable.ic_action_done;
			title = R.string.rt_max_move_title;
			unit = R.string.rt_max_move_unit;
			value = ((Double) result.get()).toString(); 
			break;
		}

		((ImageView) view.findViewById(R.id.result_icon)).setImageResource(icon);
		((TextView) view.findViewById(R.id.result_title)).setText(title);
		((TextView) view.findViewById(R.id.result_unit)).setText(unit);
		((TextView) view.findViewById(R.id.result_value)).setText(value);
		
		return view;
	}
	
	

}
