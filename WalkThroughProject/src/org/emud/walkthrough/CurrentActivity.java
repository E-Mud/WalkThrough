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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.analysis.AnalysisService;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.ResultBuilder;
import org.emud.walkthrough.model.WalkActivity;

public class CurrentActivity extends Activity implements OnClickListener {
	public static final String INTENT_ACTION = "org.emud.walkthrough.saveresult";
	private ImageView pauseResumeIcon, stopIcon;
	private int serviceState;
	private Messenger service;
	private ServiceConnection connection;
	private Messenger responseMessenger;
	private BroadcastReceiver receiver;
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
	    
	    receiver = new MyReceiver(this);
	}

	@Override
	public void onClick(View view) {
		int what;
		
		if(view.getId() == R.id.iconPauseResume){
			switch(serviceState){
			case WalkThroughApplication.SERVICE_PREPARED:
				serviceState = WalkThroughApplication.SERVICE_RUNNING;
				what = AnalysisService.MSG_START;
				break;
			case WalkThroughApplication.SERVICE_RUNNING:
				serviceState = WalkThroughApplication.SERVICE_PAUSED;
				what = AnalysisService.MSG_PAUSE;
				break;
			case WalkThroughApplication.SERVICE_PAUSED:
				serviceState = WalkThroughApplication.SERVICE_RUNNING;
				what = AnalysisService.MSG_RESUME;
				break;
			default: return;
			}
			
			setPauseResumeIconSrc();
		}else{
			if(serviceState == WalkThroughApplication.SERVICE_PREPARED)
				return;

			serviceState = WalkThroughApplication.SERVICE_STOPPED;
			what = AnalysisService.MSG_STOP;
		}
		
		sendMessageToService(what);
	}

	@Override
	public void onStart(){
		super.onStart();
		
		Intent stickyIntent = registerReceiver(receiver, new IntentFilter(INTENT_ACTION));
		if(stickyIntent != null)
			onServiceStopped(stickyIntent.getBundleExtra(AnalysisService.BUNDLE_KEY));
		
		bindService(new Intent(this, AnalysisService.class), connection, 0);
		
		serviceState = ((WalkThroughApplication) getApplicationContext()).getServiceState();
		
		setPauseResumeIconSrc();
	}
	

	@Override
	public void onPause(){
		super.onPause();
		
		unregisterReceiver(receiver);
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
		
		if(what == AnalysisService.MSG_STOP){
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
		//TODO
		android.util.Log.d("ACTIVITY", "ON STOP");
		Result result = ResultBuilder.buildResultFromBundle(msgData.getBundle(AnalysisService.LIST_ITEM_KEY+0));
        double maxvalue = ((Double)result.get()).doubleValue();
        Toast.makeText(this, "Result " + maxvalue, Toast.LENGTH_SHORT).show();
        
        WalkActivity activity;
        ArrayList<Result> results = new ArrayList<Result>();
        int size = msgData.getInt(AnalysisService.LIST_SIZE_KEY);
        Intent stopServiceIntent, backToMainIntent;
        WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
        ActivitiesDataSource dataSource = app.getActivitiesDataSource();
        
        for(int i=0; i<size; i++)
        	results.add(ResultBuilder.buildResultFromBundle(msgData.getBundle(AnalysisService.LIST_ITEM_KEY+i)));

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
            if(msg.what == AnalysisService.MSG_STOP){
            	currentActivity.onServiceStopped(msg.getData());
            }
        }
    }
	
	
	public static class MyReceiver extends BroadcastReceiver{
		private CurrentActivity currentActivity;
		
		public MyReceiver(CurrentActivity ca){
			currentActivity = ca;
		}

		@Override
		public void onReceive(Context arg0, Intent intent) {
			android.util.Log.d("ACTIVITY", "Intent received");
			currentActivity.onServiceStopped(intent.getBundleExtra(AnalysisService.BUNDLE_KEY));
		}
		
	}
}
