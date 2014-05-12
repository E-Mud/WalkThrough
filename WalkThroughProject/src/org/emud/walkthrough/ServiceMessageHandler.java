package org.emud.walkthrough;

import android.os.Handler;
import android.os.Message;

public class ServiceMessageHandler extends Handler {
	public static final int MSG_START = 0, MSG_STOP = 1, MSG_PAUSE = 2, MSG_RESUME = 3, MSG_STATE = 4;
	public static final String STATE_KEY = "currentState";
	private OnMessageReceivedListener listener;

	public ServiceMessageHandler(OnMessageReceivedListener lstner){
		this.listener = lstner;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_START:
			listener.onStartMessage(msg);
			break;
		case MSG_STOP:
			listener.onStopMessage(msg);
			break;
		case MSG_PAUSE:
			listener.onPauseMessage(msg);
			break;
		case MSG_RESUME:
			listener.onResumeMessage(msg);
			break;
		case MSG_STATE:
			listener.onStateMessage(msg);
		default:
			super.handleMessage(msg);
		}
	}

	public static interface OnMessageReceivedListener{
		public void onStartMessage(Message msg);
		public void onStateMessage(Message msg);
		public void onPauseMessage(Message msg);
		public void onResumeMessage(Message msg);
		public void onStopMessage(Message msg);
	}
}
