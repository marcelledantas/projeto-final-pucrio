package pn.interSCity;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InterSCityConsumer implements Runnable {
	/** stores a queue of bus data to be sent to the InterSCity */
	private ConcurrentLinkedQueue<InterSCityData> interSCityQueue = new ConcurrentLinkedQueue<InterSCityData>();
	private InterSCity interSCity;

	/**
	 * Constructor<br>
	 * Constructs a consumer to consume BusQueue data and send it to InterSCity<br>
	 * 
	 * @param interSCity
	 * @param interSCityQueue
	 */
	public InterSCityConsumer(InterSCity interSCity, ConcurrentLinkedQueue<InterSCityData> interSCityQueue) {
		this.interSCity = interSCity;
		this.interSCityQueue = interSCityQueue;
	}

	@Override
	public void run() {
		InterSCityData data;
		while(true) {
			while(interSCityQueue.isEmpty()) {
				synchronized (interSCityQueue) {
					try {
						interSCityQueue.wait();
					} catch (InterruptedException e) {
						System.err.println("Date = " + new Date());
						e.printStackTrace();
					}
				}
			}
			// busQueue is ConcurrentLinkedQueue thread safe linked queue, so, does NOT need to be synchronized
			while ((data = interSCityQueue.poll()) != null) {
				try {
					interSCity.updateDB(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}	
}