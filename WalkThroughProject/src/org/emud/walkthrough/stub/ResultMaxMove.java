package org.emud.walkthrough.stub;

import org.emud.walkthrough.model.Result;

import android.os.Bundle;

public class ResultMaxMove extends Result {

	public ResultMaxMove() {
		super(RT_MAX_MOVE);
	}
	
	public ResultMaxMove(Object result) {
		super(result, RT_MAX_MOVE);
	}

	@Override
	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		
		bundle.putDouble("maxValue", ((Double)get()).doubleValue());
		
		return bundle;
	}

	@Override
	public void fromBundle(Bundle bundle) {
		double maxValue = bundle.getDouble("maxValue");
		
		set(Double.valueOf(maxValue));
	}

}
