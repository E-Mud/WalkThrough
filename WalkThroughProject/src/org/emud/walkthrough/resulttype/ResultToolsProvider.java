package org.emud.walkthrough.resulttype;

import org.emud.walkthrough.cadence.CadenceFactory;
import org.emud.walkthrough.cadence.CadenceGUI;
import org.emud.walkthrough.pedometer.StepsCountFactory;
import org.emud.walkthrough.pedometer.StepsGUI;
import org.emud.walkthrough.speedometer.SpeedFactory;
import org.emud.walkthrough.speedometer.SpeedGUI;
import org.emud.walkthrough.stub.MaxMoveGUI;
import org.emud.walkthrough.stub.ResultMaxMoveFactory;

import android.util.SparseArray;

public class ResultToolsProvider {
	private SparseArray<ResultFactory> factories;
	private SparseArray<ResultGUIResolver> guiResolvers;
	
	public ResultToolsProvider(){
		factories = new SparseArray<ResultFactory>();
		guiResolvers = new SparseArray<ResultGUIResolver>();
	}

	/**
	 * Devuelve el ResultFactory apropiado para resultados de un tipo determinado.
	 * @param resultType Tipo del resultado
	 * @return ResultFactory para el tipo indicado o null si el tipo es incorrecto.
	 */
	public ResultFactory getResultFactory(ResultType resultType){
		ResultFactory factory = factories.get(resultType.intValue());
		
		if(factory == null){
			factory = buildResultFactory(resultType);
			if(factory == null){
				return null;
			}else{
				factories.put(resultType.intValue(), factory);
			}
		}
		
		return factory;
	}
	
	
	private ResultFactory buildResultFactory(ResultType resultType) {
		switch(resultType){
		case RT_MAX_MOVE:
			return new ResultMaxMoveFactory();
		case RT_STEPS:
			return new StepsCountFactory();
		case RT_SPEED:
			return new SpeedFactory();
		case RT_CADENCE:
			return new CadenceFactory();
		default: return null;
		}
	}
	
	public ResultGUIResolver getGUIResolver(ResultType resultType) {
		ResultGUIResolver resolver = guiResolvers.get(resultType.intValue());
		
		if(resolver == null){
			resolver = buildGUIResolver(resultType);
			if(resolver == null){
				return null;
			}else{
				guiResolvers.put(resultType.intValue(), resolver);
			}
		}
		
		return resolver;
	}

	private ResultGUIResolver buildGUIResolver(ResultType resultType) {

		switch(resultType){
		case RT_MAX_MOVE:
			return new MaxMoveGUI();
		case RT_STEPS:
			return new StepsGUI();
		case RT_SPEED:
			return new SpeedGUI();
		case RT_CADENCE:
			return new CadenceGUI();
		default: return null;
		}
	}
}
