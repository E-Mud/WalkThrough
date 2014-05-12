package org.emud.walkthrough.analysis;


public interface DataReceiverBuilder {
	
	/**
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public WalkDataReceiver buildReceiver(int type);
}
