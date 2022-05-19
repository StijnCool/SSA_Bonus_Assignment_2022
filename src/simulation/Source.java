package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		// give arrived product to queue
		Product p = new Product();
		p.stamp(tme,"Creation",name);

		// TODO make it choose the correct queue
		int queue_num = choose_queue(queues);


		//System.out.println("Q1: " + queues.get(0).getSize() + "Q2: " + queues.get(1).getSize() + "Q3: " + queues.get(2).getSize() + "Q4: " + queues.get(3).getSize() +"Q5: " + queues.get(4).getSize() +"QS: " + (queues.get(5).getSize() + queues.get(6).getSize()));



		queues.get(queue_num).giveProduct(p);

		if (queues.size()==7) {
			System.out.println("Arrival at queue " + queue_num + " time = " + tme);
			Simulation.arrivalTimeList.add(tme);
			for (int i = 0; i < queues.size()-1; i++) {
				if (i==5) {
					int row_size = queues.get(i).getSize()+queues.get(i+1).getSize();
					System.out.print("Q" + i + ": " + row_size + "\n");
				} else {
					System.out.print("Q" + i + ": " + queues.get(i).getSize() + " ");
				}
			}
		} else {
			System.out.println("Arrival at queue " + 6 + " time = " + tme);
			Simulation.arrivalTimeList.add(tme);
			int row_size = queues.get(0).getSize() + queues.get(1).getSize();
			System.out.println("Q6: " + row_size);
		}


		/*
		for (int i = 0; i < queues.size()-1; i++) {
			if (i==5){
				int row_size = queues.get(i).getSize()+queues.get(i+1).getSize();
				System.out.print("Q" + i + ": " + row_size + "\n");
			} else {
				System.out.print("Q" + i + ": " + queues.get(i).getSize() + " ");
			}
		}
		*/

		//System.out.println("Q1: " + queues.get(0).getSize() + "Q2: " + queues.get(1).getSize() + "Q3: " + queues.get(2).getSize() + "Q4: " + queues.get(3).getSize() +"Q5: " + queues.get(4).getSize() +"QS: " + (queues.get(5).getSize() + queues.get(6).getSize()));

		// generate duration
		if (1/arrivalRate>0) {
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
		if (queues.size()==2) {
			return 0;
		}

		int smallest = queues.get(0).getSize();
		int smallestNum = 0;

		for (int i = 0; i < queues.size()-1; i++) {
			if (i==5) {
				int rows_service = queues.get(5).getSize() + queues.get(6).getSize();
				if (rows_service < smallest) {
					smallest = rows_service;
					smallestNum = 5;
				}
			} else if (queues.get(i).getWorking() == true) {
				if(queues.get(i).getSize() < smallest){
					smallest = queues.get(i).getSize();
					smallestNum = i;
				}
			}
		}

		if (smallest>=4) {
			for (int i = 0; i < queues.size()-1; i++) {
				if (!queues.get(i).getWorking()) {
					queues.get(i).setToWork();
					return i;
				}
			}
		}
		return smallestNum;



		/*
		int queue_num = 0;
		int queue_rows = 4;
		int closed_register_num = 10;
		int current_rows = 0;

		Queue smallest = queues.get(0);
		int smallestNum = 0;

		if (queues.size() == 1) {
			return 0;
		}
		int service_desk = queues.get(6).getSize() + queues.get(7).getSize();
		if (queues.get(2).getSize()==0 && queues.get(3).getSize()==0 && queues.get(4).getSize()==0 && queues.get(5).getSize()==0)
		if (queues.get(0).getSize() <= 4 && queues.get(1).getSize() <= 4 && service_desk < 4) {
			int smallest_queue = Math.min(queues.get(0).getSize(), Math.min(queues.get(1).getSize(), queues.get(6).getSize()));
			if (queues.get(0).getSize() == smallest_queue){
				return 0;
			} else if (queues.get(1).getSize() == smallest_queue){
				return 1;
			} else {
				return 6;
			}

					queues.get(0).getSize() < queues.get(1).getSize()) {
				return 0;
			} else {
				return 1;
			}


		} else {
			// TODO take into account when to open or close new cash register
			for (int i = 0; i < queues.size()-1; i++) {
				if (queues.get(i).getSize() < smallest.getSize()) {
					smallest = queues.get(i);
					smallestNum = i;
				}
			}
			return smallestNum;
		}


		for (int i = 0; i<queues.size()-1; i++) {
			current_rows = queues.get(i).getSize();
			if (i==6) {
				//int queue_rows += ;
			} else if (i==0 && current_rows==0){
				return 0;
			} else if (i==1 && current_rows==0){
				return 1;
			}
			if (current_rows<4 && current_rows>0){
				if (current_rows < queue_rows){
					queue_num = i;
					queue_rows = queues.get(i).getSize();
				}
			}
			if (queue_rows==4 && queues.get(i).getSize()==0) {
				closed_register_num = i;
			}
		}
		if (queue_rows==4 && closed_register_num!=10){
			queue_num = closed_register_num;
		}
		return queues.size();

		*/



	}

	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean 33
		double res = -mean*Math.log(u);
		return res;
	}
}