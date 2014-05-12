package org.emud.walkthrough.service;

import java.util.HashSet;
import java.util.List;

import org.emud.walkthrough.ResultFactory;
import org.emud.walkthrough.ResultToolsProvider;
import org.emud.walkthrough.analysis.AnalysisStation;
import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.ServiceMessageHandler;
import org.emud.walkthrough.analysis.ServiceMessageHandler.OnMessageReceivedListener;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.service.ScreenBroadcastReceiver.ScreenOnOffListener;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class AnalysisService extends Service implements OnMessageReceivedListener, ScreenOnOffListener{
	public static final String RECEIVER_TYPE_KEY = "receiverType", RESULTS_TYPES_KEY = "resultsTypes", SCREEN_KEY = "screenPref";
	public static final String LIST_SIZE_KEY = "listSize",  LIST_ITEM_KEY = "resultBundle_";
	public static final String BUNDLE_KEY = "bundle";
	
	public static final int
		SERVICE_PREPARED = 0,
		SERVICE_RUNNING = 1,
		SERVICE_PAUSED = 2,
		SERVICE_STOPPED = 3,
		SERVICE_NONE = 4;
	
	private int currentState;
	private AnalysisStation station;
	private Messenger messenger;
	private boolean receiverRegistered;
	private ScreenBroadcastReceiver receiver;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		int receiverType = intent.getIntExtra(RECEIVER_TYPE_KEY, -2);
		int[] resultsTypes = intent.getIntArrayExtra(RESULTS_TYPES_KEY);
		HashSet<Integer> setResultsTypes = new HashSet<Integer>();
		int n = resultsTypes.length;
		
		for(int i=0; i<n; i++)
			setResultsTypes.add(Integer.valueOf(resultsTypes[i]));
		
		station = AnalysisStationBuilder.buildStation(this, receiverType, setResultsTypes);
		
		messenger = new Messenger(new ServiceMessageHandler(this));
		
		if(intent.getBooleanExtra(SCREEN_KEY, false)){
			receiver = new ScreenBroadcastReceiver();
			receiver.setScreenListener(this);
			registerReceiver(receiver, ScreenBroadcastReceiver.getIntentFilter());
			receiverRegistered = true;
		}else{
			receiverRegistered = false;
		}
		
		currentState = SERVICE_PREPARED;
		
		return START_STICKY;
	}
	

    @Override
    public IBinder onBind(Intent intent) {
        if(messenger != null){
            Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        	return messenger.getBinder();
        }else{
            Toast.makeText(getApplicationContext(), "binding not allowed", Toast.LENGTH_SHORT).show();
        	return null;
        }
    }


	@Override
	public void onStartMessage(Message msg) {
		station.startAnalysis();
		currentState = SERVICE_RUNNING;
	}


	@Override
	public void onPauseMessage(Message msg) {
		station.pauseAnalysis();
		currentState = SERVICE_PAUSED;
	}


	@Override
	public void onResumeMessage(Message msg) {
		station.resumeAnalysis();
		currentState = SERVICE_RUNNING;
	}


	@Override
	public void onStopMessage(Message msg) {
		station.stopAnalysis();
		currentState = SERVICE_STOPPED;
		if(receiverRegistered){
			unregisterReceiver(receiver);
			receiverRegistered = false;
		}
        sendResultResponse(station.collectResults(), msg.replyTo);
	}
	
	private void sendResultResponse(List<Result> list, Messenger replyTo) {
		Message msg = Message.obtain(null, ServiceMessageHandler.MSG_STOP, 0, 0);
		int size = list.size();
		Bundle resultListBundle = new Bundle(), resultBundle;
		ResultToolsProvider provider = new ResultToolsProvider();
		
		resultListBundle.putInt(LIST_SIZE_KEY, size);

        android.util.Log.d("AS", "results size: " + size);
        
		for(int i=0; i<size; i++){
			Result result = list.get(i);
			ResultFactory factory = provider.getResultFactory(result.getType());
			resultBundle = factory.buildBundleFromResult(result);
			resultListBundle.putBundle(LIST_ITEM_KEY + i, resultBundle);
		}
		
		msg.setData(resultListBundle);
		
		try {
			replyTo.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onStateMessage(Message msg) {
		Message msgResponse = Message.obtain(null, ServiceMessageHandler.MSG_STATE, 0, 0);
		Bundle bundle = new Bundle();
		
		bundle.putInt(ServiceMessageHandler.STATE_KEY, currentState);
		msgResponse.setData(bundle);
		
		try {
			msg.replyTo.send(msgResponse);
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}


	@Override
	public void onScreenOn() {
		if(currentState == SERVICE_RUNNING){
			station.pauseAnalysis();
			currentState = SERVICE_PAUSED;
		}
	}

	@Override
	public void onScreenOff() {
		if(currentState == SERVICE_PREPARED){
			station.startAnalysis();
			currentState = SERVICE_RUNNING;
		}else{
			if(currentState == SERVICE_PAUSED){
				station.resumeAnalysis();
				currentState = SERVICE_RUNNING;
			}
		}
	}


	/*private void logState() {
		android.util.Log.d("ServiceAnalysis", "state: " + currentState);
	}*/

}
