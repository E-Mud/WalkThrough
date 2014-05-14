package org.emud.walkthrough.fallingdetection;

import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.Analyst;

public class FallingStationBuilder extends AnalysisStationBuilder {
	private OnFallDetectedListener listener;
	
	public FallingStationBuilder(OnFallDetectedListener lstn){
		listener = lstn;
	}

	@Override
	public Analyst buildAnalyst(int resultType, int receiverType) {
		return new FallingAnalyst(listener);
	}

}
