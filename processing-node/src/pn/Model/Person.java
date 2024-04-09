package pn.Model;


import pn.interSCity.InterSCityData;

import java.util.List;
import java.util.UUID;

/**
 * 
 * @author meslin
 *
 */
public class Person extends MobileObject implements InterSCityData {
	private String name;
	private List<Integer> groups;
	
	/**
	 * Constructor
	 * @param name the person's name
	 * @param latitude
	 * @param longitude
	 */
	public Person(String name, double latitude, double longitude) {
		super(name, latitude, longitude);
	}

	/**
	 * Constructor
	 * @param name the person's name
	 * @param uuid
	 * @param groups
	 */
	public Person(String name, UUID uuid, List<Integer> groups) {
		super(name, 0, 0);
		this.groups = groups;
	}

	/**
	 * @return the groups
	 */
	public List<Integer> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<Integer> groups) {
		this.groups = groups;
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
}
