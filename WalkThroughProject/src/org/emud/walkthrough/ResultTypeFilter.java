package org.emud.walkthrough;

import org.emud.content.DataSubject;
import org.emud.content.observer.Subject;
import org.emud.walkthrough.resulttype.ResultType;

public class ResultTypeFilter {
	private ResultType resultType;
	private DataSubject dataSubject;
	
	public ResultTypeFilter(ResultType type){
		resultType = type;
		dataSubject = new DataSubject();
	}

	public Subject getSubject(){
		return dataSubject;
	}
	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType type) {
		if(type != resultType){
			this.resultType = type;
			dataSubject.notifyObservers();
		}
	}
	
	
}
