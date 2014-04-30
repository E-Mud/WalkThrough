package org.emud.walkthrough;

import org.emud.walkthrough.model.Result;
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
	public ResultFactory getResultFactory(int resultType){
		ResultFactory factory = factories.get(resultType);
		
		if(factory == null){
			factory = buildResultFactory(resultType);
			if(factory == null){
				return null;
			}else{
				factories.put(resultType, factory);
			}
		}
		
		return factory;
	}
	
	
	private ResultFactory buildResultFactory(int resultType) {
		switch(resultType){
		case Result.RT_MAX_MOVE:
			return new ResultMaxMoveFactory();
		default: return null;
		}
	}
	
	public ResultGUIResolver getGUIResolver(int resultType) {
		ResultGUIResolver resolver = guiResolvers.get(resultType);
		
		if(resolver == null){
			resolver = buildGUIResolver(resultType);
			if(resolver == null){
				return null;
			}else{
				guiResolvers.put(resultType, resolver);
			}
		}
		
		return resolver;
	}

	private ResultGUIResolver buildGUIResolver(int resultType) {

		switch(resultType){
		case Result.RT_MAX_MOVE:
			return new MaxMoveGUI();
		default: return null;
		}
	}
}
