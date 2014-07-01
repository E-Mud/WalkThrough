package org.emud.walkthrough.stub;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class MaxMoveGUI extends ResultGUIResolver {
	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<ResultMaxMove>(context);
	}

	@Override
	public int getTitleResource() {
		return R.string.rt_max_move_title;
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_maxaccel;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_max_move_unit;
	}
}
