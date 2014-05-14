package org.emud.walkthrough.speedometer;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

public class Speed extends Result {
	private double speed;

	public Speed() {
		super(ResultType.RT_SPEED);
		speed = 0;
	}
	
	public Speed(double sp) {
		super(ResultType.RT_SPEED);
		speed = sp;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
}
