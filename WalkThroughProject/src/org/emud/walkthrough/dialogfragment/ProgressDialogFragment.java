package org.emud.walkthrough.dialogfragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	public static ProgressDialogFragment newInstance(int messageResource){
		ProgressDialogFragment result = new ProgressDialogFragment();
		Bundle args = new Bundle();
		args.putInt("message", messageResource);
		result.setArguments(args);
		
		return result;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
 
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(getArguments().getInt("message")));
		dialog.setIndeterminate(true);
		setCancelable(false);

		return dialog;
	}
}
