package org.emud.walkthrough;

import java.util.Arrays;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.support.v4.content.ObserverLoader;
import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ResultsQuery;
import org.emud.walkthrough.fragment.ActivitiesListFragment;
import org.emud.walkthrough.fragment.ActivitiesListFragment.OnActivitySelectedListener;
import org.emud.walkthrough.fragment.DateFilterFragment;
import org.emud.walkthrough.fragment.NewActivityFragment;
import org.emud.walkthrough.fragment.NewActivityFragment.OnAcceptButtonClickedListener;
import org.emud.walkthrough.fragment.ResultsGraphFragment;
import org.emud.walkthrough.fragment.ResultsListFragment;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
import android.widget.ArrayAdapter;

public class MainActivity extends FragmentActivity implements OnClickListener, OnAcceptButtonClickedListener, OnActivitySelectedListener, OnNavigationListener {
	private static final int NEW_ACTIVITY_CONTENT = 0,
			STATISTICS_CONTENT = 1,
			MY_ACTIVITIES_CONTENT = 2,
			MY_RESULTS_CONTENT = 3;
	
	private int currentContent;
	
	private int[] resultTypes;
	private String[] listTitles;
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private ListFragment myActivitiesListFragment, myResultsListFragment;
	private ResultsGraphFragment myResultsGraphFragment;
	
	private DateFilterFragment dateFilterFragment;

	private ResultTypeFilter resultTypeFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//((WalkThroughApplication) getApplicationContext()).setServiceState(AnalysisService.SERVICE_NONE);
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
		
		ActionBar actionBar = getActionBar();
		
		resultTypes = getResources().getIntArray(R.array.result_types);
		listTitles = getResources().getStringArray(R.array.result_titles);
		ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, listTitles);
		actionBar.setListNavigationCallbacks(aAdpt, this);
		
		resultTypeFilter = new ResultTypeFilter(resultTypes[0]);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
        
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
		Fragment contentFragment;
		FragmentTransaction fragmentTransaction;
				
		switch(newContent){
		case NEW_ACTIVITY_CONTENT:
			if(currentContent != MY_ACTIVITIES_CONTENT)
				disableResultTypeFilter();
			setTitle(R.string.newactivity_title);
			contentFragment = new NewActivityFragment();
			((NewActivityFragment) contentFragment).setListener(this);
			((NewActivityFragment) contentFragment).setResultTypes(resultTypes);
			break;
		case MY_ACTIVITIES_CONTENT:
			if(currentContent != NEW_ACTIVITY_CONTENT)
				disableResultTypeFilter();
			setTitle(R.string.myactivities_title);
			if(myActivitiesListFragment == null){
				ActivitiesListFragment actlf = new ActivitiesListFragment();
				actlf.setListener(this);
				actlf.setDateFilter(dateFilterFragment.getDateFilter());
				actlf.setActivitiesDataSource(((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource());
				myActivitiesListFragment = actlf;
			}
			contentFragment = myActivitiesListFragment;
			break;
		case MY_RESULTS_CONTENT:
			if(currentContent != STATISTICS_CONTENT)
				enableResultTypeFilter();
			if(myResultsListFragment == null){
				ResultsListFragment rlf = new ResultsListFragment();
				rlf.setDateFilter(dateFilterFragment.getDateFilter());
				rlf.setResultTypeFilter(resultTypeFilter);
				rlf.setActivitiesDataSource(((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource());
				myResultsListFragment = rlf;
			}
			contentFragment = myResultsListFragment;
			break;
		case STATISTICS_CONTENT:
			if(currentContent != MY_RESULTS_CONTENT)
				enableResultTypeFilter();
			if(myResultsGraphFragment == null){
				myResultsGraphFragment = new ResultsGraphFragment();
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				DateFilter dateFilter = dateFilterFragment.getDateFilter();
				ResultsQuery resultsQuery = new ResultsQuery(resultTypeFilter, actDataSource, dateFilter);
				ObserverLoader<List<Result> > loader = new ObserverLoader<List<Result> >(this, resultsQuery, Arrays.asList(new Subject[]{dateFilter.getDataSubject(), actDataSource.getActivitiesSubject()}));
				myResultsGraphFragment.setLoader(loader);
			}
			contentFragment = myResultsGraphFragment;
			break;
		default: return;
		}
		
		currentContent = newContent;
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame, contentFragment);
		fragmentTransaction.commit();
		
	}
	
	private void enableResultTypeFilter(){
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	
	private void disableResultTypeFilter(){
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
	public void activitySelected(WalkActivity act) {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("activity_id", act.getId());
		startActivity(intent);
	}



	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch(itemPosition){
		case 0:
			resultTypeFilter.setResultType(Result.RT_STEPS);
			return true;
		case 1:
			resultTypeFilter.setResultType(Result.RT_MAX_MOVE);
			return true;
		case 2:
			resultTypeFilter.setResultType(Result.RT_SPEED);
			return true;
		default:
			return false;
		}
	}
	
}
