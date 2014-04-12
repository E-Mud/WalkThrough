package org.emud.walkthrough.fragment;

import org.emud.content.ObserverCursorLoader;
import org.emud.content.Query;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
public class AutoUpdateListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	private ObserverCursorLoader loader;

	public AutoUpdateListFragment(){
		
	}
	
	
	public ObserverCursorLoader getLoader() {
		return loader;
	}

	public void setLoader(ObserverCursorLoader loader) {
		this.loader = loader;
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
		
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		if(loader == null)
			loader = new ObserverCursorLoader(getActivity(), new Query<Cursor>(){
				@Override
				public Cursor execute() {
					return null;
				}
			});
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		CursorAdapter adapter = (CursorAdapter) getListAdapter();
		if(adapter != null){
			adapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		CursorAdapter adapter = (CursorAdapter) getListAdapter();
		if(adapter != null){
			adapter.swapCursor(null);
		}
	}
}
