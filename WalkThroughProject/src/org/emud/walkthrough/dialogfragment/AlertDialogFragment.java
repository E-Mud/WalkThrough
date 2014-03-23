package org.emud.walkthrough.dialogfragment;

import org.emud.walkthrough.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment implements OnClickListener {
	
	public static AlertDialogFragment newInstance(int titleResource, int messageResource){
		AlertDialogFragment result = new AlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("titleResource", titleResource);
		args.putInt("messageResource", messageResource);
		result.setArguments(args);
		
		return result;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Bundle args = getArguments();
		return new AlertDialog.Builder(getActivity())
				.setTitle(args.getInt("titleResource"))
				.setMessage(args.getInt("messageResource"))
				.setPositiveButton(R.string.accept_label, this)
				.create();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		getDialog().dismiss();
	}

}
