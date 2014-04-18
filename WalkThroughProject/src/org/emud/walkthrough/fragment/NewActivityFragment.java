package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.walkthrough.R;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.model.Result;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class NewActivityFragment extends Fragment implements OnClickListener {
	private ArrayList<Integer> analystList;
	private int receiver;
	private OnAcceptButtonClickedListener listener;

	public void setListener(OnAcceptButtonClickedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle onSavedInstanceState){
		super.onCreate(onSavedInstanceState);
		analystList = new ArrayList<Integer>();
		receiver = WalkDataReceiver.SINGLE_ACCELEROMETER;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_newactivity, null);
		
		view.findViewById(R.id.analyst_checkBox0).setOnClickListener(this);
		view.findViewById(R.id.receiver_radio0).setOnClickListener(this);
		view.findViewById(R.id.receiver_radio1).setOnClickListener(this);
		view.findViewById(R.id.newactivity_acceptbutton).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.newactivity_acceptbutton:
			onAcceptClicked();
			break;
		case R.id.analyst_checkBox0:
			onAnalystCheckBoxClicked((CheckBox) view);
			break;
		case R.id.receiver_radio0:
		case R.id.receiver_radio1:
			onReceiverRadioClicked((RadioButton) view);
			break;
		}
	}
	
	private void onAcceptClicked() {		
		if(analystList.size()>0){
			listener.acceptButtonClicked(receiver, analystList);
		}else{
			DialogFragment dialogFragment = AlertDialogFragment.newInstance(R.string.nrpd_title, R.string.nrpd_message);
			dialogFragment.show(getActivity().getSupportFragmentManager(), "connectionFailedDialog");
		}
	}

	private void onReceiverRadioClicked(RadioButton radioButton){
		switch(radioButton.getId()){
		case R.id.receiver_radio0:
			receiver = WalkDataReceiver.SINGLE_ACCELEROMETER;
			break;
		case R.id.receiver_radio1:
			receiver = WalkDataReceiver.TWO_ACCELEROMETERS;
			break;
		}
	}
	
	private void onAnalystCheckBoxClicked(CheckBox checkBox){
		int analyst = 0;
		switch(checkBox.getId()){
		case R.id.analyst_checkBox0:
			analyst = Result.RT_MAX_MOVE;
			break;
		}
		
		if(checkBox.isChecked()){
			analystList.add(Integer.valueOf(analyst));
		}else{
			analystList.remove(Integer.valueOf(analyst));
		}
	}
	
	public static interface OnAcceptButtonClickedListener{
		public void acceptButtonClicked(int receiver, List<Integer> analystList);
	}
}
