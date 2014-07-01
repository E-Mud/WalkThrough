package org.emud.walkthrough.model;

import java.util.GregorianCalendar;

import org.emud.walkthrough.resulttype.ResultType;


//TODO
public abstract class Result {
	private GregorianCalendar date;
	
	private ResultType type;
	
	public Result(ResultType type){
		this.type = type;
	}	
	
	abstract public double doubleValue();
	
	abstract public String valueAsString();
	
	public ResultType getType() {
		return type;
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
