package org.emud.walkthrough.analysis;


import org.emud.walkthrough.analysis.ServiceMessageHandler.OnMessageReceivedListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class FallingDetectionService extends Service implements OnMessageReceivedListener, OnFallDetectedListener {
	public static final String RECEIVER_TYPE_KEY = "receiverType", RESULTS_TYPES_KEY = "resultsTypes";
	public static final String LIST_SIZE_KEY = "listSize",  LIST_ITEM_KEY = "resultBundle_";
	public static final String BUNDLE_KEY = "bundle";
	
	private AnalysisStation station;
	private Messenger messenger;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){		
		station = AnalysisStationBuilder.buildFallingDetector(this, this);
		
		messenger = new Messenger(new ServiceMessageHandler(this));
		
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
	}

	@Override
	public void onPauseMessage(Message msg) {
	}

	@Override
	public void onResumeMessage(Message msg) {
	}

	@Override
	public void onStopMessage(Message msg) {
		station.stopAnalysis();
	}


	@Override
	public void fallDetected() {
		// TODO Auto-generated method stub
		
	}

}
