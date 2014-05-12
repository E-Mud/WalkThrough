package org.emud.walkthrough.database;

import java.util.List;

import org.emud.content.Query;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.ResultTypeFilter;
import org.emud.walkthrough.model.Result;

public class ResultsQuery implements Query<List<Result> > {
	private DateFilter dateFilter;
	private ActivitiesDataSource dataSource;
	private ResultTypeFilter resultTypeFilter;
	
	public ResultsQuery(ResultTypeFilter resultType, ActivitiesDataSource source,
			DateFilter filter){
		resultTypeFilter = resultType;
		dataSource = source;
		dateFilter = filter;
	}
	
	
	@Override
	public List<Result> execute() {
		return dataSource.getResults(resultTypeFilter.getResultType(), dateFilter.getFromDate(), dateFilter.getToDate());
	}

}
