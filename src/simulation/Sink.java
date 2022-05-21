package simulation;

import java.util.ArrayList;
/**
 *	A sink
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Sink implements ProductAcceptor {
	/** All products are kept */
	private ArrayList<Product> products;
	/** All properties of products are kept */
	private ArrayList<Integer> numbers;
	private ArrayList<Double> times;
	private ArrayList<String> events;
	private ArrayList<String> stations;
	/** Counter to number products */
	private int number;
	/** Name of the sink */
	private String name;
	
	/**
	 * Constructor, creates objects
	 * @param n Name of sink
	 */
	public Sink(String n) {
		this.name = n;
		this.products = new ArrayList<>();
		this.numbers = new ArrayList<>();
		this.times = new ArrayList<>();
		this.events = new ArrayList<>();
		this.stations = new ArrayList<>();
		this.number = 0;
	}

	/**
	 * Method to give product to sink
	 * @param p	The product that is accepted
	 * @return true, since product is always accepted
	 */
	@Override
	public boolean giveProduct(Product p) {
		number++;
		products.add(p);
		// store stamps
		ArrayList<Double> t = p.getTimes();
		ArrayList<String> e = p.getEvents();
		ArrayList<String> s = p.getStations();
		for (int i = 0; i < t.size(); i++) {
			numbers.add(number);
			times.add(t.get(i));
			events.add(e.get(i));
			stations.add(s.get(i));
		}
		return true;
	}
}