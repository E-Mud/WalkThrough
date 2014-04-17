package org.emud.walkthrough;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.emud.content.DataSubject;
import org.emud.content.ObserverCursorLoader;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.adapter.ActivitiesCursorAdapter;
import org.emud.walkthrough.analysis.AnalysisService;
import org.emud.walkthrough.analysis.AnalysisStation;
import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysis.DataReceiverBuilder;
import org.emud.walkthrough.analysis.LinearAccelerometerReceiver;
import org.emud.walkthrough.analysis.WalkData;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.ActivitiesQuery;
import org.emud.walkthrough.dialogfragment.DatePickerDialogFragment;
import org.emud.walkthrough.dialogfragment.DatePickerDialogFragment.OnDatePickedListener;
import org.emud.walkthrough.fragment.AutoUpdateListFragment;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
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
	
	private DateFormat filterDateFormat; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int serviceState = ((WalkThroughApplication) getApplicationContext()).getServiceState();
		if(serviceState != WalkThroughApplication.SERVICE_NONE){
			Intent intent = new Intent(this, CurrentActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		setContentView(R.layout.activity_main);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
		
		if(savedInstanceState != null){
			long dateMillis;
			dateMillis = savedInstanceState.getLong("fromDate", -1);
			if(dateMillis == -1){
				fromDate = null;
			}else{
				fromDate = new GregorianCalendar();
				fromDate.setTimeInMillis(dateMillis);
			}
			dateMillis = savedInstanceState.getLong("toDate", -1);
			if(dateMillis == -1){
				toDate = null;
			}else{
				toDate = new GregorianCalendar();
				toDate.setTimeInMillis(dateMillis);
			}

	        currentContent = savedInstanceState.getInt("currentContent", NEW_ACTIVITY_CONTENT);
		}else{
			fromDate = null;
			toDate = null;
			currentContent = NEW_ACTIVITY_CONTENT;
		}
		
		
		fromText = (TextView) findViewById(R.id.drawer_fromDate);
		toText = (TextView) findViewById(R.id.drawer_toDate);

		filterDateFormat = new DateFormat();
		setFromDateText();
		setToDateText();
		
		filterSubject = new DataSubject();
		
		fromText.setOnClickListener(this);
		toText.setOnClickListener(this);
		
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
        
        
        android.util.Log.d("MAINACT", "onCreated");
	}

	

	@Override
	public void onStart(){
		super.onStart();
        android.util.Log.d("MAINACT", "onStart");
        
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
		
		if(fromDate != null)
			saveInstanceState.putLong("fromDate", fromDate.getTimeInMillis());
		if(toDate != null)
			saveInstanceState.putLong("toDate", toDate.getTimeInMillis());
		
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
		case R.id.action_filter:
			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void datePicked(int year, int month, int day) {
		if(dateDialogShowing == R.id.drawer_fromDate){
			if(fromDate == null)
				fromDate = (GregorianCalendar) GregorianCalendar.getInstance();
			fromDate.set(Calendar.YEAR, year);
			fromDate.set(Calendar.MONTH, month);
			fromDate.set(Calendar.DAY_OF_MONTH, day);
			setFromDateText();
			if(activitiesQuery != null)
				activitiesQuery.setFromDate(fromDate);
		}else{
			if(toDate == null)
				toDate = (GregorianCalendar) GregorianCalendar.getInstance();
			toDate.set(Calendar.YEAR, year);
			toDate.set(Calendar.MONTH, month);
			toDate.set(Calendar.DAY_OF_MONTH, day);
			setToDateText();
			if(activitiesQuery != null)
				activitiesQuery.setToDate(toDate);
		}
		
		filterSubject.notifyObservers();
		
	}
	
	private void setFromDateText() {
		if(fromDate != null)
			fromText.setText(filterDateFormat.format("EEE d/M/yyyy", fromDate));
	}
	
	private void setToDateText() {
		if(toDate != null)
			toText.setText(filterDateFormat.format("EEE d/M/yyyy", toDate));
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		
		switch(id){
		case R.id.drawer_fromDate:
		case R.id.drawer_toDate:
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
		
		android.util.Log.d("MAINACT", "" + (id == R.id.drawer_toDate));
		
		if(id == R.id.drawer_fromDate){
			date = fromDate;
		}else{
			date = toDate;
		}
		
		if(date == null)
			date = (GregorianCalendar) GregorianCalendar.getInstance();
		
		dateDialogShowing = id;

		dialogFragment = DatePickerDialogFragment.newInstance(date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
		dialogFragment.setOnDatePickedListener(this);
		dialogFragment.show(getSupportFragmentManager(), "datePickerDialog" + id);
	}
	
	private void onDrawerItemClick(int id){
		int newContent = -1;
				
		switch(id){
		case R.id.drawer_newactivity_item:
			newContent = NEW_ACTIVITY_CONTENT;
			break;
		case R.id.drawer_fallingdetection_item:
			newContent = FALLING_DETECTION_CONTENT;
			break;
		case R.id.drawer_myactivities_item:
			newContent = MY_ACTIVITIES_CONTENT;
			break;
		case R.id.drawer_myresults_item:
			newContent = MY_RESULTS_CONTENT;
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
			contentFragment = new DummyFragmentNA();
			break;
		case FALLING_DETECTION_CONTENT:
			newTitle = R.string.fallingdetection_title;
			contentFragment = new DummyFragmentCS();
			break;
		case MY_ACTIVITIES_CONTENT:
			newTitle = R.string.myactivities_title;
			if(myActivitiesListFragment == null){
				ActivitiesDataSource actDataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				activitiesQuery = new ActivitiesQuery(actDataSource, fromDate, toDate);
				ObserverCursorLoader loader = new ObserverCursorLoader(this, activitiesQuery, Arrays.asList(new Subject[]{filterSubject, actDataSource.getActivitiesSubject()}));
				myActivitiesListFragment = AutoUpdateListFragment.newInstance(getResources().getString(R.string.myactivitieslist_empty));
				myActivitiesListFragment.setListAdapter(new ActivitiesCursorAdapter(this));
				((AutoUpdateListFragment)myActivitiesListFragment).setLoader(loader);
			}
			contentFragment = myActivitiesListFragment;
			break;
		case MY_RESULTS_CONTENT:
			newTitle = R.string.myresults_title;
			contentFragment = new DummyFragment();
			break;
		default: return;
		}
		
		currentContent = newContent;
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame, contentFragment);
		fragmentTransaction.commit();
		setTitle(newTitle);
		
	}
	
	
	
	
	
	public static class DummyFragment extends Fragment{		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
		}
	}
	
	public static class DummyFragmentNA extends Fragment implements OnClickListener, WalkDataReceiver.OnDataReceivedListener{
		private TextView textX, textY, textZ;
		private WalkDataReceiver receiver;
		private boolean started = false;
		private AnalysisStation station;
		private Analyst analyst;
		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.test_newactivity, container, false);
			((android.widget.Button)view.findViewById(R.id.button1)).setOnClickListener(this);
			((android.widget.Button)view.findViewById(R.id.button2)).setOnClickListener(this);
			textX = (TextView) view.findViewById(R.id.textViewX);
			textY = (TextView) view.findViewById(R.id.textViewY);
			textZ = (TextView) view.findViewById(R.id.textViewZ);
			return view;
		}

		@Override
		public void onClick(View arg0) {
			
				/*if(started){
					station.pauseAnalysis();
					Result result = station.collectResults().get(0);
					Double max = (Double) result.get();
					textX.setText(max.toString());

				}else{
					
					if(station == null){
						HashSet<Result.ResultType> resultTypes = new HashSet<Result.ResultType>();
						resultTypes.add(Result.ResultType.MAX_MOVE);
						station = AnalysisStationBuilder.buildStation(getActivity(), DataReceiverBuilder.ReceiverType.SINGLE_ACCELEROMETER, resultTypes);
						station.startAnalysis();
					}else{
						station.resumeAnalysis();
					}
				}
				started = !started;*/
			
			if(arg0.getId() == R.id.button2){
				Result result = analyst.getResult();
				Double max = (Double) result.get();
				textX.setText(max.toString());
			}
			
			
			if(started){
				receiver.stopReceiving();
			}else{
				analyst = new MaxMoveAnalyst();
				receiver = new LinearAccelerometerReceiver(getActivity());
				receiver.addOnDataReceveidListener(this);
				
				receiver.startReceiving();
				
			}
			started = !started;
			/*
			android.widget.DatePicker date = ((android.widget.DatePicker)this.getView().findViewById(R.id.datePicker1));
			
			GregorianCalendar thisdate = new GregorianCalendar();
			
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			thisdate.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			
			WalkActivity act = new WalkActivity(thisdate);
			
			ActivitiesDataSource source = ((WalkThroughApplication) getActivity().getApplicationContext()).getActivitiesDataSource();
			
			source.createNewActivity(act);*/
		}

		@Override
		public void onDataReceveid(WalkData walkData) {
			analyst.analyzeNewData(walkData);
			double[] data = walkData.getData();
			
			textX.setText("" + data[0]);
			textY.setText("" + data[1]);
			textZ.setText("" + data[2]);
		}
	}
	
	public static class DummyFragmentCS extends Fragment implements OnClickListener{
		private boolean bound = false;
		private Messenger service = null;
		private boolean started = false;
		
		private ServiceConnection mConnection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder binder) {
	            // This is called when the connection with the service has been
	            // established, giving us the object we can use to
	            // interact with the service.  We are communicating with the
	            // service using a Messenger, so here we get a client-side
	            // representation of that from the raw IBinder object.
	            service = new Messenger(binder);
	            bound = true;
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            // This is called when the connection with the service has been
	            // unexpectedly disconnected -- that is, its process crashed.
	            service = null;
	            bound = false;
	        }
	    };
		
		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.test_service, container, false);
			((android.widget.Button)view.findViewById(R.id.buttonService)).setOnClickListener(this);
			//((android.widget.Button)view.findViewById(R.id.buttonBind)).setOnClickListener(this);
			//((android.widget.Button)view.findViewById(R.id.buttonStart)).setOnClickListener(this);
			//((android.widget.Button)view.findViewById(R.id.buttonPause)).setOnClickListener(this);
			//((android.widget.Button)view.findViewById(R.id.buttonResume)).setOnClickListener(this);
			//((android.widget.Button)view.findViewById(R.id.buttonStop)).setOnClickListener(this);
			
			return view;
		}

		@Override
		public void onClick(View view) {
			Activity activity = getActivity();
			int receiverType = WalkDataReceiver.SINGLE_ACCELEROMETER;
			int[] resultsTypes = new int[]{Result.RT_MAX_MOVE};
			
			Intent intentService = new Intent(activity, AnalysisService.class);
			intentService.putExtra(AnalysisService.RECEIVER_TYPE_KEY, receiverType);
			intentService.putExtra(AnalysisService.RESULTS_TYPES_KEY, resultsTypes);
			activity.startService(intentService);
			
			((WalkThroughApplication) activity.getApplicationContext()).setServiceState(WalkThroughApplication.SERVICE_PREPARED);
			
			Intent intentCurrentActivity = new Intent(activity, CurrentActivity.class);
			startActivity(intentCurrentActivity);
			/*
			int what = 0;
			switch(view.getId()){
			case R.id.buttonService:
				if(started){
		            getActivity().unbindService(mConnection);
		            bound = false;
					Intent intent = new Intent(getActivity(), AnalysisService.class);
					getActivity().stopService(intent);
					
				}else{
					Intent intent = new Intent(getActivity(), AnalysisService.class);
					intent.putExtra(AnalysisService.RECEIVER_TYPE_KEY, WalkDataReceiver.SINGLE_ACCELEROMETER);
					intent.putExtra(AnalysisService.RESULTS_TYPES_KEY, new int[]{Result.RT_MAX_MOVE});
					
					getActivity().startService(intent);
				}
				started = !started;
				return;
			case R.id.buttonBind:
				if(!bound){
					getActivity().bindService(new Intent(getActivity(), AnalysisService.class), mConnection, 0);
				}else{
		            getActivity().unbindService(mConnection);
		            bound = false;
				}
				return;
			case R.id.buttonStart:
				what = AnalysisService.MSG_START;
				break;
			case R.id.buttonPause:
				what = AnalysisService.MSG_PAUSE;
				break;
			case R.id.buttonResume:
				what = AnalysisService.MSG_RESUME;
				break;
			case R.id.buttonStop:
				what = AnalysisService.MSG_STOP;
				break;
			}
			
			Message msg = Message.obtain(null, what, 0, 0);
	        try {
	            service.send(msg);
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }*/
		}
		
		@Override
		public void onDestroy(){
			if(bound)
				getActivity().unbindService(mConnection);
			super.onDestroy();
		}
	}

}
