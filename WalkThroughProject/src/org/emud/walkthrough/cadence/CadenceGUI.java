package org.emud.walkthrough.cadence;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class CadenceGUI extends ResultGUIResolver {

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<Cadence>(context);
	}

	@Override
	public int getTitleResource() {
		return R.string.rt_cadence_title;
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_cadence;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_cadence_unit;
	}
}
