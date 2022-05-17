/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Simulation {

    public CEventList list;
    //public static Queue queueService = new Queue("open");
    public Source source;
    public Sink sink;
    public Machine mach;

    // TODO: Record delays
    // In product.java there is a times arraylist, you could print the delay whenever the product passes the machine
    public static List<Double> delayList = new ArrayList<Double>();
    public static List<Double> arrivalTimeList = new ArrayList<Double>();

    // TODO: Record service times
    // Again, times arraylist in product.java
    public static List<Double> serviceTimeList = new ArrayList<Double>();

    /* TODO: Record queue times when they change
        The nxm+1 matrix takes the following form:
             t1,#Q1@t1,...,#Qj@t1,...,#Qm@t1
             ⋮     ⋮          ⋮           ⋮
            ti,#Q1@ti,...,#Qj@ti,...,#Qm@ti
            ⋮     ⋮          ⋮           ⋮
           tm,#Q1@tn,...,#Qj@tn,...,#Qm@tn
        Where we have at most n queues and a queue length change happens m times
        ti = the time at the i'th change of queue length
        #Qj@ti = the number of customers in queue j after the i'th change of queue length
        For a matrix List<List<Double>> L, L.get(0) should return {l11,...,l1m}, but not {l11,...,ln1}
        This means the first list should store all rows, and the second list the entries for the columns in that row
     */
    /*
    For products arriving and getting into the queue, look at the print statements in source.java - execute
    If someone goes to the service desk, it only returns the queue for the service desk, so if that happens copy the
    last row in the matrix and update the time and service desk queue.

    For products leaving the queue, I would suggest looking at queue.java - askProduct and then copying and updating
    the row according to what queue it is.

    You can get the times from product, as said above. If this doesn't work try getting it where the stamp method is used.

    */
    public static List<List<Double>> queueMatrix = new ArrayList<List<Double>>();
    private final static Random generator = new Random(314159);
	

        /**
     * @param   args    The command line arguments.
     */
    public static void main(String[] args) {

        /*
        Regular Customers
        Arrival times according to Poisson process:
        - Arrival rate (lambda) = 1/min = (1/60)/s
        Service times according to normal distribution (per register):
        - Mean = 2.6 min = 156 s
        - Standard deviation = 1.1 min = 66 s
        - Minimum service time = 1 sec
         */

        /*
        Service Desk Customers
        Arrival times according to Poisson process:
        - Mean interarrival time (1/lambda) = 5 min = 300 s
        - Arrival rate (lambda) = 0.2/min = (1/300)/s
        Service times according to normal distribution (per register):
        - Mean = 4.1 min = 246 s
        - Standard deviation = 1.1 min = 66 s
        - Minimum service time = 1 sec
         */

        double[] serviceTimes = {};

    	// Create an eventlist
	    CEventList l = new CEventList();
	    // A queue for the machine
	    Queue queueService = new Queue("open");
        Queue queueCashService = new Queue("open");
        Queue queueCash1 = new Queue("open");
        Queue queueCash2 = new Queue("open");
        Queue queueCash3 = new Queue("switching");
        Queue queueCash4 = new Queue("switching");
        Queue queueCash5 = new Queue("switching");

        ArrayList<Queue> cashQueues = new ArrayList<>(Arrays.asList(queueCash1, queueCash2, queueCash3, queueCash4, queueCash5, queueCashService, queueService));
        ArrayList<Queue> serviceDeskQueues = new ArrayList<>(Arrays.asList(queueService, queueCashService));
	    // A source
	    Source sourceRegular = new Source(cashQueues, l, "Source Regular", 1);
        Source sourceService = new Source(serviceDeskQueues,l,"Source Service",0.2);
	    // A sink
	    Sink si = new Sink("Sink 1");
	    // A machine


        // TODO Make it possible that two queues join in one machine with priority to one queue (multiple constructors with type variable?)
	    Machine machineCash1 = new Machine(queueCash1,si,l,"Machine Cash 1", 2.6,1.1, "single");
        Machine machineCash2 = new Machine(queueCash2,si,l,"Machine Cash 2", 2.6,1.1, "single");
        Machine machineCash3 = new Machine(queueCash3,si,l,"Machine Cash 3", 2.6,1.1, "single");
        Machine machineCash4 = new Machine(queueCash4,si,l,"Machine Cash 4", 2.6,1.1, "single");
        Machine machineCash5 = new Machine(queueCash5,si,l,"Machine Cash 5", 2.6,1.1, "single");
        Machine machineService = new Machine(serviceDeskQueues,si,l,"Machine Service", new double[]{4.1, 2.6},new double[]{1.1, 1.1}, "both");
	    // start the eventlist
	    l.start(100); // 2000 is maximum time

        print("");
        print(arrivalTimeList);
    }

    /**
     * Method to generate an interarrival time according to Poisson process
     * @param arrivalRate  double  The arrival rate lambda of the poisson process
     * @return              double  The generated interarrival time in seconds
     */
    public static double generate_interarrival_time(double arrivalRate){
        // Poisson arrival times lead to exponential interarrival times
        double U = generator.nextDouble();
        double X = -1/arrivalRate*Math.log(1-U);
        return X;
    }

    /**
     * Method to generate a service time according to normal distribution
     * @param mean                  double  The mean of the normal distribution
     * @param standardDeviation    double  The standard deviation of the normal distribution
     * @return                      double  The generated service time
     */
    public static double generate_service_time(double mean, double standardDeviation,double minimumServiceTime){
        // Generate using Box-Muller Transform
        double U1 = generator.nextDouble();
        double U2 = generator.nextDouble();
        double magnitude = standardDeviation * Math.sqrt(-2.0 * Math.log(U1));
        double X = magnitude * Math.cos(2*Math.PI * U2) + mean;
        return Math.max(X,minimumServiceTime);
    }

    private static <T> void print(T s){
        System.out.println(s);
    }
    
}
