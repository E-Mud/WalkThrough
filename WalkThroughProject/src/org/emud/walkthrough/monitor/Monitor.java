package org.emud.walkthrough.monitor;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.WalkDataReceiver.OnDataReceivedListener;

import android.content.Context;
import android.content.Intent;

public class Monitor implements OnDataReceivedListener {
	private Context context;
	
	public Monitor(Context cont){
		context = cont;
	}

	@Override
	public void onDataReceveid(AccelerometerData accelerometerData) {
		Intent intent = new Intent(MonitorBroadcastReceiver.ACTION_DATA_CHANGED);
		
		intent.putExtra(MonitorBroadcastReceiver.DATA_KEY, accelerometerData.getData());
		intent.putExtra(MonitorBroadcastReceiver.LOCATION_KEY, accelerometerData.getLocation());
		
		context.sendBroadcast(intent);
	}
}
