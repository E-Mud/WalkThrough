package org.emud.walkthrough.database;

import java.util.List;

import org.emud.content.Query;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.model.Result;

public class ResultsQuery implements Query<List<Result> > {
	private DateFilter dateFilter;
	private ActivitiesDataSource dataSource;
	private int resultType;
	
	public ResultsQuery(int type, ActivitiesDataSource source,
			DateFilter filter){
		resultType = type;
		dataSource = source;
		dateFilter = filter;
	}
	
	
	@Override
	public List<Result> execute() {
		return dataSource.getResults(resultType, dateFilter.getFromDate(), dateFilter.getToDate());
	}

}
