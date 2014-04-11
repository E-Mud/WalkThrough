package org.emud.walkthrough.model;

import java.util.GregorianCalendar;

public class WalkActivity {
	private GregorianCalendar date;
	
	public WalkActivity(GregorianCalendar adate){
		date = adate;
	}

	/**
	 * @return the date
	 */
	public GregorianCalendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

}
