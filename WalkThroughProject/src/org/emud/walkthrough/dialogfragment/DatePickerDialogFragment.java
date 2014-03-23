package org.emud.walkthrough.dialogfragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerDialogFragment extends DialogFragment {

	public static DatePickerDialogFragment newInstance(int day, int month, int year){
		DatePickerDialogFragment result = new DatePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt("day", day);
		args.putInt("month", month);
		args.putInt("year", year);
		result.setArguments(args);

		return result;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		return new DatePickerDialog(getActivity(), (OnDateSetListener)getActivity(), getArguments().getInt("year"), getArguments().getInt("month"), getArguments().getInt("day"));
	}

}
