package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.Query;
import org.emud.walkthrough.model.Result;

import android.database.Cursor;

public class ResultsQuery implements Query<List<Result> > {
	private GregorianCalendar fromDate, toDate;
	private ActivitiesDataSource dataSource;
	private int resultType;
	
	public ResultsQuery(int type, ActivitiesDataSource dataSource){
		this(type, dataSource, null, null);
	}
	
	public ResultsQuery(int type, ActivitiesDataSource source, GregorianCalendar from, GregorianCalendar to){
		resultType = type;
		dataSource = source;
		fromDate = from;
		toDate = to;
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
	}
	
	@Override
	public List<Result> execute() {
		return dataSource.getResults(resultType, fromDate, toDate);
	}

}
