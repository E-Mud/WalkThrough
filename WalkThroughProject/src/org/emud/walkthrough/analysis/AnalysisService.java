package org.emud.walkthrough.analysis;

import java.util.HashSet;
import java.util.List;

import org.emud.walkthrough.CurrentActivity;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.ResultBuilder;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class AnalysisService extends Service {
	public static final int MSG_START = 0, MSG_STOP = 1, MSG_PAUSE = 2, MSG_RESUME = 3;
	public static final String RECEIVER_TYPE_KEY = "receiverType", RESULTS_TYPES_KEY = "resultsTypes";
	public static final String LIST_SIZE_KEY = "listSize",  LIST_ITEM_KEY = "resultBundle_";
	public static final String BUNDLE_KEY = "bundle";
	
	private AnalysisStation station;
	private Messenger messenger;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(getApplicationContext(), "ON START", Toast.LENGTH_SHORT).show();
		int receiverType = intent.getIntExtra(RECEIVER_TYPE_KEY, -2);
		int[] resultsTypes = intent.getIntArrayExtra(RESULTS_TYPES_KEY);
		HashSet<Integer> setResultsTypes = new HashSet<Integer>();
		int n = resultsTypes.length;
		
		for(int i=0; i<n; i++)
			setResultsTypes.add(Integer.valueOf(resultsTypes[i]));
		
		station = AnalysisStationBuilder.buildStation(this, receiverType, setResultsTypes);
		
		messenger = new Messenger(new IncomingHandler(this));
		
		return START_STICKY;
	}

	public static class IncomingHandler extends Handler {
		private AnalysisService service;
		
		public IncomingHandler(AnalysisService service){
			this.service = service;
		}
		
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    Toast.makeText(service.getApplicationContext(), "START", Toast.LENGTH_SHORT).show();
                    this.service.station.startAnalysis();
                    break;
                case MSG_STOP:
                    this.service.station.stopAnalysis();
                    //double maxvalue = ((Double)this.service.station.collectResults().get(0).get()).doubleValue();
                    //Toast.makeText(service.getApplicationContext(), "STOP " + maxvalue, Toast.LENGTH_SHORT).show();
            		android.util.Log.e("SERVICE","msg_stop rcv");
                    service.sendResultResponse(this.service.station.collectResults(), msg.replyTo);
                    break;
                case MSG_PAUSE:
                    Toast.makeText(service.getApplicationContext(), "PAUSE", Toast.LENGTH_SHORT).show();
                    this.service.station.pauseAnalysis();
                    break;
                case MSG_RESUME:
                    Toast.makeText(service.getApplicationContext(), "RESUME", Toast.LENGTH_SHORT).show();
                    this.service.station.resumeAnalysis();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	private void sendResultResponse(List<Result> list, Messenger replyTo) {
		Message msg = Message.obtain(null, MSG_STOP, 0, 0);
		int size = list.size();
		Bundle resultListBundle = new Bundle(), resultBundle;
		
		resultListBundle.putInt(LIST_SIZE_KEY, size);
		android.util.Log.e("SERVICE","sendingResponse " + (replyTo==null));
		
		for(int i=0; i<size; i++){
			resultBundle = ResultBuilder.buildBundleFromResult(list.get(i));
			android.util.Log.e("SERVICE","building bundle " + i + " type " + list.get(i).getType());
			resultListBundle.putBundle(LIST_ITEM_KEY + i, resultBundle);
		}
		
		msg.setData(resultListBundle);
		
		try {
			android.util.Log.e("SERVICE","sending msg");
			replyTo.send(msg);
		} catch (RemoteException e) {
			android.util.Log.e("SERVICE", "error sending");
			e.printStackTrace();
		}
		android.util.Log.e("SERVICE","msg sent");
		
/*
		android.util.Log.e("SERVICE","building intent");
		Intent intent = new Intent();
		intent.setAction(CurrentActivity.INTENT_ACTION);
		intent.putExtra(BUNDLE_KEY, resultListBundle);
		sendBroadcast(intent);
		android.util.Log.e("SERVICE","intent sent");*/
		
		/*Result result = ResultBuilder.buildResultFromBundle(firstBundle);
		double maxvalue = ((Double)result.get()).doubleValue();
        Toast.makeText(this, "Result " + maxvalue, Toast.LENGTH_SHORT).show();*/
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

}
