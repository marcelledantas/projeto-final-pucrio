package pn.Model;


import pn.interSCity.InterSCityData;
import pn.util.Sequencial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Alert implements InterSCityData {
	private long seq;
	private String text;
	private String startTimestamp;
	private String endTimestamp;
	private List<Integer> groups;
	
	/**
	 * Construtor
	 */
	public Alert() {
		this.text = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.startTimestamp = sdf.format(new Date());
		this.endTimestamp = sdf.format(new Date());
		this.groups = new ArrayList<Integer>();
		this.seq = Sequencial.getInstance().getSeq();
	}

	/**
	 * Constructor
	 * @param text
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param groups
	 */
	public Alert(String text, Date startTimestamp, Date endTimestamp, List<Integer> groups) {
		this.text = text;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.startTimestamp = sdf.format(new Date());
		this.endTimestamp = sdf.format(new Date());
		this.groups = groups;
		this.seq = Sequencial.getInstance().getSeq();
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the timestamp
	 */
//	public Date getStartTimestamp() {
//		return startTimestamp;
//	}

	public String getStartTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date());
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setStartTimestamp(Date timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.startTimestamp = sdf.format(timestamp);
	}
	/**
	 * Convert a string with date and time to Date object<br>
	 * An example of the string is 2019-03-30T17:23
	 * @param timestamp
	 * @throws Exception 
	 */
	public void setStartTimestamp(String timestamp) throws Exception {
		this.startTimestamp = timestamp;
	}

	/**
	 * @return the expiresDate
	 */
	public String getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * @param timestamp the expiresDate to set
	 */
	public void setEndTimestamp(Date timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.endTimestamp = sdf.format(timestamp);
	}

	/**
	 * Convert a string with date and time to Date object<br>
	 * An example of the string is 2019-03-30T17:23
	 * @param timestamp
	 * @throws Exception 
	 */
	public void setEndTimestamp(String timestamp) throws Exception {
		this.endTimestamp = timestamp;
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
	 * Assign to a group, without removing previous groups
	 * @param group the group to add to a group list
	 */
	public void addGroup(Integer group) {
		this.groups.add(group);
	}

	/**
	 * @return the seq
	 */
	public long getSeq() {
		return seq;
	}
	
	/**
	 * Set the sequencial number.<br>
	 * This is a read-only attribute, so, the set method is private!!!<br>
	 * @param seq
	 */
	@SuppressWarnings("unused")
	private void setSeq(long seq) {
		this.seq = seq;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	@Override
//	public String toString() {
//		String s = "Alert [seq=" + seq + ", text=" + text + ", startTimestamp="
//				+ startTimestamp + ", endTimestamp=" + endTimestamp
//				+ ", groups=";
//		for(java.lang.Integer group : groups) {
//			s += group.toString() + ",";
//		}
//		s += "]";
//		return s;
//	}
}
