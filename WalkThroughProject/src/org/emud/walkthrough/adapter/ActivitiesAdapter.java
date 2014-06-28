package org.emud.walkthrough.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.resulttype.ResultGUIResolver;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivitiesAdapter extends ArrayAdapter<WalkActivity> {

	public ActivitiesAdapter(Context context) {
		super(context, R.layout.listitem_activity);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent){
		WalkActivity act = getItem(position);
		GregorianCalendar date = act.getDate();

		if(view == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_activity, null);
		}
		
		((TextView) view.findViewById(R.id.item_activity_dateday)).setText(DateFormat.format("EEE d/M/yyyy", date));
		((TextView) view.findViewById(R.id.item_activity_datehour)).setText(DateFormat.format("h:m a", date));
		
		List<Result> results = new ArrayList<Result>(act.getResults());
		
		Collections.sort(results, new Comparator<Result>(){
			@Override
			public int compare(Result lhs, Result rhs) {
				return lhs.getType().intValue() - rhs.getType().intValue();
			}
		});
		
		int[] brands = new int[]{R.id.item_activity_brandcolor1, R.id.item_activity_brandcolor2, R.id.item_activity_brandcolor3,
				R.id.item_activity_brandcolor4, R.id.item_activity_brandcolor5, R.id.item_activity_brandcolor6};
		int n = results.size();
		for(int i=0; i<n; i++){
			ResultGUIResolver resolver = results.get(i).getType().getGUIResolver();
			((ImageView) view.findViewById(brands[i])).setBackgroundResource(resolver.getColorBrandResource());
		}
		
		return view;
	}

}
