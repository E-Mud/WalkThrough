package org.emud.walkthrough.fragment;

import java.util.List;

import org.emud.content.ObserverLoader;
import org.emud.walkthrough.adapter.ResultGUIResolver;
import org.emud.walkthrough.model.Result;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

public class ResultsListFragment extends ListFragment  implements LoaderCallbacks<List<Result> >{
	private ObserverLoader<List<Result> > loader;
	private int resultType = -1;

	
	public static ResultsListFragment newInstance(String emptytext){
		ResultsListFragment result = new ResultsListFragment();
		Bundle args = new Bundle();
		
		args.putString("emptytext", emptytext);
		
		result.setArguments(args);
		
		return result;
	}
	

	public void update(){
		getLoaderManager().restartLoader(0, null, this);
	}
	
	public ObserverLoader<List<Result> > getLoader() {
		return loader;
	}

	public void setLoader(ObserverLoader<List<Result> > loader) {
		this.loader = loader;
	}
	
	//TODO refactoring
	public void setResultType(Context context, int type){
		if(resultType != type){
			resultType = type;
			setListAdapter(ResultGUIResolver.getListAdapter(context, resultType));
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		setEmptyText(getArguments().getString("emptytext"));
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<Result>> onCreateLoader(int arg0, Bundle arg1) {
		return loader;
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
