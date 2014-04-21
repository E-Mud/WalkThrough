package org.emud.walkthrough.analysis;

import org.emud.walkthrough.model.Result;

public interface Analyst {
	
	//TODO
	/**
	 * Realiza un análisis sobre un nuevo conjunto de datos recibidos.
	 * @param walkData Datos recibidos.
	 */
	public void analyzeNewData(WalkData walkData);
	
	//TODO
	/**
	 * Devuelve el resultado obtenido hasta el momento.
	 * @return Resultado obtenido.
	 */
	public Result getResult();
}
