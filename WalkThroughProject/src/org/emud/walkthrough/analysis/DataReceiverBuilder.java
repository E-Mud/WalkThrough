package org.emud.walkthrough.analysis;

import org.emud.walkthrough.stub.FalseReceiver;

public class DataReceiverBuilder {
	
	private DataReceiverBuilder(){
	}
	
	public static WalkDataReceiver buildReceiver(ReceiverType type){
		WalkDataReceiver receiver = null;
		//TODO
		switch(type){
		case SINGLE_ACCELEROMETER:
			break;
		case TWO_ACCELEROMETERS:
			break;
		case GUI_RECEIVER:
			receiver = new FalseReceiver();
			break;
		default:
			break;
		}
		
		return receiver;
	}
	
	public enum ReceiverType{
		SINGLE_ACCELEROMETER,
		TWO_ACCELEROMETERS,
		GUI_RECEIVER
	}
}
