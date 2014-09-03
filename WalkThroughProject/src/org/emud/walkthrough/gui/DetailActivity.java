package org.emud.walkthrough.gui;

import java.util.List;

import org.emud.content.Query;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.ActivitiesDataSource;
import org.emud.walkthrough.R;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.resulttype.ResultGUIResolver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DetailActivity extends FragmentActivity implements LoaderCallbacks<WalkActivity>{
	private ListView resultsListView;
	private long activity_id;
	private WalkActivity walkActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		resultsListView = (ListView) findViewById(R.id.detail_resultsListView);
		
		activity_id = getIntent().getLongExtra("activity_id", 0);
		
		getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.detail, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_share:
			sendActivityIntent();
			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}

	private void sendActivityIntent() {
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    StringBuilder builder = new StringBuilder();
	    builder.append("He realizado un análisis con GaitAnalyzer:");
	    buildResultsText(builder);
	    intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
	    intent.setType("text/plain");
	    startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_with)));
	}

	private void buildResultsText(StringBuilder builder) {
		List<Result> listResults = walkActivity.getResults();
		for(Result result : listResults){
			ResultGUIResolver guiResolver = result.getType().getGUIResolver();
			builder.append("\n");
			builder.append(getString(guiResolver.getTitleResource()));
			builder.append(": ");
			builder.append(result.valueAsString());
			builder.append(" ");
			builder.append(getString(guiResolver.getUnitResource()));
		}
	}

	@Override
	public Loader<WalkActivity> onCreateLoader(int arg0, Bundle arg1) {
		ActivitiesDataSource dataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
		return new ObserverLoader<WalkActivity>(this, new ActivityQuery(activity_id, dataSource));
	}

	@Override
	public void onLoadFinished(Loader<WalkActivity> loader, WalkActivity activity) {
		List<Result> resultList = activity.getResults();
		
		walkActivity = activity;
		
		resultsListView.setAdapter(new ActivityDetailListAdapter(this, resultList));
	}

	@Override
	public void onLoaderReset(Loader<WalkActivity> arg0) {
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
		    View view = result.getType().getGUIResolver().getDetailView(inflater, result);
		    
		    return view;
		}
	}
	
	private static class ActivityQuery implements Query<WalkActivity>{
		private long act_id;
		private ActivitiesDataSource dataSource;
		
		public ActivityQuery(long id, ActivitiesDataSource ds){
			act_id = id;
			dataSource = ds;
		}

		@Override
		public WalkActivity execute() {
			return dataSource.getActivity(act_id);
		}
		
	}

}
