package org.emud.walkthrough.model;

//TODO
public class Result {
	private Object result;
	
	public Result(Object result){
		this.result = result;
	}
	
	public Object get(){
		return this.result;
	}
	
	public enum ResultType{
		MAX_MOVE
	}

}
