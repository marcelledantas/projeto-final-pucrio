package pn.Model;

public class Integer extends Group {
	/**
	 * serial version due to lac.cnclib.net.groups.Group
	 */
	private static final long serialVersionUID = -5291425600515316112L;
//	private int group;
	private double latitude;
	private double longitude;
	public Integer(int group, double latitude, double longitude) {
		super(1, group);
//		this.group = group;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	/**
	 * Constructor
	 * @param area
	 * @param latitude
	 * @param longitude
	 */
	public Integer(String area, String latitude, String longitude) {
		this(java.lang.Integer.parseInt(area), Double.parseDouble(latitude), Double.parseDouble(longitude));
	}
	/**
	 * @return the group
	 */
	public int getGroup() {
		return super.groupID;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		super.setGroupID(group);
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Group [group=" + super.getGroupID() + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}
}
