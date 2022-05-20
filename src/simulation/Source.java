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
	 *	Constructor, creates objects
	 *        Interarrival times are exponentially distributed with mean 33
	 *	@param q	The receiver of the products
	 *	@param l	The eventlist that is requested to construct events
	 *	@param n	Name of object
	 */
	public Source(ArrayList<Queue> q, CEventList l, String n, double _arrivalRate) {
		list = l;
		queues = q;
		name = n;
		arrivalRate = _arrivalRate;
		// put first event in list for initialization
		double firstIAT = Simulation.generate_interarrival_time(arrivalRate);
		list.add(this,0,firstIAT); //target,type,time
		interarrivalTimes = new double[]{firstIAT};
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*/
	public Source(ArrayList<Queue> q,CEventList l,String n) {
		list = l;
		queues = q;
		name = n;
		meanArrTime=33;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with specified mean
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param m	Mean arrival time
	*/

	/**
	public Source(ProductAcceptor q,CEventList l,String n,double m)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=m;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}
	 */

	/**
	*	Constructor, creates objects
	*        Interarrival times are prespecified
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param ia	interarrival times
	*/
	public Source(ArrayList<Queue> q,CEventList l,String n,double[] ia) {
		list = l;
		queues = q;
		name = n;
		meanArrTime=-1;
		interarrivalTimes=ia;
		interArrCnt=0;
		// put first event in list for initialization
		list.add(this,0,interarrivalTimes[0]); //target,type,time
	}
	
	@Override
	public void execute(int type, double tme) {
		// show arrival
		Product p = new Product();
		p.stamp(tme,"Creation", this.name);
		p.setSourceType(this.name);

		System.out.println("Source ---> " + (name.equals("Source Service") ? "Service Desk" : "Regular" + " Customer Created"));

		// Determine which queue the customer will join
		int queue_num = choose_queue(this.queues);

		System.out.println("Source ---> Send " + (name.equals("Source Service") ? "Service Desk" : "Regular") + " Customer to queue " + (p.getSourceType().equals("Source Service") ? "Service" : (queue_num+1)));

		// give arrived product to queue
		this.queues.get(queue_num).giveProduct(p);

		// Record the arrival times
		if (this.name.equals("Source Regular")) {
			Simulation.arrivalTimeNormalList.add(tme);
		} else {
			Simulation.arrivalTimeServiceList.add(tme);
		}

		// This method is just for testing purposes to see how long every queue is
//		this.printQueueLengths(tme, queue_num);


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

	private int choose_queue(ArrayList<Queue> queues) {
		if (this.name.equals("Source Service")) {
			// Always return 0 if customer is from service desk customer source
			return 0;
		}

		// Determine which open cash register has the smallest queue
		int smallest = queues.get(0).getSize();
		int smallestNum = 0;

		for (int i = 0; i < queues.size()-1; i++) {
			// Check for all open cash registers which one has the smallest queue
			if (queues.get(i).getWorking()) {
				if (i == 5) { // This is the cash register at the service desk
					int rows_service = queues.get(5).getSize() + queues.get(6).getSize();
					if (rows_service < smallest) {
						smallest = rows_service;
						smallestNum = 5;
					}
				} else {
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

		return smallestNum;
	}

	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean 33
		double res = -mean*Math.log(u);
		return res;
	}

	private void printQueueLengths(double tme, int queue_num) {
		if (queues.size() == 7) {
			System.out.println("Arrival at queue " + (queue_num+1) + " was at time = " + tme);

			for (int i = 0; i < queues.size()-1; i++) {
				if (i==5) {
					int row_size = queues.get(i).getSize()+queues.get(i+1).getSize();
					System.out.print("Q6+S: " + row_size + "\n\n");
				} else {
					System.out.print("Q" + (i+1) + ": " + queues.get(i).getSize() + " ");
				}
			}
		} else {
			System.out.println("Arrival at queue " + 6 + "+S was at time = " + tme);
			int row_size = queues.get(0).getSize() + queues.get(1).getSize();
			System.out.println("Q6+S: " + row_size + "\n");
		}
	}
}