package org.emud.walkthrough.length;

import org.emud.walkthrough.R;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultListAdapter;

import android.content.Context;
import android.widget.ListAdapter;

public class LengthGUI extends ResultGUIResolver {

	@Override
	public int getTitleResource() {
		return R.string.rt_length_title;
	}
	
	@Override
	public int getLongTitleResource(){
		return R.string.rt_length_long_title;
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_length;
	}

	@Override
	public int getUnitResource() {
		return R.string.rt_length_unit;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new ResultListAdapter<Length>(context);
	}

}
