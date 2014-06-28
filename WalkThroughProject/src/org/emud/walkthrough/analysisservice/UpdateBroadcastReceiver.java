package org.emud.walkthrough.analysisservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION_RESULT_INSERTED = "org.emud.intent.action.RESULT",
			ACTION_CONNECTING_RESULT = "org.emud.intent.action.CONNECTING_RESULT",
			ACTION_RECEIVER_DISCONNECTED = "org.emud.intent.action.RECEIVER_DISCONNECTED";
	
	private UpdateListener listener;
	private static IntentFilter intentFilter = new IntentFilter();
	
	static{
		intentFilter.addAction(ACTION_RECEIVER_DISCONNECTED);
		intentFilter.addAction(ACTION_CONNECTING_RESULT);
		intentFilter.addAction(ACTION_RESULT_INSERTED);
	}
	
	public static IntentFilter getIntentFilter(){
		return intentFilter;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setUpdateListener(UpdateListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(listener == null)
			return;
		
		String action = intent.getAction();
		
		if(action.equals(ACTION_RECEIVER_DISCONNECTED)){
			listener.onReceiverDisconnected(intent);
			return;
		}
		
		if(action.equals(ACTION_CONNECTING_RESULT)){
			listener.onConnectingResult(intent);
			return;
		}
		
		if(action.equals(ACTION_RESULT_INSERTED)){
			listener.onResultInserted(intent);
		}
	}
	
	public static interface UpdateListener{
		public void onResultInserted(Intent intent);
		public void onReceiverDisconnected(Intent intent);
		public void onConnectingResult(Intent intent);
	}
}
