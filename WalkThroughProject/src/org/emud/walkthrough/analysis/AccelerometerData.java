package org.emud.walkthrough.analysis;

public class AccelerometerData {
	public static final int AP_AXIS = 0, VT_AXIS = 1, MP_AXIS = 2;
	private long timeStamp;
	private int ratio;
	private double[] data;
	
	public AccelerometerData(double[] data, long timeStamp, int ratio){
		this.timeStamp = timeStamp;
		this.data = data;
		this.ratio = ratio;
	}
	
	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the data
	 */
	public double[] getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(double[] data) {
		this.data = data;
	}

	/**
	 * @return the ratio
	 */
	public int getRatio() {
		return ratio;
	}

	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
}
