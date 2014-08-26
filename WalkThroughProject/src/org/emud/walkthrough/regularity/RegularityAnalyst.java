package org.emud.walkthrough.regularity;

import java.util.ArrayList;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;

public class RegularityAnalyst implements Analyst {
	private ArrayList<Double> data;

	public RegularityAnalyst(){
		data = new ArrayList<Double>();
	}
	
	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		data.add(accelerometerData.getData()[0]);
	}

	@Override
	public Result getResult() {
		int n = data.size();
		double[] dataArray = new double[n], peakValues = new double[3];
		double firstValue;
		double suma = 0.0;
		
		for(int i=0; i<n; i++){
			dataArray[i] = data.get(i);
			suma += dataArray[i]*dataArray[i];
		}
		
		peakValues[0] = peakValues[1] = peakValues[2] = 1.0;
		
		firstValue = suma/(double) n;
		
		for(int i=0; i<n; i++){
			suma = 0.0;
			for(int j=0; j<n-i; j++)
				suma += dataArray[j]*dataArray[j+i];
			
			peakValues[0] = peakValues[1];
			peakValues[1] = peakValues[2];
			peakValues[2] = (suma/(double) (n-i))/firstValue;
			
			android.util.Log.e("RA", "peak: " + peakValues[0] + " " + peakValues[1] + " " + peakValues[2]);
			
			if(peakValues[1] > peakValues[0] && peakValues[1] > peakValues[2])
				return new Regularity(peakValues[1]);
		}

		return new Regularity(0.0);
	}

}
