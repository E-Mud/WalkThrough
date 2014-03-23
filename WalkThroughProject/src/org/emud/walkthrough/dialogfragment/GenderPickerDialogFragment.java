package org.emud.walkthrough.dialogfragment;

import org.emud.walkthrough.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GenderPickerDialogFragment extends DialogFragment {

	public static GenderPickerDialogFragment newInstance(int which){
		GenderPickerDialogFragment result = new GenderPickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt("which", which);
		result.setArguments(args);
		
		return result;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		return new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getResources().getString(R.string.genderpicker_title))
				.setSingleChoiceItems(R.array.genderpicker_stringarray, getArguments().getInt("which"), (OnClickListener) getActivity())
				.create();
				
	}
}
