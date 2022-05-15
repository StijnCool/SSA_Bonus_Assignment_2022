/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;

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
	    Queue q = new Queue();
	    // A source
	    Source sourceRegular = new Source(q,l,"Source Regular",1);
        Source sourceService = new Source(q,l,"Source Service",0.2);
	    // A sink
	    Sink si = new Sink("Sink 1");
	    // A machine
	    Machine machineRegular = new Machine(q,si,l,"Machine Regular", 2.6,1.1);
        Machine machineService = new Machine(q,si,l,"Machine Service", 4.1,1.1);
	    // start the eventlist
	    l.start(8.7); // 2000 is maximum time
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
