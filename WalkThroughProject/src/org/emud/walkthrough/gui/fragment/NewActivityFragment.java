package org.emud.walkthrough.gui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.emud.walkthrough.R;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.gui.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.resulttype.ResultGUIResolver;
import org.emud.walkthrough.resulttype.ResultType;

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
	private ArrayList<ResultType> analystList;
	private int receiver;
	private OnAcceptButtonClickedListener listener;

	public void setListener(OnAcceptButtonClickedListener listener) {
		this.listener = listener;
	}
	
	
	@Override
	public void onCreate(Bundle onSavedInstanceState){
		super.onCreate(onSavedInstanceState);
		analystList = new ArrayList<ResultType>();
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
		ViewGroup layout = (ViewGroup) getView().findViewById(R.id.analyst_checkBox_content);

		OnClickListener checkBoxListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				onAnalystCheckBoxClicked(v.getId(), ((CheckBox) v).isChecked());
			}
		};
		
		for(ResultType resultType : ResultType.values()){
			ResultGUIResolver resolver = resultType.getGUIResolver();
			CheckBox cb = new CheckBox(getActivity());
			
			cb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			cb.setText(resolver.getLongTitleResource());
			cb.setOnClickListener(checkBoxListener);
			cb.setId(resultType.intValue() + 1);
			
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
		ResultType analyst = ResultType.valueOf(id-1);
		
		if(checked){
			analystList.add(analyst);
		}else{
			analystList.remove(analyst);
		}
	}
	
	public static interface OnAcceptButtonClickedListener{
		public void acceptButtonClicked(int receiver, List<ResultType> analystList);
	}
}
