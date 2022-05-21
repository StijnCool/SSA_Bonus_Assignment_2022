package simulation;

import java.util.ArrayList;

/**
 *	A source of products
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Source implements CProcess {
	/** Eventlist that will be requested to construct events */
	private CEventList list;
	/** Queue that buffers products for the machine */
	private ArrayList<Queue> queues;
	/** Name of the source */
	private String name;
	/** Mean interarrival time */
	private double meanArrTime;
	/** Interarrival times (in case pre-specified) */
	private double[] interarrivalTimes;
	/** Interarrival time iterator */
	private int interArrCnt=0;
	private double arrivalRate;

	/**
	 * Constructor, creates objects
	 *		Interarrival times are exponentially distributed with mean 33
	 * @param q The receiver of the products
	 * @param l The eventlist that is requested to construct events
	 * @param n Name of object
	 */
	public Source(ArrayList<Queue> q, CEventList l, String n, double _arrivalRate) {
		this.list = l;
		this.queues = q;
		this.name = n;
		this.arrivalRate = _arrivalRate;
		// put first event in list for initialization
		double firstIAT = Simulation.generate_interarrival_time(arrivalRate);
		this.list.add(this,0,firstIAT); //target,type,time
		this.interarrivalTimes = new double[]{firstIAT};
	}

	/**
	 * Constructor, creates objects
	 *		Interarrival times are exponentially distributed with mean 33
	 * @param q The receiver of the products
	 * @param l The eventlist that is requested to construct events
	 * @param n Name of object
	 */
	public Source(ArrayList<Queue> q,CEventList l,String n) {
		this.list = l;
		this.queues = q;
		this.name = n;
		this.meanArrTime=33;
		// put first event in list for initialization
		this.list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	 * Constructor, creates objects
	 *		Interarrival times are prespecified
	 * @param q The receiver of the products
	 * @param l The eventlist that is requested to construct events
	 * @param n Name of object
	 * @param ia Interarrival times
	 */
	public Source(ArrayList<Queue> q,CEventList l,String n,double[] ia) {
		this.list = l;
		this.queues = q;
		this.name = n;
		this.meanArrTime = -1;
		this.interarrivalTimes = ia;
		this.interArrCnt = 0;
		// put first event in list for initialization
		this.list.add(this,0,interarrivalTimes[0]); //target,type,time
	}

	/**
	 * Method to generate a new customer and sent this one to one of the open queues
	 * @param type The type of the event that has to be executed
	 * @param tme The current time
	 */
	@Override
	public void execute(int type, double tme) {
		// Show arrival
		Product p = new Product();
		p.stamp(tme,"Creation", this.name);
		p.setSourceType(this.name);

		// Print what type of customer has been created
		System.out.println("Source ---> " + (name.equals("Source Service") ? "Service Desk" : "Regular" + " Customer Created"));

		// Determine which queue the customer will join
		int queue_num = choose_queue(this.queues);

		// Print to which queue the customer is sent
		System.out.println("Source ---> Send " + (name.equals("Source Service") ? "Service Desk" : "Regular") + " Customer to queue " + (p.getSourceType().equals("Source Service") ? "Service" : (queue_num+1)));

		// Give arrived product to queue
		this.queues.get(queue_num).giveProduct(p);

		// Record the arrival times
		if (this.name.equals("Source Regular")) {
			Simulation.arrivalTimeNormalList.add(tme);
		} else {
			Simulation.arrivalTimeServiceList.add(tme);
		}

		// Generate duration
		if (1/arrivalRate > 0) {
			double duration = Simulation.generate_interarrival_time(arrivalRate);
			// Create a new event in the eventlist
			list.add(this,0,tme+duration); //target,type,time
		} else {
			interArrCnt++;
			if(interarrivalTimes.length > interArrCnt) {
				list.add(this,0,tme+interarrivalTimes[interArrCnt]); //target,type,time
			} else {
				list.stop();
			}
		}
	}

	/**
	 * Determine which queue is the smallest
	 * @param queues List of queues to choose from to which the product can be sent
	 * @return The number of the smallest queue
	 */
	private int choose_queue(ArrayList<Queue> queues) {
		if (this.name.equals("Source Service")) {
			// Always return 0 if customer is from the service desk customer source
			return 0;
		}

		// Determine which open cash register has the smallest queue
		int smallest = queues.get(0).getSize();
		int smallestNum = 0;

		// Check for all open cash registers which one has the smallest queue
		for (int i = 0; i < queues.size() - 1; i++) {
			if (queues.get(i).getWorking()) {
				if (i == 5) { // This is the cash register at the service desk
					// Total length of queues is the length both queues at service desk summed
					int rows_service = queues.get(5).getSize() + queues.get(6).getSize();
					// Update smallest queue when size of queue is smaller than previous smallest queue
					if (rows_service < smallest) {
						smallest = rows_service;
						smallestNum = 5;
					}
				} else {
					// Update smallest queue when size of queue is smaller than previous smallest queue
					if (queues.get(i).getSize() < smallest) {
						smallest = queues.get(i).getSize();
						smallestNum = i;
					}
				}
			}
		}

		// If the smallest queue is bigger or equal to 4, a new cash register will be opened
		if (smallest >= 4) {
			for (int i = 0; i < queues.size()-1; i++) {
				if (!queues.get(i).getWorking()) {
					queues.get(i).setToWork();
					return i;
				}
			}
		}

		// Return the queue number of the smallest queue
		return smallestNum;
	}

	/**
	 * Draw a random exponentially distributed variate with mean
	 * @param mean Mean of exponential distribution
	 * @return (Pseudo-)randomly generated exponentially distributed variate
	 */
	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into an exponentially distributed random variate with given mean
		double res = -mean * Math.log(u);
		return res;
	}
}