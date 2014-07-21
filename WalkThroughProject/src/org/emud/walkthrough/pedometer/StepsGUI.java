package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class StepsGUI extends ResultGUIResolver {
	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<Steps>(context);
	}

	@Override
	public int getTitleResource() {
		return R.string.rt_steps_title;
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_steps;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_steps_unit;
	}
}
