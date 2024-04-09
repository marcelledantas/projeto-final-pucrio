/**
 *
 */
package pn.Model;

import java.util.HashSet;
import java.util.List;

/**
 * @author meslin
 *
 */
public class PersonSituation extends MobileObject {
	/** this is new person? */
	private boolean isNewPerson;
	/** the new groups of this person */
	private HashSet<java.lang.Integer> difGroups;

	/**
	 * Constructor
	 * @param mobileObject
	 * @param isNewPerson
	 * @param difGroups
	 */
	public PersonSituation(MobileObject mobileObject, Boolean isNewPerson, HashSet<java.lang.Integer> difGroups) {
		super(mobileObject);
		this.isNewPerson = isNewPerson;
		this.difGroups = difGroups;
	}

	/**
	 * @return the difGroups
	 */
	public HashSet<java.lang.Integer> getDifGroups() {
		return difGroups;
	}

	/**
	 * Adds a group to a difGroup<br>
	 * A difGroup is a group set with only the new groups<br>
	 * @param difGroups
	 */
	public void addToDifGroups(HashSet<java.lang.Integer> difGroups) {
		this.difGroups.addAll(difGroups);
	}

	/**
	 * Adds a group to a difGroup<br>
	 * A difGroup is a group set with only the new groups<br>
	 * @param areas
	 */
	public void addToDifGroups(List<java.lang.Integer> areas) {
		this.difGroups.addAll(areas);
	}

	/**
	 * @param difGroups the difGroups to set
	 */
	public void setDifGroups(HashSet<java.lang.Integer> difGroups) {
		this.difGroups = difGroups;
	}

	/**
	 * @return the isNewPerson
	 */
	public boolean isNewPerson() {
		return isNewPerson;
	}

	/**
	 * @param isNewPerson the isNewPerson to set
	 */
	public void setNewPerson(boolean isNewPerson) {
		this.isNewPerson = isNewPerson;
	}
}
