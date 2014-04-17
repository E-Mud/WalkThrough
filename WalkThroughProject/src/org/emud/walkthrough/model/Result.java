package org.emud.walkthrough.model;

import android.os.Bundle;

//TODO
public abstract class Result {
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
	
	public abstract Bundle toBundle();
	
	public abstract void fromBundle(Bundle bundle);
	
	public static final int
		RT_MAX_MOVE = 1;

}
