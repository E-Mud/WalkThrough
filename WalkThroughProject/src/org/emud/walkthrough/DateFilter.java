package org.emud.walkthrough;

import java.util.GregorianCalendar;

import org.emud.content.DataSubject;

public class DateFilter {
	private GregorianCalendar fromDate, toDate;
	private DataSubject dataSubject;
	
	public DateFilter(){
		dataSubject = new DataSubject();
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
		dataSubject.notifyObservers();
	}
	
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(int year, int month, int day) {
		if(fromDate == null)
			fromDate = new GregorianCalendar();
		
		fromDate.set(year, month, day);
		dataSubject.notifyObservers();
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
		dataSubject.notifyObservers();
	}
	
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setToDate(int year, int month, int day) {
		if(toDate == null)
			toDate = new GregorianCalendar();
		
		toDate.set(year, month, day);
		dataSubject.notifyObservers();
	}

	/**
	 * @return the dataSubject
	 */
	public DataSubject getDataSubject() {
		return dataSubject;
	}
	
	
	
}
