package org.emud.walkthrough.regularity;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

import android.annotation.SuppressLint;

public class Regularity extends Result {
	private double regularity;

	public Regularity(double regularity) {
		super(ResultType.RT_REGULARITY);
		this.regularity = regularity;
	}
	
	public double getRegularity() {
		return regularity;
	}

	@Override
	public double doubleValue() {
		return regularity;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String valueAsString() {
		return String.format("%.2f", regularity);
	}

}
