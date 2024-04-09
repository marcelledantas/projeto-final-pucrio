package pn.thread;


import pn.Model.Alert;
import pn.Model.PersonSituation;
import pn.contextnet.MyProcessingNode;
import pn.dao.UserDAO;
import pn.interSCity.InterSCity;
import pn.main.MyProcessingNodeMain;
import pn.util.Debug;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReceiveData extends Thread {
	private MyProcessingNode connection;
	private InterSCity interSCity;

	public ReceiveData(String interSCityIPAddress) throws Exception {
		// create a new connection to send message to mobile-hub
//		this.connection = new MyProcessingNode(StaticLibrary.contextNetIPAddress);
		this.interSCity = new InterSCity(interSCityIPAddress);
	}

	/**
	 * Receives a message when a person enters a new region (group)<br>
	 * Queries InterSCity for an alert<br>
	 * Send an alert to a person who enter the region<br>
	 */
	@Override
	public void run() {
		System.out.println("ENTROOOU");
		Object data;
		List<Alert> alerts = new ArrayList<Alert>();

		while(true) {
//			while(MyProcessingNodeMain.dataToPNQueue.isEmpty()) {
//				synchronized (MyProcessingNodeMain.dataToPNQueue) {
//					try {
//						MyProcessingNodeMain.dataToPNQueue.wait();
//					} catch (InterruptedException e) {
//						Debug.warning("Could not wait for dataToPN", e);
//					}
//				}
//			}
			// dataToPNQueue is ConcurrentLinkedQueue thread safe linked queue, so, does NOT need to be synchronized
			while ((data = MyProcessingNodeMain.dataToPNQueue.poll()) != null) {
				Debug.info("Data received: " + data + " object");
				if(data instanceof PersonSituation) {
					PersonSituation personSituation = (PersonSituation) data;
					Debug.info("This person is in the area #" + personSituation.getDifGroups().toString());
					// now it is time to lookup at the database for an alert at this new person areas of interested
					if(personSituation.isNewPerson()) {
						// if this is a new person, lookup areas of interest in the database by username
						try {
							// get all areas this person is interested (these areas are not the same area where this person is in)
							List<Integer> areas = UserDAO.getInstance().getAreasByUsername(personSituation.getId());
							Debug.info(personSituation.getId() + " has " + areas.size() + " areas of interest");
							personSituation.addToDifGroups(areas);
						} catch (ClassNotFoundException | SQLException e) {
							Debug.warning("Could not generate the alert list for this new person.", e);
						}
					}
					// lookup for alerts where this person has entered now
					alerts.addAll(interSCity.getAlertListByArea(personSituation.getDifGroups()));
					// check if there is alerts to this person
					if(alerts != null) {
						Debug.info("There is " + alerts.size() + " alert(s) for " + personSituation.getId());
						// send each alert to this person
						for(Alert alert : alerts) {
							MyProcessingNode pn = new MyProcessingNode();
							this.connection.sendUnicastMessage(personSituation.getUuid(), alert.getText());
						}
					}
				}
			}
		}
	}
}
