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
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /*
        Arrival times according to Poisson process:
        - Arrival rate (lambda) = 1/min = (1/60)/s
        Service times according to normal distribution:
        - Mean = 2.6 min = (13/300)/s
        - Standard deviation = 1.1 min = (11/600)/s
        - Minimum service time = 1 sec
         */
        double[] interarrivalTimes = {};
        double[] serviceTimes = {};

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
    
}
