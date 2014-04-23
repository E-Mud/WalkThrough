package org.emud.walkthrough.analysis;

import java.util.HashSet;
import java.util.List;

import org.emud.walkthrough.analysis.ServiceMessageHandler.OnMessageReceivedListener;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.ResultBuilder;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class AnalysisService extends Service implements OnMessageReceivedListener{
	public static final String RECEIVER_TYPE_KEY = "receiverType", RESULTS_TYPES_KEY = "resultsTypes";
	public static final String LIST_SIZE_KEY = "listSize",  LIST_ITEM_KEY = "resultBundle_";
	public static final String BUNDLE_KEY = "bundle";
	
	private AnalysisStation station;
	private Messenger messenger;
	
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
		station.pauseAnalysis();
	}


	@Override
	public void onResumeMessage(Message msg) {
		station.resumeAnalysis();
	}


	@Override
	public void onStopMessage(Message msg) {
		station.stopAnalysis();
        sendResultResponse(station.collectResults(), msg.replyTo);
	}
	
	private void sendResultResponse(List<Result> list, Messenger replyTo) {
		Message msg = Message.obtain(null, ServiceMessageHandler.MSG_STOP, 0, 0);
		int size = list.size();
		Bundle resultListBundle = new Bundle(), resultBundle;
		
		resultListBundle.putInt(LIST_SIZE_KEY, size);
		
		for(int i=0; i<size; i++){
			resultBundle = ResultBuilder.buildBundleFromResult(list.get(i));
			resultListBundle.putBundle(LIST_ITEM_KEY + i, resultBundle);
		}
		
		msg.setData(resultListBundle);
		
		try {
			replyTo.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
