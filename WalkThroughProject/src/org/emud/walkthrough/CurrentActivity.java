package org.emud.walkthrough;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.analysis.AnalysisService;
import org.emud.walkthrough.analysis.ServiceMessageHandler;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;

public class CurrentActivity extends Activity implements OnClickListener {
	public static final String INTENT_ACTION = "org.emud.walkthrough.saveresult";
	private static final String RESULT_TYPE_KEY = "resultType";
	private ImageView pauseResumeIcon, stopIcon;
	private int serviceState;
	private Messenger service;
	private ServiceConnection connection;
	private Messenger responseMessenger;
	private boolean bound;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currentactivity);
		
		pauseResumeIcon = (ImageView) findViewById(R.id.iconPauseResume);
		stopIcon = (ImageView) findViewById(R.id.iconStop);
		
		pauseResumeIcon.setOnClickListener(this);
		stopIcon.setOnClickListener(this);
		
		bound = false;
		
		connection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder binder) {
	            service = new Messenger(binder);
	            bound = true;
	        }
	        public void onServiceDisconnected(ComponentName className) {
	            service = null;
	            bound = false;
	        }
	    };
	}

	@Override
	public void onClick(View view) {
		if(!bound)
			return;
		
		int what;
		
		if(view.getId() == R.id.iconPauseResume){
			switch(serviceState){
			case WalkThroughApplication.SERVICE_PREPARED:
				serviceState = WalkThroughApplication.SERVICE_RUNNING;
				what = ServiceMessageHandler.MSG_START;
				break;
			case WalkThroughApplication.SERVICE_RUNNING:
				serviceState = WalkThroughApplication.SERVICE_PAUSED;
				what = ServiceMessageHandler.MSG_PAUSE;
				break;
			case WalkThroughApplication.SERVICE_PAUSED:
				serviceState = WalkThroughApplication.SERVICE_RUNNING;
				what = ServiceMessageHandler.MSG_RESUME;
				break;
			default: return;
			}
			
			setPauseResumeIconSrc();
		}else{
			if(serviceState == WalkThroughApplication.SERVICE_PREPARED)
				return;

			serviceState = WalkThroughApplication.SERVICE_STOPPED;
			what = ServiceMessageHandler.MSG_STOP;
		}
		
		sendMessageToService(what);
	}

	@Override
	public void onStart(){
		super.onStart();
		
		bindService(new Intent(this, AnalysisService.class), connection, 0);
		
		serviceState = ((WalkThroughApplication) getApplicationContext()).getServiceState();
		
		setPauseResumeIconSrc();
	}
	

	@Override
	public void onPause(){
		super.onPause();
		
		((WalkThroughApplication) getApplicationContext()).setServiceState(serviceState);
		
		if(bound){
			unbindService(connection);
			bound = false;
		}
	}

	private void setPauseResumeIconSrc() {
		int src;
		if(serviceState == WalkThroughApplication.SERVICE_RUNNING){
			src = R.drawable.ic_pause_icon;
		}else{
			src = R.drawable.ic_resume_icon;
		}
		
		pauseResumeIcon.setImageResource(src);
	}
	

	private void sendMessageToService(int what) {
		Message msg = Message.obtain(null, what, 0, 0);
		
		if(what == ServiceMessageHandler.MSG_STOP){
			responseMessenger = new Messenger(new ResponseHandler(this));
			msg.replyTo = responseMessenger;
			android.util.Log.d("ACTIVITY", "SENDING STOP");
		}
		
		try {
			service.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void onServiceStopped(Bundle msgData){        
        WalkActivity activity;
        ArrayList<Result> results = new ArrayList<Result>();
        int size = msgData.getInt(AnalysisService.LIST_SIZE_KEY);
        Intent stopServiceIntent, backToMainIntent;
        WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
        ActivitiesDataSource dataSource = app.getActivitiesDataSource();
        
        for(int i=0; i<size; i++){
        	Bundle bundle = msgData.getBundle(AnalysisService.LIST_ITEM_KEY+i);
    		int type = bundle.getInt(RESULT_TYPE_KEY, -1);
    		ResultFactory factory = app.getResultFactory(type);
        	results.add(factory.buildResultFromBundle(bundle));
        }

        activity = new WalkActivity((GregorianCalendar) GregorianCalendar.getInstance(), results);
        dataSource.createNewActivity(activity);
        
        unbindService(connection);
        bound = false;

        stopServiceIntent = new Intent(this, AnalysisService.class);
        stopService(stopServiceIntent);
        
        serviceState = WalkThroughApplication.SERVICE_NONE;
        app.setServiceState(serviceState);
        
        backToMainIntent = new Intent(this, MainActivity.class);
        startActivity(backToMainIntent);
        finish();
	}
	
	public static class ResponseHandler extends Handler {
		private CurrentActivity currentActivity;
		
		public ResponseHandler(CurrentActivity ca){
			currentActivity = ca;
		}
		
        @Override
        public void handleMessage(Message msg) {
    		android.util.Log.e("ACTIVITY","msg " + msg.what);
            if(msg.what == ServiceMessageHandler.MSG_STOP){
            	currentActivity.onServiceStopped(msg.getData());
            }
        }
    }
}
