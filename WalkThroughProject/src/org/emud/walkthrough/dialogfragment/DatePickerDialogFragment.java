package org.emud.walkthrough.dialogfragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment implements OnDateSetListener {
	private OnDatePickedListener onDatePickedListener;
	private int year, month, day;

	public OnDatePickedListener getOnDateSetListener() {
		return onDatePickedListener;
	}

	public void setOnDatePickedListener(OnDatePickedListener onDateSetlistener) {
		this.onDatePickedListener = onDateSetlistener;
	}

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
		Bundle args = getArguments();
		year = args.getInt("year");
		month = args.getInt("month");
		day = args.getInt("day");
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog){
		onDatePickedListener.datePicked(year, month, day);
		super.onDismiss(dialog);
	}
	
	@Override
	public void onDateSet(DatePicker view, int newYear, int monthOfYear,
			int dayOfMonth) {
		year = newYear;
		month = monthOfYear;
		day = dayOfMonth;
	}

	
	public static interface OnDatePickedListener{
		public void datePicked(int year, int month, int day);
	}
}
