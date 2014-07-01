package org.emud.walkthrough.stub;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.pedometer.MedianFilter;

import android.content.Context;

public class MaxMoveAnalyst implements Analyst {
	/*private AccelerometerData max;
	
	public MaxMoveAnalyst(){
		max = new AccelerometerData(new double[]{0,0,0}, 0, 0);
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		double absValueMax = getAbsoluteValue(max.getData());
		double absValueNew = getAbsoluteValue(accelerometerData.getData());
		
		if(absValueNew > absValueMax)
			max = accelerometerData;
	}

	@Override
	public Result getResult() {
		double maxMove = getAbsoluteValue(max.getData());
		
		return new ResultMaxMove(Double.valueOf(maxMove));
	}

	private double getAbsoluteValue(double[] ds) {
		double value = 0;
		
		for(int i=0; i<3; i++)
			value += ds[i]*ds[i];
		
		value = Math.sqrt(value);
		
		return value;
	}*/
	private Context context;
	private static final int WINDOW_SIZE = 25;
	private double[] autoCorrelX, autoCorrelY, rawSamplesX, rawSamplesY;
	private int windowFillingIndex, index;
	
	private double[][] dataOutput;
	private int outputIndex, filterIndex;
	private MedianFilter medianFilter;
	
	public MaxMoveAnalyst(Context con){
		windowFillingIndex = index = 0;
		autoCorrelX = new double[WINDOW_SIZE];
		autoCorrelY = new double[WINDOW_SIZE];
		rawSamplesX = new double[WINDOW_SIZE];
		rawSamplesY = new double[WINDOW_SIZE];
		
		context = con;
		dataOutput = new double[7][1024];
		outputIndex = 0;
		medianFilter = new MedianFilter(3);
		filterIndex = 0;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		double atp, vt;

		atp = medianFilter.applyFilter(accelerometerData.getData()[0]);
		vt = medianFilter.applyFilter(accelerometerData.getData()[1]);
		filterIndex++;
		if(filterIndex<3)
			return;
		
		if(outputIndex >= 1024)
			return;
		
		dataOutput[0][outputIndex] = accelerometerData.getData()[0];
		dataOutput[1][outputIndex] = accelerometerData.getData()[1];
		dataOutput[2][outputIndex] = accelerometerData.getData()[2];
		dataOutput[5][outputIndex] = atp;
		dataOutput[6][outputIndex] = vt;
		
		if(windowFillingIndex<WINDOW_SIZE){
			rawSamplesX[windowFillingIndex] = atp;
			rawSamplesY[windowFillingIndex] = vt;
			windowFillingIndex++;
			
			if(windowFillingIndex == WINDOW_SIZE){
				autoCorrelX = autocorrelation(rawSamplesX);
				autoCorrelY = autocorrelation(rawSamplesY);
				int n = autoCorrelX.length;
				AccelerometerData accData = new AccelerometerData(new double[]{0,0,0}, 0, 0);
				
				for(int i=0; i<n; i++){
					dataOutput[3][i] = autoCorrelX[i];
					dataOutput[4][i] = autoCorrelY[i];
				}
			}
		}else{
			autoCorrelX[index] = autoCorrelX[index] * atp;
			autoCorrelY[index] = autoCorrelY[index] * vt;
			accelerometerData.setDataValue(0, autoCorrelX[index] + autoCorrelY[index]);

			dataOutput[3][outputIndex] = autoCorrelX[index];
			dataOutput[4][outputIndex] = autoCorrelY[index];

			
			index = (index+1)%WINDOW_SIZE;
		}
		
		outputIndex++;
	}

	private double[] autocorrelation(double[] data) {
		int n= data.length;
		double[] result = new double[n];
		double sum;
		
		for(int m=0; m<n; m++){
			sum = 0;
			for(int i=0; i<n-m; i++)
				sum += data[i]*data[i+m];
			result[m] = sum/(double)(n-m);
		}
		
		return result;
	}

	@Override
	public Result getResult() {
		//TODO
		FileOutputStream outputStream;
		DataOutputStream dataOutputStream;
		byte[] doubleBytes = new byte[8];
		long lng;

		try {
		  outputStream = context.openFileOutput("walkdata.dat", Context.MODE_PRIVATE);
		  dataOutputStream = new DataOutputStream(outputStream);
		  for(int i=0; i<outputIndex; i++)
			  for(int j=0; j<7; j++){
				  dataOutputStream.writeDouble(dataOutput[j][i]);
				  /*lng = Double.doubleToLongBits(dataOutput[j][i]);
				  for(int k=0; k<8; k++)
					  doubleBytes[k] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
				  outputStream.write(doubleBytes);*/
			  }
		  
		  dataOutputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
		return new ResultMaxMove(0);
	}


}
