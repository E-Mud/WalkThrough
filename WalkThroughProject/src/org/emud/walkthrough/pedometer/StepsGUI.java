package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultGUIResolver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class StepsGUI implements ResultGUIResolver {

	@Override
	public View getDetailView(LayoutInflater inflater, Result result) {
		View view = null;

		view = inflater.inflate(R.layout.base_result_detail, null);

		((ImageView) view.findViewById(R.id.result_colorbrand)).setBackgroundResource(getColorBrandResource());
		((TextView) view.findViewById(R.id.result_title)).setText(R.string.rt_steps_title);
		((TextView) view.findViewById(R.id.result_unit)).setText(R.string.rt_steps_unit);
		((TextView) view.findViewById(R.id.result_value)).setText("" + ((StepsCount) result).getSteps());
		
		return view;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new StepsListAdapter(context);
	}

	@Override
	public String getTitle() {
		return "Pasos";
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_steps;
	}
}
