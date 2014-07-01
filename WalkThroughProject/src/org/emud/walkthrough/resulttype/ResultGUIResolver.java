package org.emud.walkthrough.resulttype;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public abstract class ResultGUIResolver {
	
	public View getDetailView(LayoutInflater inflater, Result result){
		View view = null;

		view = inflater.inflate(R.layout.base_result_detail, null);

		((ImageView) view.findViewById(R.id.result_colorbrand)).setBackgroundResource(getColorBrandResource());
		((TextView) view.findViewById(R.id.result_title)).setText(getLongTitleResource());
		((TextView) view.findViewById(R.id.result_unit)).setText(getUnitResource());
		((TextView) view.findViewById(R.id.result_value)).setText(result.valueAsString());
		
		return view;
	}
	
	public abstract int getTitleResource();
	
	public int getLongTitleResource(){
		return getTitleResource();
	}
	
	public abstract int getColorBrandResource();
	
	public abstract int getUnitResource();
	
	public abstract ListAdapter getListAdapter(Context context);
}
