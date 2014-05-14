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
import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.resulttype.ResultFactory;
import org.emud.walkthrough.resulttype.ResultType;

public class CurrentActivity extends Activity implements OnClickListener {
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
	        	if(binder != null){
	        		service = new Messenger(binder);
	        		bound = true;
	        		sendMessageToService(ServiceMessageHandler.MSG_STATE);
	        	}
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
			case AnalysisService.SERVICE_PREPARED:
				serviceState = AnalysisService.SERVICE_RUNNING;
				what = ServiceMessageHandler.MSG_START;
				findViewById(R.id.iconStop_content).setVisibility(View.VISIBLE);
				break;
			case AnalysisService.SERVICE_RUNNING:
				serviceState = AnalysisService.SERVICE_PAUSED;
				what = ServiceMessageHandler.MSG_PAUSE;
				break;
			case AnalysisService.SERVICE_PAUSED:
				serviceState = AnalysisService.SERVICE_RUNNING;
				what = ServiceMessageHandler.MSG_RESUME;
				break;
			default: return;
			}
			
			setPauseResumeIconSrc();
		}else{
			if(serviceState == AnalysisService.SERVICE_PREPARED)
				return;

			serviceState = AnalysisService.SERVICE_STOPPED;
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
		if(serviceState == AnalysisService.SERVICE_RUNNING){
			src = R.drawable.ic_pause_icon;
		}else{
			src = R.drawable.ic_resume_icon;
		}
		
		pauseResumeIcon.setImageResource(src);
	}
	

	private void sendMessageToService(int what) {
		Message msg = Message.obtain(null, what, 0, 0);
		
		if(what == ServiceMessageHandler.MSG_STOP || what == ServiceMessageHandler.MSG_STATE){
			responseMessenger = new Messenger(new ResponseHandler(this));
			msg.replyTo = responseMessenger;
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
    		ResultType resultType = ResultType.valueOf(bundle.getInt(ResultFactory.RESULT_TYPE_KEY, -1));
    		ResultFactory factory = resultType.getFactory();
        	results.add(factory.buildResultFromBundle(bundle));
        }
        
        android.util.Log.d("CA", "results size: " + size);

        activity = new WalkActivity((GregorianCalendar) GregorianCalendar.getInstance(), results);
        dataSource.createNewActivity(activity);
        
        unbindService(connection);
        bound = false;

        stopServiceIntent = new Intent(this, AnalysisService.class);
        stopService(stopServiceIntent);
        
        serviceState = AnalysisService.SERVICE_NONE;
        app.setServiceState(serviceState);
        
        backToMainIntent = new Intent(this, MainActivity.class);
        startActivity(backToMainIntent);
        finish();
	}
	

	private void onStateAnswer(Bundle data) {
		serviceState = data.getInt(ServiceMessageHandler.STATE_KEY);
		setPauseResumeIconSrc();
		if(serviceState != AnalysisService.SERVICE_PREPARED)
			findViewById(R.id.iconStop_content).setVisibility(View.VISIBLE);
	}
	
	public static class ResponseHandler extends Handler {
		private CurrentActivity currentActivity;
		
		public ResponseHandler(CurrentActivity ca){
			currentActivity = ca;
		}
		
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == ServiceMessageHandler.MSG_STOP){
            	currentActivity.onServiceStopped(msg.getData());
            }else{
            	currentActivity.onStateAnswer(msg.getData());
            }
        }
    }

}
