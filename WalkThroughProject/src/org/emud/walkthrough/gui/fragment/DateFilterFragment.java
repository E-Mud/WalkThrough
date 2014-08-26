package org.emud.walkthrough.gui.fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.R;
import org.emud.walkthrough.gui.dialogfragment.DatePickerDialogFragment;
import org.emud.walkthrough.gui.dialogfragment.DatePickerDialogFragment.OnDatePickedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class DateFilterFragment extends Fragment implements OnClickListener, OnDatePickedListener {
	private TextView fromDateText, toDateText;
	private DateFilter dateFilter;
	private int dateDialogShowing;
	
	public DateFilterFragment(){
		super();
		dateFilter = new DateFilter();
	}
	
	/**
	 * @return the dateFilter
	 */
	public DateFilter getDateFilter() {
		return dateFilter;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			long dateMillis;
			GregorianCalendar date;
			
			dateMillis = savedInstanceState.getLong("fromDate", -1);
			date = new GregorianCalendar();
			date.setTimeInMillis(dateMillis);
			dateFilter.setFromDate(date);
			
			dateMillis = savedInstanceState.getLong("toDate", -1);
			date = new GregorianCalendar();
			date.setTimeInMillis(dateMillis);
			dateFilter.setToDate(date);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		GregorianCalendar date;
		
		date = dateFilter.getFromDate();		
		if(date != null)
			saveInstanceState.putLong("fromDate", date.getTimeInMillis());
		
		date = dateFilter.getToDate();		
		if(date != null)
			saveInstanceState.putLong("toDate", date.getTimeInMillis());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_datefilter, null);
		
		fromDateText = (TextView) view.findViewById(R.id.drawer_fromDate);
		toDateText = (TextView) view.findViewById(R.id.drawer_toDate);
		
		fromDateText.setOnClickListener(this);
		toDateText.setOnClickListener(this);

		setFromDateText();
		setToDateText();
		
		return view;
	}


	@Override
	public void onClick(View v) {
		DatePickerDialogFragment dialogFragment;
		GregorianCalendar date;
		int id = v.getId();
		
		if(id == R.id.drawer_fromDate){
			date = dateFilter.getFromDate();
		}else{
			date = dateFilter.getToDate();
		}
		
		if(date == null)
			date = (GregorianCalendar) GregorianCalendar.getInstance();
		
		dateDialogShowing = id;

		dialogFragment = DatePickerDialogFragment.newInstance(date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
		dialogFragment.setOnDatePickedListener(this);
		dialogFragment.show(getFragmentManager(), "datePickerDialog" + id);
	}
	
	private void setFromDateText() {
		GregorianCalendar fromDate = dateFilter.getFromDate();
		if(fromDate != null)
			fromDateText.setText(DateFormat.format("EEE d/M/yyyy", fromDate));
	}
	
	private void setToDateText() {
		GregorianCalendar toDate = dateFilter.getToDate();
		if(toDate != null)
			toDateText.setText(DateFormat.format("EEE d/M/yyyy", toDate));
	}

	@Override
	public void datePicked(int year, int month, int day) {
		if(dateDialogShowing == R.id.drawer_fromDate){
			dateFilter.setFromDate(year, month, day);
			setFromDateText();
		}else{
			dateFilter.setToDate(year, month, day);
			setToDateText();
		}
	}
}
