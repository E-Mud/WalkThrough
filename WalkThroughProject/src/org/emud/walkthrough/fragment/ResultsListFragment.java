package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultGUIResolver;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ResultsQuery;
import org.emud.walkthrough.model.Result;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

public class ResultsListFragment extends ListFragment  implements LoaderCallbacks<List<Result> >{
	private ObserverLoader<List<Result> > loader;
	private int resultType = -1;
	private ActivitiesDataSource activitiesDataSource;
	private DateFilter dateFilter;

	
	public static ResultsListFragment newInstance(int type){
		ResultsListFragment result = new ResultsListFragment();
		Bundle args = new Bundle();
		
		args.putInt("resultType", type);
		
		result.setArguments(args);
		
		return result;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		setEmptyText(getActivity().getResources().getString(R.string.myresultslist_empty));
		setResultType(getArguments().getInt("resultType"));

		if(activitiesDataSource != null && dateFilter != null)
			getLoaderManager().initLoader(0, null, this);
	}
	
	public ObserverLoader<List<Result> > getLoader() {
		return loader;
	}

	public void setLoader(ObserverLoader<List<Result> > loader) {
		this.loader = loader;
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

	//TODO refactoring
	public void setResultType(int type){
		if(resultType != type && isAdded()){
			resultType = type;
			ResultGUIResolver resolver = ((WalkThroughApplication) getActivity().getApplicationContext()).getGUIResolver(resultType);
			setListAdapter(resolver.getListAdapter(getActivity()));
		}
	}

	@Override
	public Loader<List<Result>> onCreateLoader(int arg0, Bundle arg1) {
		ResultsQuery query = new ResultsQuery(resultType, activitiesDataSource, dateFilter);
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		
		subjects.add(activitiesDataSource.getActivitiesSubject());
		subjects.add(dateFilter.getDataSubject());
		
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
}
