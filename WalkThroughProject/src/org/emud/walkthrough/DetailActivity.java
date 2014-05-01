package org.emud.walkthrough;

import java.util.List;

import org.emud.support.v4.content.ObserverLoader;
import org.emud.content.Query;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.model.Result;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DetailActivity extends FragmentActivity implements LoaderCallbacks<List<Result> >{
	private ListView resultsListView;
	private long activity_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		resultsListView = (ListView) findViewById(R.id.detail_resultsListView);
		
		activity_id = getIntent().getLongExtra("activity_id", 0);
		
		getSupportLoaderManager().initLoader(0, null, this);
	}
	
	

	@Override
	public Loader<List<Result>> onCreateLoader(int arg0, Bundle arg1) {
		ActivitiesDataSource dataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
		return new ObserverLoader<List<Result> >(this, new ActivityResultsQuery(activity_id, dataSource));
	}

	@Override
	public void onLoadFinished(Loader<List<Result>> loader, List<Result> resultList) {
		resultsListView.setAdapter(new ActivityDetailListAdapter(this, resultList));
	}

	@Override
	public void onLoaderReset(Loader<List<Result>> arg0) {
		resultsListView.setAdapter(null);
	}
	

	private static class ActivityDetailListAdapter extends ArrayAdapter<Result>{
		private List<Result> resultsList;

		public ActivityDetailListAdapter(Context context, List<Result> objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
			
			resultsList = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
		    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    Result result = resultsList.get(position);
		    View view = ((WalkThroughApplication) getContext().getApplicationContext()).getGUIResolver(result.getType()).getDetailView(inflater, result);
		    
		    return view;
		}
	}
	
	private static class ActivityResultsQuery implements Query<List<Result> >{
		private long act_id;
		private ActivitiesDataSource dataSource;
		
		public ActivityResultsQuery(long id, ActivitiesDataSource ds){
			act_id = id;
			dataSource = ds;
		}

		@Override
		public List<Result> execute() {
			return dataSource.getActivityResults(act_id);
		}
		
	}

}
