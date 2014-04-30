package org.emud.walkthrough.adapter;

import java.util.List;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.stub.MaxMoveListAdapter;

import org.emud.walkthrough.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ImageView;

public class ResultGUIResolver {
	private ResultGUIResolver(){
		
	}
/*
	public static View getDetailView(LayoutInflater inflater, Result result) {
		int type = result.getType();
		View view = null;
		
		switch(type){
		case Result.RT_MAX_MOVE:
			view = inflater.inflate(R.layout.base_result_detail, null);
			break;
			default: return null;
		}
		
		int icon = 0, title = 0, unit = 0;
		String value = "";
		
		switch(type){
		case Result.RT_MAX_MOVE:
			icon = R.drawable.ic_action_done;
			title = R.string.rt_max_move_title;
			unit = R.string.rt_max_move_unit;
			value = ((Double) result.get()).toString(); 
			break;
		}

		((ImageView) view.findViewById(R.id.result_icon)).setImageResource(icon);
		((TextView) view.findViewById(R.id.result_title)).setText(title);
		((TextView) view.findViewById(R.id.result_unit)).setText(unit);
		((TextView) view.findViewById(R.id.result_value)).setText(value);
		
		return view;
	}
	
	public static ListAdapter getListAdapter(Context context, int resultType){
		switch(resultType){
		case Result.RT_MAX_MOVE:
			return new MaxMoveListAdapter(context);
		}
		return null;
	}
*/
	public static GraphViewSeries[] getGraphSeries(List<Result> listResults) {
		GraphViewSeries[] seriesArray;
		GraphViewSeries maxValueSeries;
		GraphViewData[] resultData;
		int n = listResults.size();
		
		resultData = new GraphViewData[n];
		
		for(int i=0; i<n; i++){
			Result result = listResults.get(n-1-i);
			resultData[i] = new GraphViewData(result.getDate().getTimeInMillis(), ((Double) result.get()).doubleValue());
		}
		
		maxValueSeries = new GraphViewSeries(resultData);
		seriesArray = new GraphViewSeries[]{maxValueSeries};
		
		return seriesArray;
	}

}
