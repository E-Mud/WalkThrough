package org.emud.walkthrough.monitor;

import org.emud.walkthrough.R;
import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.monitor.MonitorBroadcastReceiver.MonitorListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class MonitorFragment extends Fragment implements MonitorListener {
	private ProgressBar rightXpb, rightYpb, rightZpb;
	private ProgressBar leftXpb, leftYpb, leftZpb;
	private MonitorBroadcastReceiver receiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		receiver = new MonitorBroadcastReceiver();
		
		receiver.setListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance){
		View view = inflater.inflate(R.layout.fragment_monitor, container, false);
		
		rightXpb = (ProgressBar) view.findViewById(R.id.progressBarRX);
		rightYpb = (ProgressBar) view.findViewById(R.id.progressBarRY);
		rightZpb = (ProgressBar) view.findViewById(R.id.progressBarRZ);
		leftXpb = (ProgressBar) view.findViewById(R.id.progressBarLX);
		leftYpb = (ProgressBar) view.findViewById(R.id.progressBarLY);
		leftZpb = (ProgressBar) view.findViewById(R.id.progressBarLZ);
		
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		getActivity().registerReceiver(receiver, MonitorBroadcastReceiver.getIntentFilter());
	}
	
	@Override
	public void onPause(){
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}

	@Override
	public void onDataChanged(Intent intent) {
		double[] data = intent.getDoubleArrayExtra(MonitorBroadcastReceiver.DATA_KEY);
		int n = data.length, maxLevel = rightXpb.getMax();
		int location = intent.getIntExtra(MonitorBroadcastReceiver.LOCATION_KEY, AccelerometerData.LOCATION_RIGHT_ANKLE);
		int[] progressLevels = new int[n];
		
		for(int i=0; i<n; i++){
			int progLevel = (int) data[i];
			if(progLevel > maxLevel)
				progLevel = maxLevel;
			if(progLevel < 0)
				progLevel = 0;
			progressLevels[i] = progLevel;
		}
		
		if(location == AccelerometerData.LOCATION_RIGHT_ANKLE){
			rightXpb.setProgress(progressLevels[0]);
			rightYpb.setProgress(progressLevels[1]);
			rightZpb.setProgress(progressLevels[2]);
		}else{
			leftXpb.setProgress(progressLevels[0]);
			leftYpb.setProgress(progressLevels[1]);
			leftZpb.setProgress(progressLevels[2]);
		}
	}
}
