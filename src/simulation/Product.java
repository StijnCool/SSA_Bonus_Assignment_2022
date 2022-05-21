package simulation;

import java.util.ArrayList;
/**
 *	Product that is send trough the system
 *	@author Joel Karel
 *	@version %I%, %G%
 */
class Product {
	/** Stamps for the products */
	private ArrayList<Double> times;
	private ArrayList<String> events;
	private ArrayList<String> stations;
	/** Source name from which product was produced */
	private String source = "";
	
	/** 
	 *	Constructor for the product
	 *	Mark the time at which it is created
	 */
	public Product() {
		this.times = new ArrayList<>();
		this.events = new ArrayList<>();
		this.stations = new ArrayList<>();
	}

	/**
	 * Method to add stamps of certain events, i.e., creation, starting production and finishing production
	 * @param time Time of the event
	 * @param event Name of the event
	 * @param station Where the product was at
	 */
	public void stamp(double time,String event,String station) {
		this.times.add(time);
		this.events.add(event);
		this.stations.add(station);
	}

	public void setSourceType(String source) {
		this.source = source;
	}

	public String getSourceType() {
		return this.source;
	}
	
	public ArrayList<Double> getTimes() {
		return times;
	}

	public ArrayList<String> getEvents() {
		return events;
	}

	public ArrayList<String> getStations() {
		return stations;
	}
}