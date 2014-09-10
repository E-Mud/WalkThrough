package org.emud.walkthrough.length;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultType;

import android.annotation.SuppressLint;

public class Length extends Result {
	private double length;
	
	public Length(double l){
		super(ResultType.RT_LENGTH);
		length = l;
	}

	@Override
	public double doubleValue() {
		return length;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String valueAsString() {
		return String.format("%.2f", length);
	}

	public double getLength() {
		return length;
	}

}
