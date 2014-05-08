package org.emud.walkthrough;

import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;

public interface ResultGUIResolver {
	
	public String getTitle();
	
	public View getDetailView(LayoutInflater inflater, Result result);
	
	public ListAdapter getListAdapter(Context context);
}
