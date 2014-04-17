package org.emud.walkthrough.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class WalkActivity {
	private GregorianCalendar date;
	private ArrayList<Result> results;
	
	public WalkActivity(GregorianCalendar adate, List<Result> resultsList){
		date = adate;
		results = new ArrayList<Result>(resultsList);
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
	
	public List<Result> gerResults(){
		return results;
	}

}
