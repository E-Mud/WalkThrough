package org.emud.walkthrough;

import org.emud.content.DataSubject;
import org.emud.content.observer.Subject;

public class ResultTypeFilter {
	private int resultType;
	private DataSubject dataSubject;
	
	public ResultTypeFilter(int type){
		resultType = type;
		dataSubject = new DataSubject();
	}

	public Subject getSubject(){
		return dataSubject;
	}
	/**
	 * @return the resultType
	 */
	public int getResultType() {
		return resultType;
	}

	public void setResultType(int type) {
		if(type != resultType){
			this.resultType = type;
			dataSubject.notifyObservers();
		}
	}
	
	
}
