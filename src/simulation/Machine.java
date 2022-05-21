package simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *	Machine in a factory
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Machine implements CProcess,ProductAcceptor {
	/** Product that is being handled  */
	private Product product;
	/** Eventlist that will manage events */
	private final CEventList eventlist;
	/** Queue from which the machine has to take products */
	private Queue queue;
	private ArrayList<Queue> queue_service;
	/** Sink to dump products */
	private ProductAcceptor sink;
	/** Status of the machine (b=busy, i=idle) */
	private char status;
	/** Machine name */
	private final String name;
	/** Mean processing time */
	private double meanProcTime;
	/** Processing times (in case pre-specified) */
	private double[] processingTimes;
	/** Processing time iterator */
	private int procCnt;
	/** Mean processing time */
	private double mean;
	/** Multiple means of processing time */
	private double[] mean_2;
	/** Standard deviation of processing time */
	private double STD;
	/** Multiple standard deviations of processing time */
	private double[] STD_2;
	/** Type of the machine, i.e., it accept one ("single") or two queues ("both") */
	private String type;

	/**
	 * Constructor
	 * 		Service times are exponentially distributed with specified mean
	 * @param q Queue from which the machine has to take products
	 * @param s Where to send the completed products
	 * @param e Eventlist that will manage events
	 * @param n The name of the machine
	 * @param mean Mean processing time
	 * @param STD Standard deviation of processing time
	 * @param type String, either "single" or "both", that determines whether machine has one or more queues
	 */
	public Machine(Queue q, ProductAcceptor s, CEventList e, String n, double mean, double STD, String type) {
		this.status = 'i';
		this.queue = q;
		this.sink = s;
		this.eventlist = e;
		this.name = n;
		this.mean = mean;
		this.STD = STD;
		this.queue.askProduct(this);
		this.type = type;
	}

	/**
	 * Constructor
	 *		Service times are exponentially distributed with specified means
	 * @param qs Queues from which the machine has to take products
	 * @param s Where to send the completed products
	 * @param e Eventlist that will manage events
	 * @param n The name of the machine
	 * @param mean_2 Multiple means of processing time
	 * @param STD_2 Multiple standard deviations of processing time
	 * @param type String, either "single" or "both", that determines whether machine has one or more queues
	 */
	public Machine(ArrayList<Queue> qs, ProductAcceptor s, CEventList e, String n, double[] mean_2, double[] STD_2, String type) {
		this.status = 'i';
		this.queue_service = qs;
		for (Queue queue : qs) {
			queue.askProduct(this);
		}
		this.sink = s;
		this.eventlist = e;
		this.name = n;
		this.mean_2 = mean_2;
		this.STD_2 = STD_2;
		this.type = type;
	}

	/**
	 * Constructor
	 *		Service times are exponentially distributed with mean 30
	 * @param q Queue from which the machine has to take products
	 * @param s Where to send the completed products
	 * @param e Eventlist that will manage events
	 * @param n The name of the machine
	 */
	public Machine(Queue q, ProductAcceptor s, CEventList e, String n) {
		this.status = 'i';
		this.queue = q;
		this.sink = s;
		this.eventlist = e;
		this.name = n;
		this.meanProcTime = 30;
		this.queue.askProduct(this);
	}

	/**
	 * Constructor
	 *		Service times are exponentially distributed with specified mean
	 * @param q Queue from which the machine has to take products
	 * @param s Where to send the completed products
	 * @param e Eventlist that will manage events
	 * @param n The name of the machine
	 * @param m Mean processing time
	 */
	public Machine(Queue q, ProductAcceptor s, CEventList e, String n, double m) {
		this.status = 'i';
		this.queue = q;
		this.sink = s;
		this.eventlist = e;
		this.name = n;
		this.meanProcTime = m;
		this.queue.askProduct(this);
	}
	
	/**
	 * Constructor
	 *		Service times are pre-specified
	 * @param q Queue from which the machine has to take products
	 * @param s Where to send the completed products
	 * @param e Eventlist that will manage events
	 * @param n The name of the machine
	 * @param st Service times
	 */
	public Machine(Queue q, ProductAcceptor s, CEventList e, String n, double[] st) {
		this.status = 'i';
		this.queue = q;
		this.sink = s;
		this.eventlist = e;
		this.name = n;
		this.meanProcTime = -1;
		this.processingTimes = st;
		this.procCnt = 0;
		this.queue.askProduct(this);
	}

	/**
	 * Method to have this object execute an event
	 * @param type The type of the event that has to be executed
	 * @param tme The current time
	 */
	public void execute(int type, double tme) {
		// Show completion product at machine
		// Print at which machine and time the product finished
		System.out.println("Machine ---> Product finished at " + this.name + " at time = " + tme);

		// Mark time of completion
		product.stamp(tme,"Production complete",name);

		// Record delay and service times for both normal and service desk customers
		List<Double> times = product.getTimes();
		double delay = times.get(1) - times.get(0);
		double serviceTime = times.get(2) - times.get(1);
		if (isRegularCustomer()) {
			Simulation.delayNormalList.add(delay);
			Simulation.serviceTimeNormalList.add(serviceTime);
		} else {
			Simulation.delayServiceList.add(delay);
			Simulation.serviceTimeServiceList.add(serviceTime);
		}

		// Remove product from system by sending the product to the sink and setting product to null
		sink.giveProduct(product);
		product = null;
		// Set machine status to idle
		status = 'i';
		// Ask the queue for new products
		if (this.type.equals("single")) {
			queue.askProduct(this);
		} else {
			// In this case type = "both", i.e., this is the service desk with two queues.
			// At the service desk, customers in the service desk queue get priority over customers in the cash register queue
			if (queue_service.get(0).getSize() != 0) {
				queue_service.get(0).askProduct(this); // Service Desk customer
			} else {
				queue_service.get(1).askProduct(this); // Cash Register customer
			}
		}
	}
	
	/**
	 * Let the machine accept a product and let it start handling it
	 * @param p The product that is offered
	 * @return true if the product is accepted and started, false in all other cases
	 */
	@Override
	public boolean giveProduct(Product p) {
		// Only accept something if the machine is idle
		if (status == 'i') {
			// Accept the product
			product = p;
			// Mark starting time
			product.stamp(eventlist.getTime(),"Production started",name);

			// Print at which machine and time the production started
			System.out.println("Machine ---> Production of " + ((p.getSourceType().equals("Source Service")) ? "Service Desk Customer" : "Regular Customer") + " started at " + name + " at time = " + eventlist.getTime());

			// When type is "both", switch to the right queue and take correct mean and std of corresponding customer group
			if (this.type.equals("both")) {
				if (isRegularCustomer()) {
					// In this case, customer comes from the source of regular (=cash register) customers
					queue = queue_service.get(1);
					mean = mean_2[1];
					STD = STD_2[1];
				} else {
					// In this case, customer comes from the source of service desk customers
					queue = queue_service.get(0);
					mean = mean_2[0];
					STD = STD_2[0];
				}
			}

			// Start production
			startProduction();
			// Flag that the product has arrived
			return true;
		} else {
			// Flag that the product has been rejected
			return false;
		}
	}

	/**
	 * @return True if customer is from Source Regular, i.e., a cash register customer, false when service desk customer
	 */
	private boolean isRegularCustomer() {
		return this.product.getSourceType().equals("Source Regular");
	}

	/**
	 * Starting routine for the production
	 * Start the handling of the current product with an exponentially distributed processing-time with average 30
	 * This time is placed in the eventlist
	 */
	private void startProduction() {
		// Generate duration
		if (mean > 0) {
			double duration = Simulation.generate_service_time(mean,STD,1/60);
			// Create a new event in the eventlist
			double tme = eventlist.getTime();
			eventlist.add(this,0,tme+duration); //target,type,time
			// Set status to busy
			status = 'b';
		} else {
			if (processingTimes.length > procCnt) {
				eventlist.add(this,0,eventlist.getTime()+processingTimes[procCnt]); //target,type,time
				// Set status to busy
				status = 'b';
				procCnt++;
			} else {
				eventlist.stop();
			}
		}
	}

	/**
	 * Draw a random exponentially distributed variate with mean
	 * @param mean Mean of exponential distribution
	 * @return (Pseudo-)randomly generated exponentially distributed variate
	 */
	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into an exponentially distributed random variate with mean 33
		double res = -mean * Math.log(u);
		return res;
	}
}