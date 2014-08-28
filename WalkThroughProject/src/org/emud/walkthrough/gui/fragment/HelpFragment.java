package org.emud.walkthrough.gui.fragment;

import org.emud.walkthrough.R;
import org.emud.walkthrough.analysisservice.AnalysisService;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpFragment extends Fragment {
	private int receiverType, serviceStatus;
	private TextView text;
	private static final String RECEIVER_TYPE_KEY = "rec", SERVICE_STATUS_KEY = "serv";
	
	public static Bundle buildArguments(int recType, int servStatus){
		Bundle args = new Bundle();
		
		args.putInt(RECEIVER_TYPE_KEY, recType);
		args.putInt(SERVICE_STATUS_KEY, servStatus);
		
		return args;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_help, null);
		Bundle args = getArguments();
		
		text = (TextView) view.findViewById(R.id.help_text);
		
		receiverType = args.getInt(RECEIVER_TYPE_KEY, AnalysisService.SINGLE_ACCELEROMETER);
		int servStatus = args.getInt(SERVICE_STATUS_KEY, AnalysisService.SERVICE_STOPPED);
		
		setServiceStatus(servStatus);
		
		return view;
	}
	
	public void setServiceStatus(int servStatus){
		if(servStatus == serviceStatus)
			return;
		
		serviceStatus = servStatus;
		
		switch(serviceStatus){
		case AnalysisService.SERVICE_PREPARED:
			if(receiverType == AnalysisService.SINGLE_ACCELEROMETER){
				text.setText(R.string.help_sa_prepared);
			}else{
				text.setText(R.string.help_ta_prepared);				
			}
			break;
		case AnalysisService.SERVICE_CONNECTING:
			text.setText(R.string.help_ta_connecting);
			break;
		case AnalysisService.SERVICE_UNSTARTED:
			text.setText(R.string.help_ta_unstarted);
			break;
		default:
			text.setText("");
			break;
		}
	}
}
