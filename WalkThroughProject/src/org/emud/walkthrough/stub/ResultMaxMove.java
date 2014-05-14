package org.emud.walkthrough.stub;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

public class ResultMaxMove extends Result {
	private double maxAccel;

	public ResultMaxMove() {
		this(0.0d);
	}
	
	public ResultMaxMove(double accel){
		super(ResultType.RT_MAX_MOVE);
		maxAccel = accel;
	}

	/**
	 * @return the maxAccel
	 */
	public double getMaxAcceleration() {
		return maxAccel;
	}

	/**
	 * @param maxAccel the maxAccel to set
	 */
	public void setMaxAcceleration(double maxAccel) {
		this.maxAccel = maxAccel;
	}
	
	
}
