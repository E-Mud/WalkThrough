package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.R;
import org.emud.walkthrough.adapter.ActivitiesAdapter;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ActivitiesQuery;
import org.emud.walkthrough.model.WalkActivity;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ActivitiesListFragment extends ListFragment implements
		OnItemClickListener, LoaderCallbacks<List<WalkActivity> > {
	
	private OnActivitySelectedListener listener;
	private ActivitiesDataSource activitiesDataSource;
	private DateFilter dateFilter;


	public void setActivitiesDataSource(ActivitiesDataSource activitiesDataSource) {
		this.activitiesDataSource = activitiesDataSource;
	}

	public void setDateFilter(DateFilter dateFilter) {
		this.dateFilter = dateFilter;
	}
	
	public void setListener(OnActivitySelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
				
		if(activitiesDataSource != null && dateFilter != null)
			getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		setEmptyText(getActivity().getResources().getString(R.string.myactivitieslist_empty));
		setListAdapter(new ActivitiesAdapter(getActivity()));
		getListView().setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long row_id) {
		if(listener != null)
			listener.activitySelected(((ActivitiesAdapter)getListAdapter()).getItem(position));
	}

	@Override
	public Loader<List<WalkActivity>> onCreateLoader(int id, Bundle args) {
		ActivitiesQuery query = new ActivitiesQuery(activitiesDataSource, dateFilter);
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		
		subjects.add(activitiesDataSource.getActivitiesSubject());
		subjects.add(dateFilter.getDataSubject());
		
		return new ObserverLoader<List<WalkActivity> >(getActivity(), query, subjects);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<List<WalkActivity>> loader, List<WalkActivity> list) {
		ArrayAdapter<WalkActivity> adapter = (ArrayAdapter<WalkActivity>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.addAll(list);
			adapter.notifyDataSetChanged();
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onLoaderReset(Loader<List<WalkActivity> > loader) {
		ArrayAdapter<WalkActivity> adapter = (ArrayAdapter<WalkActivity>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
	}
	
	public static interface OnActivitySelectedListener{
		public void activitySelected(WalkActivity act);
	}
}
