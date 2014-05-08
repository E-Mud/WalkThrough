package org.emud.walkthrough.fragment;

import java.util.List;

import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.content.Query;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class AutoUpdateListFragment extends ListFragment implements LoaderCallbacks<List<WalkActivity> >{
	private ObserverLoader<List<WalkActivity>> loader;
	private OnItemClickListener listener;

	public AutoUpdateListFragment(){
		
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.listener = listener;
	}
	
	public ObserverLoader<List<WalkActivity>> getLoader() {
		return loader;
	}

	public void setLoader(ObserverLoader<List<WalkActivity>> aloader) {
		this.loader = aloader;
	}

	public void update(){
		getLoaderManager().restartLoader(0, null, this);
	}
	
	public static AutoUpdateListFragment newInstance(String emptytext){
		AutoUpdateListFragment result = new AutoUpdateListFragment();
		Bundle args = new Bundle();
		
		args.putString("emptytext", emptytext);
		
		result.setArguments(args);
		
		return result;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		setEmptyText(getArguments().getString("emptytext"));
		getListView().setOnItemClickListener(listener);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<WalkActivity> > onCreateLoader(int arg0, Bundle arg1) {
		if(loader == null)
			loader = new ObserverLoader<List<WalkActivity> >(getActivity(), new Query<List<WalkActivity> >(){
				@Override
				public List<WalkActivity> execute() {
					return null;
				}
			});
		
		return loader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<List<WalkActivity> > loader, List<WalkActivity> list) {
		ArrayAdapter<WalkActivity> adapter = (ArrayAdapter<WalkActivity>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.addAll(list);
			adapter.notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoaderReset(Loader<List<WalkActivity> >  arg0) {
		ArrayAdapter<WalkActivity> adapter = (ArrayAdapter<WalkActivity>) getListAdapter();
		if(adapter != null){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
	}
}
