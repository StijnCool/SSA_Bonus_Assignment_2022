/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;
	

        /**
     * @param   args    The command line arguments.
     */
    public static void main(String[] args) {

        /*
        Regular Customers
        Arrival times according to Poisson process:
        - Arrival rate (lambda) = 1/min = (1/60)/s
        Service times according to normal distribution:
        - Mean = 2.6 min = 156 s
        - Standard deviation = 1.1 min = 66 s
        - Minimum service time = 1 sec
         */
        double[] interarrivalTimes = {};
        double[] serviceTimes = {};

        /*
        Service Desk Customers
        Arrival times according to Poisson process:
        - Mean interarrival time (1/lambda) = 5 min = 300 s
        - Arrival rate (lambda) = 0.2/min = (1/300)/s
        Service times according to normal distribution:
        - Mean = 4.1 min = 246 s
        - Standard deviation = 1.1 min = 66 s
        - Minimum service time = 1 sec
         */


    	// Create an eventlist
	    CEventList l = new CEventList();
	    // A queue for the machine
	    Queue q = new Queue();
	    // A source
	    Source s = new Source(q,l,"Source 1",interarrivalTimes);
	    // A sink
	    Sink si = new Sink("Sink 1");
	    // A machine
	    Machine m = new Machine(q,si,l,"Machine 1", serviceTimes);
	    // start the eventlist
	    l.start(8.7); // 2000 is maximum time
    }

    /**
     * Method to generate an interarrival time according to Poisson process
     * @param arrival_rate  double  The arrival rate lambda of the poisson process
     * @return              double  The generated interarrival time
     */
    private static double generate_interarrival_time(double arrival_rate){
        double t_ia;
        t_ia = -1;
        return t_ia;
    }

    /**
     * Method to generate a service time according to normal distribution
     * @param mean                  double  The mean of the normal distribution
     * @param standard_deviation    double  The standard deviation of the normal distribution
     * @return                      double  The generated service time
     */
    private static double generate_service_time(double mean, double standard_deviation){
        double t_s;
        t_s = -1;
        return t_s;
    }
    
}
