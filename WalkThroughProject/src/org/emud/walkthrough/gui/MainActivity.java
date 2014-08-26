package org.emud.walkthrough.gui;

import java.util.List;

import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultTypeFilter;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.R.drawable;
import org.emud.walkthrough.R.id;
import org.emud.walkthrough.R.layout;
import org.emud.walkthrough.R.menu;
import org.emud.walkthrough.R.string;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.gui.fragment.ActivitiesListFragment;
import org.emud.walkthrough.gui.fragment.DateFilterFragment;
import org.emud.walkthrough.gui.fragment.NewActivityFragment;
import org.emud.walkthrough.gui.fragment.ResultsGraphFragment;
import org.emud.walkthrough.gui.fragment.ResultsListFragment;
import org.emud.walkthrough.gui.fragment.ActivitiesListFragment.OnActivitySelectedListener;
import org.emud.walkthrough.gui.fragment.NewActivityFragment.OnAcceptButtonClickedListener;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultType;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener, OnAcceptButtonClickedListener, OnActivitySelectedListener, OnNavigationListener {
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

	private ArrayAdapter<ResultType> spinnerAdapter;
	private ResultTypeFilter resultTypeFilter;
	
	private List<ResultType> analysts;
	private int receiverType;

	private boolean closingOnFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean analysisServiceRunning = isAnalysisServiceRunning();
		
		if(analysisServiceRunning){
			Intent intent = new Intent(this, CurrentActivity.class);
			startActivity(intent);
        	closingOnFinish = false;
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
		
		spinnerAdapter = new ResultDropdownAdapter(this);
		actionBar.setListNavigationCallbacks(spinnerAdapter, this);
		
		resultTypeFilter = new ResultTypeFilter(ResultType.RT_STEPS);
		
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
		
		closingOnFinish = true;
	}

	private boolean isAnalysisServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (AnalysisService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public void onStart(){
		super.onStart();
        
        String activeUserName = ((WalkThroughApplication) getApplicationContext()).getActiveUserName();
        
        if(activeUserName == null){
        	Intent intent = new Intent();
        	intent.setClass(this, LogInActivity.class);
        	startActivity(intent);
        	closingOnFinish = false;
        	finish();
        }else{
        	setNewContent(currentContent);
        }
	}
	
	@Override
	public void onDestroy(){
		if(closingOnFinish){
			closingOnFinish = false;
			((WalkThroughApplication) getApplicationContext()).onClose();
		}
		super.onDestroy();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){			
			startCurrentActivity();
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
				myResultsGraphFragment.setDateFilter(dateFilterFragment.getDateFilter());
				myResultsGraphFragment.setResultTypeFilter(resultTypeFilter);
				myResultsGraphFragment.setActivitiesDataSource(((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource());
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
	
	@SuppressLint("NewApi")
	@Override
	public void acceptButtonClicked(int receiver, List<ResultType> analystList) {
		receiverType = receiver;
		analysts = analystList;
		if(receiver == WalkDataReceiver.TWO_ACCELEROMETERS){
			BluetoothManager bluetoothManager =
	                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
	        if (!bluetoothAdapter.isEnabled()) {
	        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        	startActivityForResult(enableBtIntent, 0);
	        }else{
	        	startCurrentActivity();
	        }
		}else{
			startCurrentActivity();
		}
	}

	private void startCurrentActivity() {
		int n = analysts.size();
		int[] resultTypes = new int[n];
		
		for(int i=0; i<n; i++)
			resultTypes[i] = analysts.get(i).intValue();
		
		Intent intentService = new Intent(this, AnalysisService.class);
		intentService.putExtra(AnalysisService.RECEIVER_TYPE_KEY, receiverType);
		intentService.putExtra(AnalysisService.RESULTS_TYPES_KEY, resultTypes);
		intentService.putExtra(AnalysisService.SCREEN_KEY, ((WalkThroughApplication) getApplicationContext()).getScreenPref());
		startService(intentService);
		
		((WalkThroughApplication) getApplicationContext()).setServiceState(AnalysisService.SERVICE_PREPARED);
		
		Intent intentCurrentActivity = new Intent(this, CurrentActivity.class);
		intentCurrentActivity.putExtra(AnalysisService.RECEIVER_TYPE_KEY, receiverType);
		startActivity(intentCurrentActivity);
    	closingOnFinish = false;
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
		resultTypeFilter.setResultType(spinnerAdapter.getItem(itemPosition));
		return true;
	}
	
	private static class ResultDropdownAdapter extends ArrayAdapter<ResultType>{

		public ResultDropdownAdapter(Context context) {
			super(context, R.layout.listitem_dropdown, R.id.item_dropdown_title, ResultType.values());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup){
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listitem_dropdown, null);
			}

			ResultGUIResolver resolver = getItem(position).getGUIResolver();
			((TextView) convertView.findViewById(R.id.item_dropdown_title)).setText(resolver.getTitleResource());
			((ImageView) convertView.findViewById(R.id.item_dropdown_brandcolor)).setBackgroundResource(resolver.getColorBrandResource());
			
			return convertView;
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup viewGroup){
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listitem_dropdown, null);
			}
			
			ResultGUIResolver resolver = getItem(position).getGUIResolver();
			((TextView) convertView.findViewById(R.id.item_dropdown_title)).setText(resolver.getTitleResource());
			((ImageView) convertView.findViewById(R.id.item_dropdown_brandcolor)).setBackgroundResource(resolver.getColorBrandResource());
			
			return convertView;
		}
	}
	
}
