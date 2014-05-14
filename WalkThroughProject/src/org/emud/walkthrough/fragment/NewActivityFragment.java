package org.emud.walkthrough.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.walkthrough.R;
import org.emud.walkthrough.ResultGUIResolver;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.dialogfragment.AlertDialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;

public class NewActivityFragment extends Fragment implements OnClickListener {
	private ArrayList<Integer> analystList;
	private int receiver;
	private OnAcceptButtonClickedListener listener;
	private int[] resultTypes;

	public void setListener(OnAcceptButtonClickedListener listener) {
		this.listener = listener;
	}
	
	public void setResultTypes(int[] types){
		resultTypes = types;
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

		view.findViewById(R.id.receiver_radio0).setOnClickListener(this);
		view.findViewById(R.id.receiver_radio1).setOnClickListener(this);
		view.findViewById(R.id.newactivity_acceptbutton).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		WalkThroughApplication app = (WalkThroughApplication) getActivity().getApplicationContext();
		int n = resultTypes.length;
		ViewGroup layout = (ViewGroup) getView().findViewById(R.id.analyst_checkBox_content);

		OnClickListener checkBoxListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				onAnalystCheckBoxClicked(v.getId(), ((CheckBox) v).isChecked());
			}
		};
		
		
		for(int i=0; i<n; i++){
			ResultGUIResolver resolver = app.getGUIResolver(resultTypes[i]);
			CheckBox cb = new CheckBox(getActivity());
			
			cb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			cb.setText(resolver.getTitle());
			cb.setOnClickListener(checkBoxListener);
			cb.setId(i+1);
			
			layout.addView(cb);
		}
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.newactivity_acceptbutton){
			onAcceptClicked();			
		}else{
			onReceiverRadioClicked(view.getId());
		}
	}
	
	private void onAcceptClicked() {		
		if(analystList.size()>0){
			listener.acceptButtonClicked(receiver, analystList);
		}else{
			DialogFragment dialogFragment = AlertDialogFragment.newInstance(R.string.nrpd_title, R.string.nrpd_message);
			dialogFragment.show(getActivity().getSupportFragmentManager(), "noResultsdDialog");
		}
	}

	private void onReceiverRadioClicked(int id){
		switch(id){
		case R.id.receiver_radio0:
			receiver = WalkDataReceiver.SINGLE_ACCELEROMETER;
			break;
		case R.id.receiver_radio1:
			receiver = WalkDataReceiver.TWO_ACCELEROMETERS;
			break;
		}
	}
	
	private void onAnalystCheckBoxClicked(int id, boolean checked){
		int analyst = resultTypes[id-1];
		
		if(checked){
			analystList.add(Integer.valueOf(analyst));
		}else{
			analystList.remove(Integer.valueOf(analyst));
		}
	}
	
	public static interface OnAcceptButtonClickedListener{
		public void acceptButtonClicked(int receiver, List<Integer> analystList);
	}
}
