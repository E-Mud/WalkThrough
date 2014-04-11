package org.emud.walkthrough;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.emud.content.DataSubject;
import org.emud.content.ObserverCursorLoader;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.adapter.ActivitiesCursorAdapter;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ActivitiesQuery;
import org.emud.walkthrough.dialogfragment.DatePickerDialogFragment;
import org.emud.walkthrough.dialogfragment.DatePickerDialogFragment.OnDatePickedListener;
import org.emud.walkthrough.fragment.AutoUpdateListFragment;
import org.emud.walkthrough.model.WalkActivity;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener, OnDatePickedListener {
	private static final int NEW_ACTIVITY_CONTENT = 0,
			FALLING_DETECTION_CONTENT = 1,
			MY_ACTIVITIES_CONTENT = 2,
			MY_RESULTS_CONTENT = 3;
	
	private int currentContent;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListFragment myActivitiesListFragment;
	
	private GregorianCalendar fromDate, toDate;
	private TextView fromText, toText;
	private DataSubject filterSubject;
	
	private int dateDialogShowing;

	private ActivitiesQuery activitiesQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
		fromDate = null;
		toDate = null;
		//fromText = (TextView) findViewById(R.id.textView2);
		//toText = (TextView) findViewById(R.id.mainactivity_to);
		filterSubject = new DataSubject();
		
		//fromText.setOnClickListener(this);
		//toText.setOnClickListener(this);
		
		findViewById(R.id.drawer_newactivity_item).setOnClickListener(this);
		findViewById(R.id.drawer_fallingdetection_item).setOnClickListener(this);
		findViewById(R.id.drawer_myactivities_item).setOnClickListener(this);
		findViewById(R.id.drawer_myresults_item).setOnClickListener(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        
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
	}

	@Override
	public void onResume(){
		super.onResume();
			
		
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
		case R.id.action_filter:
			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void datePicked(int year, int month, int day) {
		if(dateDialogShowing == R.id.textView2){
			fromDate.set(Calendar.YEAR, year);
			fromDate.set(Calendar.MONTH, month);
			fromDate.set(Calendar.DAY_OF_MONTH, day);
			//fromText.setText(""+day+"/"+month+"/"+year);
			activitiesQuery.setFromDate(fromDate);
		}else{
			toDate.set(Calendar.YEAR, year);
			toDate.set(Calendar.MONTH, month);
			toDate.set(Calendar.DAY_OF_MONTH, day);
			//toText.setText(""+day+"/"+month+"/"+year);
			activitiesQuery.setToDate(toDate);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		
		switch(id){
		case R.id.textView2:
		case R.id.mainactivity_to:
			onDateFilterClick(id);
			break;
		case R.id.drawer_newactivity_item:
		case R.id.drawer_fallingdetection_item:
		case R.id.drawer_myactivities_item:
		case R.id.drawer_myresults_item:
			onDrawerItemClick(id);
			break;
		default: break;
		}
	}
	
	private void onDateFilterClick(int id){
		DatePickerDialogFragment dialogFragment;
		GregorianCalendar date;
		
		if(id == R.id.textView2){
			date = fromDate;
		}else{
			date = toDate;
		}
		
		dateDialogShowing = id;

		dialogFragment = DatePickerDialogFragment.newInstance(date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
		dialogFragment.setOnDatePickedListener(this);
		dialogFragment.show(getSupportFragmentManager(), "datePickerDialog");
	}
	
	private void onDrawerItemClick(int id){
		int newTitle;
		Fragment contentFragment;
		FragmentTransaction fragmentTransaction;
				
		switch(id){
		case R.id.drawer_newactivity_item:
			currentContent = NEW_ACTIVITY_CONTENT;
			newTitle = R.string.newactivity_title;
			contentFragment = new DummyFragmentNA();
			break;
		case R.id.drawer_fallingdetection_item:
			currentContent = FALLING_DETECTION_CONTENT;
			newTitle = R.string.fallingdetection_title;
			contentFragment = new DummyFragment();
			break;
		case R.id.drawer_myactivities_item:
			currentContent = MY_ACTIVITIES_CONTENT;
			newTitle = R.string.myactivities_title;
			if(myActivitiesListFragment == null){
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				activitiesQuery = new ActivitiesQuery(actDataSource, null, null);
				ObserverCursorLoader loader = new ObserverCursorLoader(this, activitiesQuery, Arrays.asList(new Subject[]{filterSubject, actDataSource.getActivitiesSubject()}));
				myActivitiesListFragment = AutoUpdateListFragment.newInstance(getResources().getString(R.string.myactivitieslist_empty));
				myActivitiesListFragment.setListAdapter(new ActivitiesCursorAdapter(this));
				((AutoUpdateListFragment)myActivitiesListFragment).setLoader(loader);
			}
			contentFragment = myActivitiesListFragment;
			break;
		case R.id.drawer_myresults_item:
			currentContent = MY_RESULTS_CONTENT;
			newTitle = R.string.myresults_title;
			contentFragment = new DummyFragment();
			break;
		default: return;
		}
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame, contentFragment);
		fragmentTransaction.commit();
		setTitle(newTitle);
		
		drawerLayout.closeDrawer(GravityCompat.START);
	}
	
	
	
	
	
	
	
	
	
	public static class DummyFragment extends Fragment{		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
		}
	}
	
	public static class DummyFragmentNA extends Fragment implements OnClickListener{		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.test_newactivity, container, false);
			((android.widget.Button)view.findViewById(R.id.button1)).setOnClickListener(this);
			return view;
		}

		@Override
		public void onClick(View arg0) {
			android.widget.DatePicker date = ((android.widget.DatePicker)this.getView().findViewById(R.id.datePicker1));
			
			GregorianCalendar thisdate = new GregorianCalendar();
			
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			
			WalkActivity act = new WalkActivity(thisdate);
			
			ActivitiesDataSource source = ((WalkThroughApplication) getActivity().getApplicationContext()).getActivitiesDataSource();
			
			source.createNewActivity(act);
		}
	}

}
