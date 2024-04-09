package servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pn.Model.Alert;
import pn.Model.Group;
import pn.Model.Integer;
import pn.connection.HTTPException;
import pn.interSCity.InterSCity;
import pn.util.Debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet implementation class PutAlert
 */
@WebServlet(description = "Insert a new Alert in InterSCity platform", urlPatterns = { "/PutAlert" })
public class PutAlert extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String responseMessage;
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// just call doGet
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	ServletContext application = getServletContext();
    	/** ContextNet IP address */
//		String gatewayIP = application.getInitParameter("gatewayIP");
		/** Contextnet UDP port */
//		int gatewayPort = Integer.parseInt(application.getInitParameter("gatewayPort"));
		/** InterSCity ip address */
    	String interSCityIPAddress;
		InterSCity interSCity;
    	
    	if((interSCityIPAddress = System.getenv("REGIONALERT_INTERSCITYIPADDRESS")) == null) {
    		interSCityIPAddress = application.getInitParameter("interSCityIPAddress");
    	}
		try {
			interSCity = new InterSCity(interSCityIPAddress);
		}
		catch (Exception e) {
			interSCity = null;
			e.printStackTrace();
		}

		Alert alert = new Alert();
		
		// text
		alert.setText(request.getParameter("text"));
		
		// start date
		try {
			alert.setStartTimestamp(request.getParameter("startTimestamp"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// end date
		if(request.getParameter("endTimestamp") != null) {
			try {
				alert.setEndTimestamp(request.getParameter("endTimestamp"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// convert minutes to milliseconds and add to the start date in milliseconds,
			// then, convert to Date and set endTimestamp
			DateFormat format = new SimpleDateFormat("");
			Date date;
			try {
				date = ((Date) format.parse(alert.getStartTimestamp()));
				alert.setEndTimestamp(new Date(date.getTime() + java.lang.Integer.parseInt(request.getParameter("deltaTime"))* 60 * 1000));
			} catch (ParseException e) {
				Debug.warning("Wrong date format at date " + alert.getStartTimestamp(), e);
			}			
		}

		String[] areasArray = request.getParameterValues("areas");
		// area, latitude & longitude
		if (areasArray != null){
			for(String aux : request.getParameterValues("areas")) {
				// area, latitude & longitude came in a single HTTP parameter separated by #
				String[] areaLatLon = aux.split("#");
				Integer group = new Integer(areaLatLon[0], areaLatLon[1], areaLatLon[2]);
				alert.addGroup(group);
			}
		}
		responseMessage = "Data stored in InterSCity: " + alert;
		
		try {
			interSCity.updateDB(alert);
		} catch (Exception e) {
			Debug.warning(" created", e);
			responseMessage = "Alert not created. Is InterSCity UP at " + interSCity.getIpAddress() + "?";
		}
		
		List<String> areas = new ArrayList<String>();
		for(Group group : alert.getGroups()) {
			areas.add("" + group.getGroupID());
		}
		try {
			interSCity.sendActuatorCommand("alertListener", areas.toArray(new String[areas.size()]));
		} catch (HTTPException e) {
			responseMessage += " but could not publish that there are new alerts";
		}

		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		out.println(responseMessage);
	}
}
