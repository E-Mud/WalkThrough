package org.emud.walkthrough.database;

import java.util.GregorianCalendar;

import org.emud.content.Query;

import android.database.Cursor;

public class ActivitiesQuery implements Query<Cursor> {
	private GregorianCalendar fromDate, toDate;
	private ActivitiesDataSource dataSource;
	
	public ActivitiesQuery(ActivitiesDataSource dataSource){
		this(dataSource, null, null);
	}
	
	public ActivitiesQuery(ActivitiesDataSource source, GregorianCalendar from, GregorianCalendar to){
		dataSource = source;
		fromDate = from;
		toDate = to;
	}
	
	/**
	 * @return the fromDate
	 */
	public GregorianCalendar getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(GregorianCalendar fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public GregorianCalendar getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(GregorianCalendar toDate) {
		this.toDate = toDate;
	}
	
	@Override
	public Cursor execute() {
		return dataSource.getActivities(fromDate, toDate);
	}

}
