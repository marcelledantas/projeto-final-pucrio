package pn.interSCity;

import org.json.JSONArray;
import org.json.JSONObject;
import pn.Model.Alert;
import pn.Model.Integer;
import pn.Model.Person;
import pn.connection.HTTPConnection;
import pn.connection.HTTPException;
import pn.util.Debug;
import pn.util.Sequencial;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

/**
 * 
 * @author meslin
 *
 */
public class InterSCity {
	private HTTPConnection connection;
//	private Map<String, String> ordemUUIDMap;

	/**
	 * Constructor
	 * @param connection an HTTP connection to the InterSCity platform
	 */
	public InterSCity(HTTPConnection connection) {
		this.connection = connection;
//		ordemUUIDMap = new HashMap<String, String>();
	}
	
	/**
	 * Constructor<br>
	 * @param interSCityIPAddress
	 * @throws Exception 
	 */
	public InterSCity(String interSCityIPAddress) throws Exception {
//		this(new HTTPConnection(interSCityIPAddress));
	}

	public InterSCity(){

	}
	
	/**
	 * Creates a new resource<br>
	 * Register new resources<br>
	 * DIR: /adaptor/resources<br>
	 * Method: POST<br>
	 * Includes a dummy capability for workaround for the one postion JSON array bug - capability not used<br>
	 *<br>
	 * interSCity data:<br>
	 *  {<br>
	 *  	"data": {<br> 
	 *  		"description": "A city alert",<br> 
	 *  		"capabilities": [<br>
	 *  			"city_alerting"
	 *  		],<br> 
	 *  		"status": "active",<br> 
	 *  		"lat": -23.559616,<br> 
	 *  		"lon": -46.731386<br> 
	 *  	}<br> 
	 *  }<br>
	 *<br>
	 * InterSCity answer:<br>
	 * 	{<br>
  	 *		"data": {<br>
     *			"uuid": "45b7d363-86fd-4f81-8681-663140b318d4",<br>
     *			"description": "A city alert",<br>
     *			"capabilities": [<br>
	 *  			"ordem",<br>
	 *  			"linha",<br> 
	 *  			"velocidade" <br>
     *			],<br>
     *  		"status": "active",<br>
     *  		"lat": -23.559616,<br>
     *  		"lon": -46.731386,<br>
     *  		"country": "Brazil",<br>
     *  		"state": "São Paulo",<br>
     *  		"city": "São Paulo",<br>
     *  		"neighborhood": "Butantã",<br>
     *  		"postal_code": null,<br>
     *  		"created_at": "2017-12-27T13:25:07.176Z",<br>
     *  		"updated_at": "2017-12-27T13:25:07.176Z",<br>
     *  		"id": 10<br>
     * 		}<br>
     * 	}<br>
     * 
	 * @param description the resource description
	 * @param capabilities the resource capability list
	 * @param lat the resource latitude
	 * @param lon the resource longitude
	 * @return uuid the new resource String
	 * @throws Exception
	 */
	public String createNewResource(String description, String[] capabilities, double lat, double lon) throws Exception {
		String uuid =null;
		String response = null;

		// create a new resource
		JSONObject data = new JSONObject();
		data.put("description", description);
		for(String capability : capabilities) {
			data.accumulate("capabilities", capability);
		}
		data.accumulate("capabilities", "fake");	// workaround for the one postion JSON array bug - capability not used
		data.put("status", "active");
		data.put("lat", lat);
		data.put("lon", lon);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", data);
		System.out.println("Jsondata" + jsonObject.toString());

		try {
			//mocked response
			response = "{"
					+ "\"data\": {"
					+ "\"uuid\": \"45b7d363-86fd-4f81-8681-663140b318d4\","
					+ "\"description\": \"A city alert\","
					+ "\"capabilities\": [\"ordem\",\"linha\",\"velocidade\"],"
					+ "\"status\": \"active\","
					+ "\"lat\": -23.559616,"
					+ "\"lon\": -46.731386,"
					+ "\"country\": \"Brazil\","
					+ "\"state\": \"São Paulo\","
					+ "\"city\": \"São Paulo\","
					+ "\"neighborhood\": \"Butantã\","
					+ "\"postal_code\": null,"
					+ "\"created_at\": \"2017-12-27T13:25:07.176Z\","
					+ "\"updated_at\": \"2017-12-27T13:25:07.176Z\","
					+ "\"id\": 10"
					+ "}"
					+ "}";

			//response = connection.sendPost("catalog/resources", jsonObject.toString());

		} catch (Exception e) {
			Debug.error("resource not created", e);
			throw new Exception(e.getMessage());
		}

		jsonObject = new JSONObject(response);
		uuid = jsonObject.getJSONObject("data").getString("uuid");

		if(uuid == null) {
			Debug.warning("Null UUID for " + jsonObject.toString());
		}
		return uuid;
	}
	
	/**
	 * Creates a new resource<br>
	 * Register new resources<br>
	 * DIR: /adaptor/resources<br>
	 * Method: POST<br>
	 * Includes a dummy capability for workaround for the one postion JSON array bug - capability not used<br>
	 *<br>
	 * interSCity data:<br>
	 *  {<br>
	 *  	"data": {<br> 
	 *  		"description": "A city alert",<br> 
	 *  		"capabilities": [<br>
	 *  			"city_alerting"
	 *  		],<br> 
	 *  		"status": "active",<br> 
	 *  		"lat": -23.559616,<br> 
	 *  		"lon": -46.731386<br> 
	 *  	}<br> 
	 *  }<br>
	 *<br>
	 * InterSCity answer:<br>
	 * 	{<br>
  	 *		"data": {<br>
     *			"uuid": "45b7d363-86fd-4f81-8681-663140b318d4",<br>
     *			"description": "A city alert",<br>
     *			"capabilities": [<br>
	 *  			"ordem",<br>
	 *  			"linha",<br> 
	 *  			"velocidade" <br>
     *			],<br>
     *  		"status": "active",<br>
     *  		"lat": -23.559616,<br>
     *  		"lon": -46.731386,<br>
     *  		"country": "Brazil",<br>
     *  		"state": "São Paulo",<br>
     *  		"city": "São Paulo",<br>
     *  		"neighborhood": "Butantã",<br>
     *  		"postal_code": null,<br>
     *  		"created_at": "2017-12-27T13:25:07.176Z",<br>
     *  		"updated_at": "2017-12-27T13:25:07.176Z",<br>
     *  		"id": 10<br>
     * 		}<br>
     * 	}<br>
     * 
	 * @param description the resource description
	 * @param capability the resource capability
	 * @param lat the resource latitude
	 * @param lon the resource longitude
	 * @return uuid the new resource String
	 * @throws Exception
	 */
	public String createNewResource(String description, String capability, double lat, double lon) throws Exception {
		return createNewResource(description, new String[]{capability}, lat, lon);
	}

	/**
	 * Creates a new resource<br>
	 * Register new resources<br>
	 * /adaptor/resources<br>
	 * Method: POST<br>
     *<br>
	 * @param interSCityData
	 * @param group
	 * @return UUID String
	 * @throws Exception 
	 */
	public String createNewResource(InterSCityData interSCityData, Integer group) throws Exception {
		if(interSCityData instanceof Alert) {
			// create a new Alert
			return createNewResource("An alert", "city_alerting", group.getLatitude(), group.getLongitude());
		}
		else if(interSCityData instanceof Person) {
			// create a new Person
			return createNewResource("A person", "person", ((Person)interSCityData).getLatitude(), ((Person)interSCityData).getLongitude());
		}
		else {
			Debug.warning("unknown resource type to create");
			return null;
		}
	}

	/**
	 * Update InterSCity database<br>
	 * InterSCity commands<br>
	 * <ul>
	 * <li>
	 * <li>curl -X GET  "http://localhost:8000/catalog/capabilities"
	 * <li>curl -X POST "http://localhost:8000/adaptor/resources/" + uuid + "/data" -H "Content-Type: application/json" -d jsonobject
	 * </ul>
	 * @param interSCityData
	 * @throws Exception 
	 */
	public void updateDB(InterSCityData interSCityData) throws Exception {
		String uuid = null;
		@SuppressWarnings("unused")
		String response = null;

		if(interSCityData instanceof Alert) {
			Alert alert = (Alert) interSCityData;
			JSONArray cityAlerting = new JSONArray();
			for(Integer group : alert.getGroups()) {
				uuid = createNewResource(interSCityData, group);
				if(uuid == null) {
					Debug.warning("Resource alert not created");
					return;
				}

				// if everything is ok, create the alert in this format
				// an alert for every group (area)
				/*
				 * {
				 * 		"data": {
				 * 			"city_alerting": [
				 * 				{
				 * 					"seq": 123,
				 * 					"text": "Danger! Danger! Danger!",
				 * 					"timestamp": "2017-06-14T17:52:25.428Z",
				 * 					"endTimestamp": "2017-06-14T19:52:25.428Z",
				 * 					"group": 12,
				 * 					"lat": -12,
				 * 					"lon": -32
				 * 				}, ...
				 * 				...
				 * 			]
				 * 		}
				 * }
				 */

				// create the JSON string
				JSONObject capabilities = new JSONObject();
				capabilities.put("seq", Sequencial.getInstance().getSeq());
				capabilities.put("text", alert.getText());
				capabilities.put("endTimestamp", alert.getEndTimestamp());
				capabilities.put("group", group.getGroup());
				capabilities.put("timestamp", alert.getStartTimestamp());
				cityAlerting.put(capabilities);
			}
			JSONObject data = new JSONObject();
			data.put("city_alerting", cityAlerting);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("data", data);
			String jsonString = jsonObject.toString(2).replace("[[", "[").replace("]]", "]");
//			connection = new HTTPConnection();
//			try {
//				response = connection.sendPost("adaptor/resources/" + uuid + "/data", jsonString);
//			} catch (IOException | HTTPException e) {
//				Debug.warning("Could not update resource\n" + jsonString, e) ;
//			}
		}
		else if(interSCityData instanceof Person) {
			// TODO InterSCity received an instance of PERSON! Do something (I don't know what to do by now!!!)
		}
	}


	/**
	 * Checks the InterSCity:<br>
	 * <ul>
	 * <li>Availability</li>
	 * <li>Existence of the required capabilities (creates if not available)</li>
	 * </ul>
	 * InterSCity commands:
	 * <ul>
	 * <li>curl -X GET  "http://localhost:8000/catalog/capabilities"</li>
	 * <li>curl -X POST "http://localhost:8000/catalog/capabilities" -H "Content-Type: application/json" -d '{"name": "city_alerting", "description": "City alert", "capability_type": "sensor"}'</li>
	 * </ul>
	 * @throws HTTPException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public void checkInterSCity() throws MalformedURLException, IOException, HTTPException {
		String response;
		Boolean found;
		
		// the availability is checked using the services
		
		// check for the existence of the city_alerting capability
		//connection = new HTTPConnection();
		try {
			response = "{"
					+ "\"name\": \"city_alerting\","
					+ "\"description\": \"City alert\","
					+ "\"capability_type\": \"sensor\","
					+ "\"capabilities\": ["
					+ "{\"name\": \"ordem\"},"
					+ "{\"name\": \"linha\"},"
					+ "{\"name\": \"velocidade\"}"
					+ "]"
					+ "}";

//			response = connection.sendGet("catalog/capabilities", "");

		} catch (Exception e) {
			Debug.warning("Error while checking InterSCity at " + connection.getIpAddress() + " connection (is it up?)", e);
			return;
		}
		
		if(response == null) return;
		
		JSONArray capabilities = new JSONObject(response).getJSONArray("capabilities");;
//		try {
//			 capabilities = new JSONObject(response).getJSONArray("capabilities");
//		} catch (JSONException e) {
//			Debug.warning("Error while parsing the JSON response", e);
//			return;
//		}

		// check for alert capability
		found = false;
		for(int i=0; i<capabilities.length(); i++) {
			if(((String)((JSONObject)capabilities.get(i)).get("name")).equals("city_alerting")) {
				found = true;
				break;
			}
		}
		if(!found) {
			// alert capability not found, need to be created now!
			Debug.info("Capability alert not found, creating one");
			String jsonString = "{ \"name\": \"city_alerting\", \"description\": \"City alert\", \"capability_type\": \"sensor\" }";
			Debug.warning("jsonString = " + jsonString);
//			response = connection.sendPost("catalog/capabilities", jsonString);
		}
		else {
			Debug.info("Capability alert found!");
		}

		// check for person capability
		found = false;
		for(int i=0; i<capabilities.length(); i++) {
			if(((String)((JSONObject)capabilities.get(i)).get("name")).equals("person")) {
				found = true;
				break;
			}
		}
		if(!found) {
			// person capability not found, need to be created now!
			Debug.info("Capability person not found, creating one");
			String data = "{ \"name\": \"person\", \"description\": \"Person\", \"capability_type\": \"sensor\" }";
//			response = connection.sendPost("catalog/capabilities", data);
		}
		else {
			Debug.info("Capability person found!");
		}

		// check for alertListener capability
		found = false;
		for(int i=0; i<capabilities.length(); i++) {
			if(((String)((JSONObject)capabilities.get(i)).get("name")).equals("alertListener")) {
				found = true;
				break;
			}
		}
		if(!found) {
			// alertListener capability not found, need to be created now!
			Debug.info("Capability alertListener not found, creating one");
			String data = "{ \"name\": \"alertListener\", \"description\": \"alertListener\", \"capability_type\": \"actuator\" }";
//			response = connection.sendPost("catalog/capabilities", data);
		}
		else {
			Debug.info("Capability alertListener found!");
		}

		// check for fake capability
		found = false;
		for(int i=0; i<capabilities.length(); i++) {
			if(((String)((JSONObject)capabilities.get(i)).get("name")).equals("fake")) {
				found = true;
				break;
			}
		}
		if(!found) {
			// fake capability not found, need to be created now!
			Debug.info("Capability fake not found, creating one");
			String data = "{ \"name\": \"fake\", \"description\": \"fake capability (workaround)\", \"capability_type\": \"actuator\" }";
//			response = connection.sendPost("catalog/capabilities", data);
		}
		else {
			Debug.info("Capability fake found!");
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getIpAddress() {
		return connection.getIpAddress();
	}
	
	/**
	 * Lookup InterSCity for alerts by areas<br>
	 * @param groups
	 * @return
	 */
	public List<Alert> getAlertListByArea(HashSet<java.lang.Integer> groups) {
		// no areas => nothing to do, just return
		if(groups == null || groups.size() == 0) return null;
		List<java.lang.Integer> areas = new ArrayList<java.lang.Integer>();
		for(int area : groups) {
			areas.add(area);
		}
		return getAlertListByArea(areas);
	}
	/**
	 * Lookup InterSCity for alerts by areas<br>
	 * @param areas area List
	 * @return an Alert List
	 */
	public List<Alert> getAlertListByArea(List<java.lang.Integer> areas) {
		// no areas => nothing to do, just return
		if(areas == null || areas.size() == 0) return null;

		String response;
		List<Alert> alerts = new ArrayList<Alert>();

		String qs = "capability=city_alerting";
		for(int area : areas) {
			qs += "&group.in[]=" + area;
		}

		/*
		 * Connection: alert:8000/discovery/resources?capability=city_alerting&group.in[]=73&group.in[]=60
		 * Response:
		 * {
		 *     "resources": [
		 *         {
		 *             "id": 10,
		 *             "uri": null,
		 *             "created_at": "2019-03-15T19:14:22.333Z",
		 *             "updated_at": "2019-03-15T19:14:22.333Z",
		 *             "lat": -22.9631480868568,
		 *             "lon": -43.3693979286854,
		 *             "status": "active",
		 *             "collect_interval": null,
		 *             "description": "An alert",
		 *             "uuid": "3a4acbf2-3e60-4a4e-a27e-a0072ff2ce87",
		 *             "city": null,
		 *             "neighborhood": null,
		 *             "state": null,
		 *             "postal_code": null,
		 *             "country": null,
		 *             "capabilities": [
		 *                 "city_alerting",
		 *                 "fake"
		 *             ]
		 *         },...
		 *         ...
		 *     ]
		 * }
		 */

		try {
			Debug.info("Getting sensors using discovery/resources?" + qs);
//			response = connection.sendGet("discovery/resources", qs);
			// Mocking the response
			response = "{"
					+ "\"resources\": ["
					+ "{"
					+ "\"uuid\": \"12a36493-eddc-4fd6-bc3f-903f4458e2a3\","
					+ "\"capabilities\": {"
					+ "\"city_alerting\": ["
					+ "{"
					+ "\"text\": \"Olá Mobile Node!\","
					+ "\"endTimestamp\": \"Sun Mar 31 14:02:00 BRT 2019\","
					+ "\"seq\": 6,"
					+ "\"group\":89,"
					+ "\"date\": \"2019-03-15T19:10:24.809Z\""
					+ "}"
					+ "]"
					+ "}"
					+ "}"
					+ "]"
					+ "}";
		} catch (Exception e) {
			Debug.warning("Could not get resources by area: ", e);
			return null;
		}


		/*
		 * This is an example of response:
		 * {
		 *     "resources": [
		 *         {
		 *             "uuid": "12a36493-eddc-4fd6-bc3f-903f4458e2a3",
		 *             "capabilities": {
		 *                 "city_alerting": [
		 *                     {
		 *                         "text": "Danger at Grajau!",
		 *                         "endTimestamp": "Sun Mar 31 14:02:00 BRT 2019",
		 *                         "seq": 6,
		 *                         "group": 60,
		 *                         "date": "2019-03-15T19:10:24.809Z"
		 *                     }
		 *                 ]
		 *             }
		 *         }
		 *     ]
		 * }
		 */
		JSONObject jsonResources = new JSONObject(response).getJSONArray("resources").getJSONObject(0);
		Debug.info("There are " + jsonResources.length() + " resources with alert for this user\nbecause the answer was " + response);
		List<Integer> groupList = new ArrayList<>();
		for(int i=0; i< jsonResources.length(); i++) {
//			JSONObject jsonResource = getResourceDataByUUID(jsonResources.getJSONObject(i).getString("uuid"));
			Alert alert = new Alert();
			try {
				// this came from the previous query
//				alert.setStartTimestamp(jsonResources.getJSONObject(i).getString("updated_at"));
				// and these came from this query
//				JSONArray resources = jsonResources.getJSONArray("resources");
				alert.setEndTimestamp(jsonResources.getJSONObject("capabilities").getJSONArray("city_alerting").getJSONObject(0).getString("endTimestamp"));
				alert.setText(jsonResources.getJSONObject("capabilities").getJSONArray("city_alerting").getJSONObject(0).getString("text"));
				int group = jsonResources // get the first object in the resources array
						.getJSONObject("capabilities")
						.getJSONArray("city_alerting")
						.getJSONObject(0)
						.getInt("group");
				groupList.add(new Integer(group, 10,10));
				alert.setGroups(groupList);
				alerts.add(alert);
			} catch (Exception e) {
				Debug.warning("Unknown error (I do NOT know how to react to it)", e);
			}
		}

		return alerts;
	}
	
	/** Mock
	 * Returns the resource data by UUID
	 * @param uuid
	 * @return the resource data in JSON format or null in case of error
	 */
	private JSONObject getResourceDataByUUID(String uuid) {
		JSONObject response = new JSONObject();
		try {
			response.put("uuid", uuid);
		} catch (Exception e) {
			Debug.warning("Could not get resource data by UUID " + uuid, e);
			return null;
		}
		return response;
	}

	/**
	 * Returns the resource data by UUID
	 * @param uuid
	 * @return the resource data in JSON format or null in case of error
	 */
//	private JSONObject getResourceDataByUUID(String uuid) {
//		JSONObject response;
//		try {
//			response = new JSONObject(connection.sendGet("collector/resources/" + uuid + "/data", ""));
//		} catch (Exception e) {
//			Debug.warning("Could not get resource data by UUID " + uuid, e);
//			return null;
//		}
//		return response;
//	}

	/**
	 * Subscribe to receive actuation commands<br>
	 * @param capabilities
	 * @param url
	 * @throws HTTPException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public void subscribe(String uuid, String[] capabilities, String url) throws MalformedURLException, IOException, HTTPException {
		JSONObject subscription = new JSONObject();
		subscription.put("uuid", uuid);
		for(String capability : capabilities) {
			subscription.accumulate("capabilities", capability);			
		}
		// add a dummy capability to correct JSONObject bug when creating a single element array
		subscription.accumulate("capabilities", "fake");
		subscription.put("url", url);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("subscription", subscription);
		
		// subscribe
//		HTTPConnection connection = new HTTPConnection();
		//connection.sendPost("adaptor/subscriptions", jsonObject.toString()); TODO: original descomentado
	}
	public void sendActuatorCommand(String capability, String[] commands) throws MalformedURLException, IOException, HTTPException {
		/*
		 * Publish the information that there are new alerts.
		 * The command has the following JSON format:
		 * {
		 *   "data": [
		 *     {
		 *       "uuid": "b0ae6f76-521d-4199-9595-f52c99361052",
		 *       "capabilities": {
		 *         "alertListener": <command>,
		 *         "illuminate": null
		 *       }
		 *     }
		 *   ]
		 * }
		 */
		// capabilities
		JSONObject capabilities = new JSONObject();
		for(String command : commands) {
			capabilities.accumulate(capability, command);			
		}
		// command
		JSONObject command = new JSONObject();
		command.put("uuid", UUID.randomUUID().toString());
		command.put("capabilities", capabilities);
		// data
		JSONArray data = new JSONArray();
		data.put(0, command);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", data);
		
//		HTTPConnection connection = new HTTPConnection();
//		connection.sendPost("actuator/commands", jsonObject.toString()); TODO: original descomentado
	}
	
	/**
	 * Discover alert listeners near lat and lon<br>
	 * @param lat
	 * @param lon
	 * @return UUID list
	 * @throws Exception 
	 */
	public String[] alertListenerDiscover(double lat, double lon) throws Exception {
		String response;
//		response = connection.sendGet("discovery/resources", "capability=alertListener&lat=" + lat + "&lon=" + lon); //TODO: Como estava antes
//		response = connection.sendGet("discovery/resources", "capability=alertListener");
		response = "{"
				+ "\"resources\": ["
				+ "{"
				+ "\"uuid\": \"45b7d363-86fd-4f81-8681-663140b318d4\","
				+ "\"description\": \"A public bus\","
				+ "\"capabilities\": [\"temperature\",\"humidity\",\"illuminate\"],"
				+ "\"status\": \"active\","
				+ "\"lat\": -23.559616,"
				+ "\"lon\": -46.731386,"
				+ "\"country\": \"Brazil\","
				+ "\"state\": \"São Paulo\","
				+ "\"city\": \"São Paulo\","
				+ "\"neighborhood\": \"Butantã\","
				+ "\"postal_code\": null,"
				+ "\"created_at\": \"2023-10-27T21:58:43.750Z\","
				+ "\"updated_at\": \"2023-10-27T21:58:43.750Z\","
				+ "\"id\": 10"
				+ "}"
				+ "]"
				+ "}";

		JSONArray jsonResources = (new JSONObject(response)).getJSONArray("resources");
		List<String> uuids = new ArrayList<String>();
		
		for(int i=0; i<jsonResources.length(); i++) {
			uuids.add(jsonResources.getJSONObject(i).getString("uuid"));
		}
		return uuids.toArray(new String[0]);
	}
}
