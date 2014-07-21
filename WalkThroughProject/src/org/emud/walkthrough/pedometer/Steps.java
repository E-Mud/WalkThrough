package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

public class Steps extends Result {
	private int steps;

	public Steps() {
		this(0);
	}

	public Steps(int count){
		super(ResultType.RT_STEPS);
		steps = count;
	}
	
	/**
	 * @return the steps
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	@Override
	public double doubleValue() {
		return steps;
	}

	@Override
	public String valueAsString() {
		return ""+steps;
	}
}
