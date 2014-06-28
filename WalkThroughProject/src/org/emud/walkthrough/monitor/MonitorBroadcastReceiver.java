package org.emud.walkthrough.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MonitorBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION_DATA_CHANGED = "org.emud.intent.action.DATA_CHANGED";
	public static final String DATA_KEY = "data", LOCATION_KEY = "location";
	
	private MonitorListener listener;
	private static IntentFilter intentFilter = new IntentFilter();
	
	static{
		intentFilter.addAction(ACTION_DATA_CHANGED);
	}
	
	public static IntentFilter getIntentFilter(){
		return intentFilter;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(MonitorListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(listener != null)
			listener.onDataChanged(intent);
	}
	
	public static interface MonitorListener{
		public void onDataChanged(Intent intent);
	}
}
