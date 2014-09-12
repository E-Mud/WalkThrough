package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Filter;

public class GaitCycle implements Filter {
	private static final int FILTER_SIZE = 5;
	private CycleExtractor right, left;
	
	public GaitCycle(int receiverType){
		right = new CycleExtractor();
		if(receiverType == AnalysisService.TWO_ACCELEROMETERS)
			left = new CycleExtractor();
	}
	
	@Override
	public AccelerometerData filter(AccelerometerData accelerometerData) {
		if(accelerometerData.getLocation() == AccelerometerData.LOCATION_RIGHT_ANKLE || 
				accelerometerData.getLocation() == AccelerometerData.LOCATION_TRUNK)
			return right.filter(accelerometerData);
		
		if(accelerometerData.getLocation() == AccelerometerData.LOCATION_LEFT_ANKLE)
			return left.filter(accelerometerData);
		
		return null;
	}
	
	public boolean cycleCompleted(int location){
		if(location == AccelerometerData.LOCATION_RIGHT_ANKLE || 
				location == AccelerometerData.LOCATION_TRUNK)
			return right.cycleCompleted();
		
		if(location == AccelerometerData.LOCATION_LEFT_ANKLE)
			return left.cycleCompleted();
		
		return false;
	}
	
	private static class CycleExtractor{
		private MedianFilter medianFilter;
		private int nSamples, sign;
		private boolean cycleCompleted;

		public CycleExtractor(){
			medianFilter = new MedianFilter(FILTER_SIZE);
			nSamples = 0;
			cycleCompleted = false;
		}

		public AccelerometerData filter(AccelerometerData accelerometerData) {
			double filteredAtp = medianFilter.applyFilter(accelerometerData.getData()[0]);
			
			nSamples++;
			
			if(nSamples > FILTER_SIZE/2 ){
				checkSign(filteredAtp);
				accelerometerData.setDataValue(0, filteredAtp);
				return accelerometerData;
			}else{
				return null;
			}
		}

		private void checkSign(double currentValue) {
			switch(sign){
			case 0:
				if(currentValue < 0)
					sign = -1;
				cycleCompleted = false;
				break;
			case 1:
				if(currentValue < 0){
					sign = -1;
					cycleCompleted = true;
				}
				break;
			case -1:
				if(currentValue > 0)
					sign = 1;
				cycleCompleted = false;
				break;
			}
		}
		
		public boolean cycleCompleted(){
			return cycleCompleted;
		}
	}
}
