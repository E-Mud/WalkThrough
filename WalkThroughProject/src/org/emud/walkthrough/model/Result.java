package org.emud.walkthrough.model;

import java.util.GregorianCalendar;


//TODO
public abstract class Result {
	private GregorianCalendar date;
	
	private Object result;
	private int type;
	
	public Result(int type){
		this(null, type);
	}	

	public Result(Object result, int type){
		this.result = result;
		this.type = type;
	}
	
	public Object get(){
		return this.result;
	}
	
	public void set(Object res){
		result = res;
	}
	
	public int getType() {
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
	
	public static final int
		RT_STEPS = 0,
		RT_MAX_MOVE = 1,
		RT_SPEED = 2;

}
