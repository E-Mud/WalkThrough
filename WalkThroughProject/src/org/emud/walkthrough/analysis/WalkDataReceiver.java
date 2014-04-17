package org.emud.walkthrough.analysis;

import java.util.ArrayList;

import android.content.Context;

public abstract class WalkDataReceiver {
	protected ArrayList<OnDataReceivedListener> listeners;
	private Context context;
	
	public WalkDataReceiver(Context context){
		this.context = context;
		listeners = new ArrayList<OnDataReceivedListener>();
	}

	public void addOnDataReceveidListener(OnDataReceivedListener listener) {
		listeners.add(listener);
	}

	public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
		listeners.remove(listener);
	}
	
	public Context getContext(){
		return context;
	}
	
	public abstract void startReceiving();
	
	public abstract void pauseReceiving();
	
	public abstract void resumeReceiving();
	
	public abstract void stopReceiving();
	
	public static interface OnDataReceivedListener{
		public void onDataReceveid(WalkData walkData);
	}
	
	public static interface OnErrorReceivingListener{
		
	}
	

	public static final int
		SINGLE_ACCELEROMETER = 0,
		TWO_ACCELEROMETERS = 1,
		GUI_RECEIVER = -1;
}
