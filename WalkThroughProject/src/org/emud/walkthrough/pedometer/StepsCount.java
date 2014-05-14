package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

public class StepsCount extends Result {
	private int steps;

	public StepsCount() {
		this(0);
	}

	public StepsCount(int count){
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
}
