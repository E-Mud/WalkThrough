package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultTypeFilter;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ResultsQuery;
import org.emud.walkthrough.model.Result;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class ResultsGraphFragment extends Fragment implements LoaderCallbacks<List<Result> >{
	private ResultTypeFilter resultTypeFilter;
	private ActivitiesDataSource activitiesDataSource;
	private DateFilter dateFilter;
	private ViewGroup container;
	private GraphView graphView;
	private CustomLabelFormatter labelFormatter;	

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
		
		labelFormatter = new CustomLabelFormatter() {
			@SuppressLint("DefaultLocale")
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
		};
		
		if(activitiesDataSource != null && dateFilter != null && resultTypeFilter != null)
			getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * @param resultTypeFilter the resultTypeFilter to set
	 */
	public void setResultTypeFilter(ResultTypeFilter typeFilter) {
		resultTypeFilter = typeFilter;
	}

	/**
	 * @param activitiesDataSource the activitiesDataSource to set
	 */
	public void setActivitiesDataSource(ActivitiesDataSource activitiesDataSource) {
		this.activitiesDataSource = activitiesDataSource;
	}

	/**
	 * @param dateFilter the dateFilter to set
	 */
	public void setDateFilter(DateFilter dateFilter) {
		this.dateFilter = dateFilter;
	}

	@Override
	public Loader<List<Result>> onCreateLoader(int arg0, Bundle arg1) {
		ResultsQuery query = new ResultsQuery(resultTypeFilter, activitiesDataSource, dateFilter);
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		
		subjects.add(activitiesDataSource.getActivitiesSubject());
		subjects.add(dateFilter.getDataSubject());
		subjects.add(resultTypeFilter.getSubject());
		
		return new ObserverLoader<List<Result> >(getActivity(), query, subjects);
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
			graphView.setCustomLabelFormatter(labelFormatter);
			container.addView(graphView);
		}else{
			graphView.removeAllSeries();
		}
		
		series = getGraphSeries(listResults);
		n = series.length;
		
		for(int i=0; i<n; i++)
			graphView.addSeries(series[i]);
	}

	@Override
	public void onLoaderReset(Loader<List<Result>> arg0) {
		if(graphView != null)
			graphView.removeAllSeries();
	}
	
	private static GraphViewSeries[] getGraphSeries(List<Result> listResults) {
		GraphViewSeries[] seriesArray;
		GraphViewSeries maxValueSeries;
		GraphViewData[] resultData;
		int n = listResults.size();

		resultData = new GraphViewData[n];

		for(int i=0; i<n; i++){
			Result result = listResults.get(n-1-i);
			resultData[i] = new GraphViewData(result.getDate().getTimeInMillis(), result.doubleValue());
		}

		maxValueSeries = new GraphViewSeries(resultData);
		seriesArray = new GraphViewSeries[]{maxValueSeries};

		return seriesArray;
	}
}
