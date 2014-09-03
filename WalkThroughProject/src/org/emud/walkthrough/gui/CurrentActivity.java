package org.emud.walkthrough.gui;

import org.emud.walkthrough.ActivitiesDataSource;
import org.emud.walkthrough.R;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.WebClient;
import org.emud.walkthrough.WtFragmentActivity;
import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.analysisservice.UpdateBroadcastReceiver;
import org.emud.walkthrough.analysisservice.UpdateBroadcastReceiver.UpdateListener;
import org.emud.walkthrough.gui.fragment.HelpFragment;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.monitor.MonitorFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CurrentActivity extends WtFragmentActivity implements OnClickListener, UpdateListener {
	private ImageView pauseResumeIcon, stopIcon;
	private ToggleButton connectButton;
	private int serviceState, receiverType;
	private AnalysisService service;
	private ServiceConnection connection;
	private boolean bound;
	private UpdateBroadcastReceiver updateReceiver;
	private HelpFragment helpFragment;
	private MonitorFragment monitorFragment;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currentactivity);
		
		pauseResumeIcon = (ImageView) findViewById(R.id.iconPauseResume);
		stopIcon = (ImageView) findViewById(R.id.iconStop);
		connectButton = (ToggleButton) findViewById(R.id.right_toggleButton);
		
		//left.setOnClickListener(this);
		connectButton.setOnClickListener(this);
		pauseResumeIcon.setOnClickListener(this);
		stopIcon.setOnClickListener(this);
		
		bound = false;
		
		connection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder binder) {
	        	if(binder != null){
	        		service = ((AnalysisService.LocalBinder) binder).getService();
	        		bound = true;
	        		setServiceState(service.getState());
	        		updateUI();
	        	}
	        }
			public void onServiceDisconnected(ComponentName className) {
	            service = null;
	            bound = false;
	        }
	    };
	    
	    receiverType = getIntent().getIntExtra(AnalysisService.RECEIVER_TYPE_KEY, AnalysisService.SINGLE_ACCELEROMETER); 
	    
	    helpFragment = new HelpFragment();
	    helpFragment.setArguments(HelpFragment.buildArguments(receiverType, -10));
	    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	    fragmentTransaction.replace(R.id.center_content, helpFragment);
	    fragmentTransaction.commit();
	}


    private void updateUI() {
    	android.util.Log.i("CA", "serviceState: " + serviceState);
    	
    	if(serviceState == AnalysisService.SERVICE_RUNNING && monitorFragment == null
    			&& receiverType == AnalysisService.TWO_ACCELEROMETERS){
	    	monitorFragment = new MonitorFragment();
	    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	    	fragmentTransaction.replace(R.id.center_content, monitorFragment);
			fragmentTransaction.commit();    		
    	}
    	
		if(serviceState == AnalysisService.SERVICE_RUNNING || serviceState == AnalysisService.SERVICE_PAUSED)
			findViewById(R.id.iconStop_content).setVisibility(View.VISIBLE);
		
		if(serviceState == AnalysisService.SERVICE_UNSTARTED || serviceState == AnalysisService.SERVICE_CONNECTING){
			findViewById(R.id.lifeCycleButtons_content).setVisibility(View.INVISIBLE);
			connectButton.setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.lifeCycleButtons_content).setVisibility(View.VISIBLE);
			connectButton.setVisibility(View.INVISIBLE);
		}
	}
    
	@Override
	public void onClick(View view) {
		if(!bound)
			return;
		
		switch(view.getId()){
		case R.id.right_toggleButton:
			if(connectButton.isChecked()){
				service.connectSensor();
			}else{
				service.stopConnecting();
			}
			break;
		case R.id.iconPauseResume:
			switch(serviceState){
			case AnalysisService.SERVICE_PREPARED:
				setServiceState(service.startAnalysis());
				updateUI();
				break;
			case AnalysisService.SERVICE_RUNNING:
				setServiceState(service.pauseAnalysis());
				break;
			case AnalysisService.SERVICE_PAUSED:
				setServiceState(service.resumeAnalysis());
				break;
			default: return;
			}

			setPauseResumeIconSrc();
			break;
		case R.id.iconStop:
			if(serviceState == AnalysisService.SERVICE_PREPARED)
				return;

			setServiceState(service.stopAnalysis());
			break;			
		default:
			return;
		}
	}

	@Override
	public void onStart(){
		super.onStart();
		
		updateReceiver = new UpdateBroadcastReceiver();
		updateReceiver.setUpdateListener(this);
		registerReceiver(updateReceiver, UpdateBroadcastReceiver.getIntentFilter());
		
		bindService(new Intent(this, AnalysisService.class), connection, 0);
	}
	

	@Override
	public void onPause(){
		super.onPause();
		
		if(updateReceiver != null){
			unregisterReceiver(updateReceiver);
			updateReceiver = null;
		}
		
		((WalkThroughApplication) getApplicationContext()).setServiceState(serviceState);
		
		if(bound){
			unbindService(connection);
			bound = false;
		}
	}
	
	public void setServiceState(int servState){
		if(helpFragment != null)
			helpFragment.setServiceStatus(servState);
		
		serviceState = servState;
	}

	private void setPauseResumeIconSrc() {
		int src;
		if(serviceState == AnalysisService.SERVICE_RUNNING){
			src = R.drawable.ic_pause_icon;
		}else{
			src = R.drawable.ic_resume_icon;
		}
		
		pauseResumeIcon.setImageResource(src);
	}

	@Override
	public void onResultInserted(Intent intent) {
		boolean success = intent.getBooleanExtra(AnalysisService.SUCCESS_KEY, false);
		Toast toast;
		int text;

		if(success){
			text = R.string.currentactivity_insertion_success;
			long activity_id = intent.getLongExtra(AnalysisService.ACTIVITY_ID_KEY, -1);
			if(activity_id != -1){
				final WebClient webClient = getWebClient();
				final ActivitiesDataSource ds = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
				new AsyncTask<Long,Void,Void>(){
					@Override
					protected Void doInBackground(Long... params) {
						long activity_id = params[0];
						WalkActivity activity = ds.getActivity(activity_id);
						int webId = webClient.insertWalkActivity(activity);
						activity.setWebId(webId);
						if(webId != -1)
							ds.updateActivity(activity_id, activity);

						return null;
					}
				}.execute(new Long[]{activity_id});
			}
		}else{
			text = R.string.currentactivity_insertion_failed;
		}
		
		toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		toast.show();

        goBackToMainActivity();
	}
	
	private void goBackToMainActivity(){
        Intent stopServiceIntent, backToMainIntent;
        
		unbindService(connection);
        bound = false;
        
		stopServiceIntent = new Intent(this, AnalysisService.class);
		stopService(stopServiceIntent);

		backToMainIntent = new Intent(this, MainActivity.class);
		startActivity(backToMainIntent);
		finish();
	}

	@Override
	public void onReceiverDisconnected(Intent intent) {
		Toast toast;

		toast = Toast.makeText(getApplicationContext(), R.string.currentactivity_disconnected, Toast.LENGTH_LONG);
		toast.show();
		
		goBackToMainActivity();
	}

	@Override
	public void onConnectingResult(Intent intent) {
		boolean connected = intent.getBooleanExtra(AnalysisService.CONNECTED_KEY, false);
		android.util.Log.i("CA", "onConnectingResult " + connected);
		setServiceState(service.getState());
		
		connectButton.setChecked(false);
		
		updateUI();
	}
}
