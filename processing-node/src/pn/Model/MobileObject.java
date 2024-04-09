package pn.Model;

import org.json.JSONObject;

import java.util.UUID;

public class MobileObject {
	/** A generic id just to be diplayed on maps<br> */
	private String id;
	private String uuid;
	private double latitude;
	private double longitude;

	public MobileObject(JSONObject jsonObject) {
		this.latitude = jsonObject.getDouble("latitude");
		this.longitude = jsonObject.getDouble("longitude");
		if(jsonObject.has("username")) {
			this.id = jsonObject.getString("username");
		}
		else if(jsonObject.has("uuid")) {
			this.id = jsonObject.getString("uuid");
		}
		else {
			this.id = UUID.randomUUID().toString();
		}
		this.uuid = jsonObject.getString("uuid");
	}

	/**
	 * Constructor<br>
	 * @param id
	 * @param latitude
	 * @param longitude
	 */
	public MobileObject(String id, double latitude, double longitude) {
		this.setId(id);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	/**
	 * Constructor<br>
	 * @param mobileObject
	 */
	public MobileObject(MobileObject mobileObject) {
		this(mobileObject.getId(), mobileObject.getLatitude(), mobileObject.getLongitude());
		this.uuid = mobileObject.getUuid();
	}

	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * 
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @param latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = Double.parseDouble(latitude);
	}

	public double getLongitude() {
		return this.longitude;
	}

	/**
	 * 
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = Double.parseDouble(longitude);
	}
	/**
	 * Gets the generic ID on displayed on map<br>
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the generic ID to be displayed on map<br>
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets a UUID<br>
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets a randomUUID<br>
	 */
	public void setUuid() {
		this.uuid = UUID.randomUUID().toString();
	}
}
