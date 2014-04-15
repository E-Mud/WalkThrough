package org.emud.walkthrough.analysis;

public interface WalkDataReceiver {
	
	public void addOnDataReceveidListener(OnDataReceivedListener listener);
	
	public void removeOnDataReceivedListener(OnDataReceivedListener listener);
	
	public void startReceiving();
	
	public void pauseReceiving();
	
	public void resumeReceiving();
	
	public void stopReceiving();
	
	public static interface OnDataReceivedListener{
		public void onDataReceveid(WalkData walkData);
	}
	
	public static interface OnErrorReceivingListener{
		
	}
}
