package org.emud.walkthrough.model;


public class User {
	private int wsId;
	private double legLength;
	private String username;
	
	public User(){
		this(-1, null, 0.0d);
	}
	
	public User(String username, double legLength){
		this(-1, username, legLength);
	}
	
	public User(int wsId, String username, double legLength){
		this.wsId = wsId;
		this.username = username;
		this.legLength = legLength;
	}

	/**
	 * @return the wsId
	 */
	public int getWebServiceId() {
		return wsId;
	}

	/**
	 * @param wsId the wsId to set
	 */
	public void setWebServiceId(int wsId) {
		this.wsId = wsId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the legLength
	 */
	public double getLegLength() {
		return legLength;
	}

	/**
	 * @param legLength the legLength to set
	 */
	public void setLegLength(double legLength) {
		this.legLength = legLength;
	}
	
	
}
