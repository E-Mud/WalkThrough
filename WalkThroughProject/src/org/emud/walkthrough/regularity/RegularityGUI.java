package org.emud.walkthrough.regularity;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class RegularityGUI extends ResultGUIResolver {

	@Override
	public int getTitleResource() {
		return R.string.rt_regularity_title;
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_regularity;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_regularity_unit;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<Regularity>(context);
	}

}
