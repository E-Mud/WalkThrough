package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.content.observer.Observer;
import org.emud.content.observer.Subject;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultGUIResolver;
import org.emud.walkthrough.ResultTypeFilter;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ResultsQuery;
import org.emud.walkthrough.model.Result;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

public class ResultsListFragment extends ListFragment  implements LoaderCallbacks<List<Result> >, Observer{
	private ResultTypeFilter resultTypeFilter;
	private ActivitiesDataSource activitiesDataSource;
	private DateFilter dateFilter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		setEmptyText(getActivity().getResources().getString(R.string.myresultslist_empty));

		if(activitiesDataSource != null && dateFilter != null && resultTypeFilter != null){
			updateResultType();
			getLoaderManager().initLoader(0, null, this);
		}
	}
	
	/**
	 * @param resultTypeFilter the resultTypeFilter to set
	 */
	public void setResultTypeFilter(ResultTypeFilter typeFilter) {
		if(resultTypeFilter != null)
			resultTypeFilter.getSubject().unregisterObserver(this);
		
		resultTypeFilter = typeFilter;
		resultTypeFilter.getSubject().registerObserver(this);
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

	public void updateResultType(){
		if(resultTypeFilter != null && isAdded()){
			ResultGUIResolver resolver = ((WalkThroughApplication) getActivity().getApplicationContext()).getGUIResolver(resultTypeFilter.getResultType());
			setListAdapter(resolver.getListAdapter(getActivity()));
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<List<Result>> arg0, List<Result> listResults) {
		ArrayAdapter<Result> adapter = (ArrayAdapter<Result>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.addAll(listResults);
			adapter.notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoaderReset(Loader<List<Result>> arg0) {
		ArrayAdapter<Result> adapter = (ArrayAdapter<Result>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void update() {
		updateResultType();
	}
}
