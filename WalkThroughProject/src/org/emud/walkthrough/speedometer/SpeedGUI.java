package org.emud.walkthrough.speedometer;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class SpeedGUI extends ResultGUIResolver {
	@Override
	public int getTitleResource() {
		return R.string.rt_speed_title;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<Speed>(context);
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_speed;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_speed_unit;
	}

}
