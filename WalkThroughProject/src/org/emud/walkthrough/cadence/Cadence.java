package org.emud.walkthrough.cadence;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

public class Cadence extends Result {
	private double cadence;
	
	public Cadence() {
		this(0);
	}

	public Cadence(double cad) {
		super(ResultType.RT_CADENCE);
		cadence = cad;
	}

	/**
	 * @return the cadence
	 */
	public double getCadence() {
		return cadence;
	}

	/**
	 * @param cadence the cadence to set
	 */
	public void setCadence(double cadence) {
		this.cadence = cadence;
	}

	@Override
	public double doubleValue() {
		return cadence;
	}

	@Override
	public String valueAsString() {
		return String.format("%.2f", cadence);
	}

}
