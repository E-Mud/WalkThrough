package org.emud.walkthrough;

import java.util.Arrays;
import java.util.List;

import org.emud.support.v4.content.ObserverLoader;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.adapter.ActivitiesAdapter;
import org.emud.walkthrough.analysis.AnalysisService;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ActivitiesQuery;
import org.emud.walkthrough.database.ResultsQuery;
import org.emud.walkthrough.fragment.AutoUpdateListFragment;
import org.emud.walkthrough.fragment.DateFilterFragment;
import org.emud.walkthrough.fragment.NewActivityFragment;
import org.emud.walkthrough.fragment.NewActivityFragment.OnAcceptButtonClickedListener;
import org.emud.walkthrough.fragment.ResultsGraphFragment;
import org.emud.walkthrough.fragment.ResultsListFragment;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements OnClickListener, OnAcceptButtonClickedListener, OnItemClickListener {
	private static final int NEW_ACTIVITY_CONTENT = 0,
			STATISTICS_CONTENT = 1,
			MY_ACTIVITIES_CONTENT = 2,
			MY_RESULTS_CONTENT = 3;
	
	private int currentContent;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListFragment myActivitiesListFragment, myResultsListFragment;
	private ResultsGraphFragment myResultsGraphFragment;
	
	private DateFilterFragment dateFilterFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int serviceState = ((WalkThroughApplication) getApplicationContext()).getServiceState();
		if(serviceState != AnalysisService.SERVICE_NONE){
			Intent intent = new Intent(this, CurrentActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		setContentView(R.layout.activity_main);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
		
		if(savedInstanceState != null){
	        currentContent = savedInstanceState.getInt("currentContent", MY_ACTIVITIES_CONTENT);
		}else{
			currentContent = MY_ACTIVITIES_CONTENT;
		}
		
		
		findViewById(R.id.drawer_newactivity_item).setOnClickListener(this);
		findViewById(R.id.drawer_myactivities_item).setOnClickListener(this);
		findViewById(R.id.drawer_myresults_item).setOnClickListener(this);
		findViewById(R.id.drawer_graph_item).setOnClickListener(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host WalkActivity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_nav_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        drawerLayout.setDrawerListener(drawerToggle);
        
        dateFilterFragment = new DateFilterFragment();
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_datefilter_frame, dateFilterFragment);
		fragmentTransaction.commit();
	}

	

	@Override
	public void onStart(){
		super.onStart();
        
        String activeUserName = ((WalkThroughApplication) getApplicationContext()).getActiveUserName();
        
        if(activeUserName == null){
        	Intent intent = new Intent();
        	intent.setClass(this, LogInActivity.class);
        	startActivity(intent);
        	finish();
        }else{
        	setNewContent(currentContent);
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		
		saveInstanceState.putInt("currentContent", currentContent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (drawerToggle.onOptionsItemSelected(item))
		      return true;
		
		switch (item.getItemId()){
		case R.id.action_settings:
			if (drawerToggle.onOptionsItemSelected(item)) {
				return true;
			}

			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);

			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		int newContent = -1;
				
		switch(id){
		case R.id.drawer_newactivity_item:
			newContent = NEW_ACTIVITY_CONTENT;
			break;
		case R.id.drawer_myactivities_item:
			newContent = MY_ACTIVITIES_CONTENT;
			break;
		case R.id.drawer_myresults_item:
			newContent = MY_RESULTS_CONTENT;
			break;
		case R.id.drawer_graph_item:
			newContent = STATISTICS_CONTENT;
			break;
		default: return;
		}
		
		if(newContent != currentContent)
			setNewContent(newContent);
		
		drawerLayout.closeDrawer(GravityCompat.START);
	}
	
	
	private void setNewContent(int newContent){
		int newTitle;
		Fragment contentFragment;
		FragmentTransaction fragmentTransaction;
				
		switch(newContent){
		case NEW_ACTIVITY_CONTENT:
			newTitle = R.string.newactivity_title;
			contentFragment = new NewActivityFragment();
			((NewActivityFragment) contentFragment).setListener(this);
			break;
		case STATISTICS_CONTENT:
			newTitle = R.string.graph_title;
			if(myResultsGraphFragment == null){
				myResultsGraphFragment = new ResultsGraphFragment();
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				DateFilter dateFilter = dateFilterFragment.getDateFilter();
				ResultsQuery resultsQuery = new ResultsQuery(Result.RT_MAX_MOVE, actDataSource, dateFilter);
				ObserverLoader<List<Result> > loader = new ObserverLoader<List<Result> >(this, resultsQuery, Arrays.asList(new Subject[]{dateFilter.getDataSubject(), actDataSource.getActivitiesSubject()}));
				myResultsGraphFragment.setLoader(loader);
			}
			contentFragment = myResultsGraphFragment;
			break;
		case MY_ACTIVITIES_CONTENT:
			newTitle = R.string.myactivities_title;
			if(myActivitiesListFragment == null){
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				DateFilter dateFilter = dateFilterFragment.getDateFilter();
				ActivitiesQuery activitiesQuery = new ActivitiesQuery(actDataSource, dateFilter);
				ObserverLoader<List<WalkActivity> > loader = new ObserverLoader<List<WalkActivity> >(this, activitiesQuery, Arrays.asList(new Subject[]{dateFilter.getDataSubject(), actDataSource.getActivitiesSubject()}));
				myActivitiesListFragment = AutoUpdateListFragment.newInstance(getResources().getString(R.string.myactivitieslist_empty));
				myActivitiesListFragment.setListAdapter(new ActivitiesAdapter(this));
				((AutoUpdateListFragment) myActivitiesListFragment).setLoader(loader);
				((AutoUpdateListFragment) myActivitiesListFragment).setOnItemClickListener(this);
			}
			contentFragment = myActivitiesListFragment;
			break;
		case MY_RESULTS_CONTENT:
			newTitle = R.string.myresults_title;
			if(myResultsListFragment == null){
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				DateFilter dateFilter = dateFilterFragment.getDateFilter();
				ResultsQuery resultsQuery = new ResultsQuery(Result.RT_MAX_MOVE, actDataSource, dateFilter);
				ObserverLoader<List<Result> > loader = new ObserverLoader<List<Result> >(this, resultsQuery, Arrays.asList(new Subject[]{dateFilter.getDataSubject(), actDataSource.getActivitiesSubject()}));
				myResultsListFragment = ResultsListFragment.newInstance(getResources().getString(R.string.myresultslist_empty));
				((ResultsListFragment) myResultsListFragment).setLoader(loader);
				((ResultsListFragment) myResultsListFragment).setResultType(this, Result.RT_MAX_MOVE);
			}
			contentFragment = myResultsListFragment;
			break;
		default: return;
		}
		
		currentContent = newContent;
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame, contentFragment);
		fragmentTransaction.commit();
		setTitle(newTitle);
		
	}
	
	@Override
	public void acceptButtonClicked(int receiver, List<Integer> analystList) {
		int n = analystList.size();
		int[] resultTypes = new int[n];
		
		for(int i=0; i<n; i++)
			resultTypes[i] = analystList.get(i).intValue();
		
		Intent intentService = new Intent(this, AnalysisService.class);
		intentService.putExtra(AnalysisService.RECEIVER_TYPE_KEY, receiver);
		intentService.putExtra(AnalysisService.RESULTS_TYPES_KEY, resultTypes);
		intentService.putExtra(AnalysisService.SCREEN_KEY, ((WalkThroughApplication) getApplicationContext()).getScreenPref());
		startService(intentService);
		
		((WalkThroughApplication) getApplicationContext()).setServiceState(AnalysisService.SERVICE_PREPARED);
		
		Intent intentCurrentActivity = new Intent(this, CurrentActivity.class);
		startActivity(intentCurrentActivity);
		finish();
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		Intent intent = new Intent(this, DetailActivity.class);
		long real_id = ((WalkActivity)myActivitiesListFragment.getListAdapter().getItem(position)).getId();
		intent.putExtra("activity_id", real_id);
		startActivity(intent);
	}
	
}
