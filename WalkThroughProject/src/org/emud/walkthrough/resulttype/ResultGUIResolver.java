package org.emud.walkthrough.resulttype;

import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;

public interface ResultGUIResolver {
	
	public String getTitle();
	
	public View getDetailView(LayoutInflater inflater, Result result);
	
	public int getColorBrandResource();
	
	public ListAdapter getListAdapter(Context context);
}
