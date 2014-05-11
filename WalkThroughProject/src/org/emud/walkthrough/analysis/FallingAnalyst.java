package org.emud.walkthrough.analysis;

import org.emud.walkthrough.model.Result;

public class FallingAnalyst implements Analyst {
	private static final int WINDOW_SIZE = 25;
	private static final double FALLING_THRESHOLD = WINDOW_SIZE*1.5, FALLED_THRESHOLD = WINDOW_SIZE/4.0d;
	private double[] fallingWindow, falledWindow;
	private double sumFalling, sumFalled, lastAbsAccel;
	private int index, windowSize;
	private boolean falling, falled, windowFull;
	private OnFallDetectedListener listener;
	
	public FallingAnalyst(OnFallDetectedListener lstner){
		fallingWindow = new double[WINDOW_SIZE];
		falledWindow = new double[WINDOW_SIZE];
		falling = falled = windowFull = false;
		sumFalling = sumFalled = lastAbsAccel = 0;
		windowSize = index = 0;
		listener = lstner;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		insertNewData(accelerometerData.getData());
		
		if(!windowFull && ++windowSize == WINDOW_SIZE)
			windowFull = true;
		
		falling = windowFull && isFalling();
		falled = falling && isFalled();
		
		if(falled){
			windowFull = false;
			windowSize = 0;
			listener.fallDetected();
		}
	}

	private void insertNewData(double[] data) {
		double absAccel = getAbsoluteValue(data);
		double newDiff = Math.abs(absAccel - lastAbsAccel);
		
		lastAbsAccel = absAccel;
		
		sumFalling -= fallingWindow[index];
		fallingWindow[index] = falledWindow[index];
		sumFalling += fallingWindow[index]; 
		
		sumFalled -= falledWindow[index];
		falledWindow[index] = newDiff;
		sumFalled += falledWindow[index];
		
		index = (index+1)%WINDOW_SIZE;
	}

	private boolean isFalling() {
		return sumFalling > FALLING_THRESHOLD;
	}

	private boolean isFalled() {
		return sumFalled < FALLED_THRESHOLD;
	}

	private double getAbsoluteValue(double[] ds) {
		double value = 0;
		
		for(int i=0; i<3; i++)
			value += ds[i]*ds[i];
		
		value = Math.sqrt(value);
		
		return value;
	}

	@Override
	public Result getResult() {
		return null;
	}

}
