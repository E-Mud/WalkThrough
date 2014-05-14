package org.emud.walkthrough.speedometer;

import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultGUIResolver;
import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SpeedGUI implements ResultGUIResolver {

	@Override
	public String getTitle() {
		return "Velocidad media";
	}

	@Override
	public View getDetailView(LayoutInflater inflater, Result result) {
		View view = null;

		view = inflater.inflate(R.layout.base_result_detail, null);

		((ImageView) view.findViewById(R.id.result_icon)).setImageResource(R.drawable.ic_action_done);
		((TextView) view.findViewById(R.id.result_title)).setText(R.string.rt_speed_title);
		((TextView) view.findViewById(R.id.result_unit)).setText(R.string.rt_speed_unit);
		((TextView) view.findViewById(R.id.result_value)).setText("" + String.format("%.2f", ((Speed) result).getSpeed()));
		
		return view;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new SpeedListAdapter(context);
	}

}
