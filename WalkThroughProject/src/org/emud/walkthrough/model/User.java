package org.emud.walkthrough.model;

import java.util.GregorianCalendar;

public class User {
	int wsId, height, gender;
	double weight;
	String username, name, lastname;
	GregorianCalendar borndate;
	
	public User(){
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
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the gender
	 */
	public int getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the borndate
	 */
	public GregorianCalendar getBorndate() {
		return borndate;
	}

	/**
	 * @param borndate the borndate to set
	 */
	public void setBorndate(GregorianCalendar borndate) {
		this.borndate = borndate;
	}
}
