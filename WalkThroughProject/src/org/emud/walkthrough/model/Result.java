package org.emud.walkthrough.model;

import java.util.GregorianCalendar;

import org.emud.walkthrough.resulttype.ResultType;


//TODO
public abstract class Result {
	private long id;
	private GregorianCalendar date;
	private int webId;
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

	/**
	 * @return the webId
	 */
	public int getWebId() {
		return webId;
	}

	/**
	 * @param webId the webId to set
	 */
	public void setWebId(int webId) {
		this.webId = webId;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	
}
