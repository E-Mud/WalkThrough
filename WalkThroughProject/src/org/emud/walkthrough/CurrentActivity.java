package org.emud.walkthrough;

import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.analysisservice.UpdateBroadcastReceiver;
import org.emud.walkthrough.analysisservice.UpdateBroadcastReceiver.UpdateListener;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.monitor.MonitorFragment;
import org.emud.walkthrough.webclient.WebClient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CurrentActivity extends WtFragmentActivity implements OnClickListener, UpdateListener {
	private ImageView pauseResumeIcon, stopIcon;
	private ToggleButton left, right;
	private int serviceState;
	private AnalysisService service;
	private ServiceConnection connection;
	private boolean bound;
	private UpdateBroadcastReceiver updateReceiver;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currentactivity);
		
		pauseResumeIcon = (ImageView) findViewById(R.id.iconPauseResume);
		stopIcon = (ImageView) findViewById(R.id.iconStop);
		left = (ToggleButton) findViewById(R.id.left_toggleButton);
		right = (ToggleButton) findViewById(R.id.right_toggleButton);
		
		//left.setOnClickListener(this);
		right.setOnClickListener(this);
		pauseResumeIcon.setOnClickListener(this);
		stopIcon.setOnClickListener(this);
		
		bound = false;
		
		connection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder binder) {
	        	if(binder != null){
	        		service = ((AnalysisService.LocalBinder) binder).getService();
	        		bound = true;
	        		serviceState = service.getState();
	        		updateUI();
	        	}
	        }
			public void onServiceDisconnected(ComponentName className) {
	            service = null;
	            bound = false;
	        }
	    };
	    
	    int receiverType = getIntent().getIntExtra(AnalysisService.RECEIVER_TYPE_KEY, WalkDataReceiver.SINGLE_ACCELEROMETER); 
	    if(receiverType == WalkDataReceiver.TWO_ACCELEROMETERS){
	    	Fragment contentFragment = new MonitorFragment();
	    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	    	fragmentTransaction.replace(R.id.center_content, contentFragment);
			fragmentTransaction.commit();
	    }
	}


    private void updateUI() {
    	android.util.Log.i("CA", "serviceState: " + serviceState);
		if(serviceState == AnalysisService.SERVICE_RUNNING || serviceState == AnalysisService.SERVICE_PAUSED)
			findViewById(R.id.iconStop_content).setVisibility(View.VISIBLE);
		
		if(serviceState == AnalysisService.SERVICE_UNSTARTED || serviceState == AnalysisService.SERVICE_CONNECTING){
			findViewById(R.id.lifeCycleButtons_content).setVisibility(View.INVISIBLE);
			right.setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.lifeCycleButtons_content).setVisibility(View.VISIBLE);
			right.setVisibility(View.INVISIBLE);
		}
	}
    
	@Override
	public void onClick(View view) {
		if(!bound)
			return;
		
		switch(view.getId()){
		case R.id.right_toggleButton:
			if(right.isChecked()){
				service.connectSensor();
			}else{
				service.stopConnecting();
			}
			break;
		case R.id.iconPauseResume:
			switch(serviceState){
			case AnalysisService.SERVICE_PREPARED:
				serviceState = service.startAnalysis();
				if(serviceState == AnalysisService.SERVICE_RUNNING)
					findViewById(R.id.iconStop_content).setVisibility(View.VISIBLE);
				break;
			case AnalysisService.SERVICE_RUNNING:
				serviceState = service.pauseAnalysis();
				break;
			case AnalysisService.SERVICE_PAUSED:
				serviceState = service.resumeAnalysis();
				break;
			default: return;
			}

			setPauseResumeIconSrc();
			break;
		case R.id.iconStop:
			if(serviceState == AnalysisService.SERVICE_PREPARED)
				return;

			serviceState = service.stopAnalysis();
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
		
		serviceState = service.getState();
		
		right.setChecked(false);
		
		updateUI();
	}
}
