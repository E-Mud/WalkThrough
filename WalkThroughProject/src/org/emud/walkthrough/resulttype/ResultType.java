package org.emud.walkthrough.resulttype;


public enum ResultType {
	RT_STEPS(0), RT_SPEED(2), RT_REGULARITY(1), RT_CADENCE(3), RT_LENGTH(4);

	private int value;
	private static ResultToolsProvider toolsProvider = new ResultToolsProvider();
	
	private ResultType(int v){
		value = v;
	}
	
	public int intValue(){
		return value;
	}
	
	public ResultFactory getFactory(){
		return toolsProvider.getResultFactory(this);
	}
	
	public ResultGUIResolver getGUIResolver(){
		return toolsProvider.getGUIResolver(this);
	}
	
	public static ResultType valueOf(int v){
		switch(v){
		case 0:
			return RT_STEPS;
		case 1:
			return RT_REGULARITY;
		case 2:
			return RT_SPEED;
		case 3:
			return RT_CADENCE;
		case 4:
			return RT_LENGTH;
		default:
			return null;
		}
	}
	
	/*@Override
	public String toString(){
		switch(this){
		case RT_MAX_MOVE:
			return "Aceleración máxima";
		case RT_SPEED:
			return "Velocidad";
		case RT_STEPS:
			return "Pasos";
		case RT_CADENCE:
			return "Ritmo";
		default:
			return null;
		}
	}*/
}
