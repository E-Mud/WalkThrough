package org.emud.walkthrough.analysisservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
	private static IntentFilter intentFilter = new IntentFilter();
	private ScreenOnOffListener listener;
	
	static{
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		intentFilter.addAction("android.intent.action.SCREEN_OFF");
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(listener == null)
			return;
		
		String action = intent.getAction();
		
		if(action.equals("android.intent.action.SCREEN_ON")){
			listener.onScreenOn();
		}
		
		if(action.equals("android.intent.action.SCREEN_OFF")){
			listener.onScreenOff();
		}
	}
	
	public void setScreenListener(ScreenOnOffListener lstn){
		listener = lstn;
	}

	public static IntentFilter getIntentFilter(){
		return intentFilter;
	}
	
	public static interface ScreenOnOffListener{
		public void onScreenOn();
		public void onScreenOff();
	}
}
