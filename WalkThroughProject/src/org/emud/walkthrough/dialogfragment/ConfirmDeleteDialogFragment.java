package org.emud.walkthrough.dialogfragment;

import org.emud.walkthrough.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfirmDeleteDialogFragment extends DialogFragment implements OnClickListener, TextWatcher {
	private String username;
	private Button acceptButton;
	private OnAcceptButtonListener listener;

	public static ConfirmDeleteDialogFragment newInstance(String userName){
		ConfirmDeleteDialogFragment result = new ConfirmDeleteDialogFragment();
		Bundle args = new Bundle();
		args.putString("userName", userName);
		result.setArguments(args);
		
		return result;
	}
	
	public OnAcceptButtonListener getAcceptButtonListener() {
		return listener;
	}

	public void setAcceptButtonListener(OnAcceptButtonListener listener) {
		this.listener = listener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		username = getArguments().getString("userName");
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirmdelete, null);
		
		builder.setTitle(R.string.cdd_title)
			.setPositiveButton(R.string.accept_label, this)
			.setNegativeButton(R.string.cancel_label, this)
			.setView(view);
		
		return builder.create();
	}

	@Override
	public void onResume(){
		super.onResume();
		
		EditText username = ((EditText)getDialog().findViewById(R.id.confirmdelete_username));
		acceptButton = ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		
		acceptButton.setEnabled(false);
		username.addTextChangedListener(this);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case AlertDialog.BUTTON_POSITIVE:
			if(listener != null)
				listener.buttonClicked();
			dismiss();
			break;
		case AlertDialog.BUTTON_NEGATIVE:
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {
		if(editable.toString().equals(username)){
			acceptButton.setEnabled(true);
		}else{
			acceptButton.setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	public static interface OnAcceptButtonListener{
		public void buttonClicked();
	}
}
