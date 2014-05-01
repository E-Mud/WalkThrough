package org.emud.walkthrough.fragment;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.R;
import org.emud.walkthrough.adapter.ResultGUIResolver;
import org.emud.walkthrough.model.Result;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultsGraphFragment extends Fragment implements LoaderCallbacks<List<Result> >{
	private ObserverLoader<List<Result> > loader;
	private ViewGroup container;
	private GraphView graphView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_results_graph, null);
		
		this.container = (ViewGroup) view.findViewById(R.id.resultsgraph_content);
		
		return view;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	public ObserverLoader<List<Result> > getLoader() {
		return loader;
	}

	public void setLoader(ObserverLoader<List<Result> > loader) {
		this.loader = loader;
	}

	@Override
	public Loader<List<Result>> onCreateLoader(int arg0, Bundle arg1) {
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<Result>> arg0, List<Result> listResults) {
		GraphViewSeries[] series;
		int n;
		
		if(graphView == null){
			graphView = new LineGraphView(getActivity(), "");
			graphView.setLayoutParams(new ViewGroup.LayoutParams(
		            ViewGroup.LayoutParams.MATCH_PARENT,
		            ViewGroup.LayoutParams.WRAP_CONTENT));
			graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
				  @Override
				  public String formatLabel(double value, boolean isValueX) {
				    if (isValueX) {
				    	GregorianCalendar date = new GregorianCalendar();
				    	date.setTimeInMillis((long) value);
				    	return DateFormat.format("d/M/yy", date).toString();
				    }else{
				    	return String.format("%.2f", value);
				    }
				  }
				});
			container.addView(graphView);
		}else{
			graphView.removeAllSeries();
		}
		
		series = ResultGUIResolver.getGraphSeries(listResults);
		n = series.length;
		
		for(int i=0; i<n; i++)
			graphView.addSeries(series[i]);
	}

	@Override
	public void onLoaderReset(Loader<List<Result>> arg0) {
		if(graphView != null)
			graphView.removeAllSeries();
	}
}
