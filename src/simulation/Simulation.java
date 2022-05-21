/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Simulation {

    public CEventList list;
    //public static Queue queueService = new Queue("open");
    public Source source;
    public Sink sink;
    public Machine mach;


    public static List<Double> delayNormalList = new ArrayList<>();
    public static List<Double> delayServiceList = new ArrayList<>();
    public static List<Double> arrivalTimeNormalList = new ArrayList<>();

    public static List<Double> arrivalTimeServiceList = new ArrayList<>();
    public static List<Double> serviceTimeNormalList = new ArrayList<>();
    public static List<Double> serviceTimeServiceList = new ArrayList<>();

    /* The nxm+1 matrix takes the following form:
             t1,#Q1@t1,...,#Qj@t1,...,#Qm@t1,id1
             ⋮     ⋮          ⋮           ⋮    ⋮
            ti,#Q1@ti,...,#Qj@ti,...,#Qm@ti,idi
            ⋮     ⋮          ⋮           ⋮    ⋮
           tn,#Q1@tn,...,#Qj@tn,...,#Qm@tn,idn
        Where we have at most n queues and a queue length change happens m times
        ti = the time at the i'th change of queue length
        #Qj@ti = the number of customers in queue j after the i'th change of queue length
        idi = 1 if i'th queue length change is an arrival, 0 otherwise
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
    public static List<List<Double>> queueMatrix = new ArrayList<>();
    private static Random generator = new Random();
	

    /**
     * @param args The command line arguments.
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
        boolean writeToFiles = true;
        double max_time = 460; // 8 hours working day
        for(int i = 1; i<=10; i++) {
            //generator = new Random(i^i);
            run_simulation(i, max_time, writeToFiles);
            print(i);
        }
    }

    private static void run_simulation(int iteration, double max_time, boolean writeToFiles) {
        System.out.println("-----------------------\n##### ITERATION " + iteration + " #####\n-----------------------");

    	// Create an eventlist
	    CEventList l = new CEventList();

	    // The queues for the machines
        Queue queueCash1 = new Queue("open",1);
        Queue queueCash2 = new Queue("open",2);
        Queue queueCash3 = new Queue("switching",3);
        Queue queueCash4 = new Queue("switching",4);
        Queue queueCash5 = new Queue("switching",5);
        Queue queueCashService = new Queue("open",6);
        Queue queueService = new Queue("open",7);

        // List of queues for the sources
        ArrayList<Queue> cashQueues = new ArrayList<>(Arrays.asList(queueCash1, queueCash2, queueCash3, queueCash4, queueCash5, queueCashService, queueService));
        ArrayList<Queue> serviceDeskQueues = new ArrayList<>(Arrays.asList(queueService, queueCashService));

        // The sources
        Source sourceRegular = new Source(cashQueues, l, "Source Regular", 1);
        Source sourceService = new Source(serviceDeskQueues,l,"Source Service",0.2);

        // The sink
        Sink si = new Sink("Sink 1");

        // The machines
        Machine machineCash1 = new Machine(queueCash1,si,l,"Machine Cash 1", 2.6,1.1, "single");
        Machine machineCash2 = new Machine(queueCash2,si,l,"Machine Cash 2", 2.6,1.1, "single");
        Machine machineCash3 = new Machine(queueCash3,si,l,"Machine Cash 3", 2.6,1.1, "single");
        Machine machineCash4 = new Machine(queueCash4,si,l,"Machine Cash 4", 2.6,1.1, "single");
        Machine machineCash5 = new Machine(queueCash5,si,l,"Machine Cash 5", 2.6,1.1, "single");
        Machine machineService = new Machine(serviceDeskQueues,si,l,"Machine Service", new double[]{4.1, 2.6},new double[]{1.1, 1.1}, "both");

        // Start the eventlist
        l.start(max_time);

        // Write to a file
        if (writeToFiles) {
            write_to_file(arrivalTimeNormalList, "arrivalTimeNormalList" + iteration);
            write_to_file(arrivalTimeServiceList, "arrivalTimeServiceList" + iteration);
            write_to_file(delayNormalList, "delayNormalList" + iteration);
            write_to_file(delayServiceList, "delayServiceList" + iteration);
            write_to_file(serviceTimeNormalList, "serviceTimeServiceList" + iteration); // these are switched in the code
            write_to_file(serviceTimeServiceList, "serviceTimeNormalList" + iteration);
            write_to_file(queueMatrix, "queueMatrix" + iteration);
        }

        // Prints for testing purposes
        System.out.println();
        print("arrivalTimeNormalList: " + arrivalTimeNormalList.size());
        //print(arrivalTimeNormalList);
        print("arrivalTimeServiceList: " + arrivalTimeServiceList.size());
        //print(arrivalTimeServiceList);
        print("delayNormalList: " + delayNormalList.size());
        //print(delayServiceList);
        print("delayServiceList: " + delayServiceList.size());
        //print(serviceTimeNormalList);
        print("serviceTimeNormalList: " + serviceTimeNormalList.size());
        //print(serviceTimeServiceList);
        print("serviceTimeServiceList: " + serviceTimeServiceList.size());
        System.out.println("\n");
        //print_matrix(queueMatrix);

        // Clear the recorded times for new simulation
        arrivalTimeNormalList = new ArrayList<>();
        arrivalTimeServiceList = new ArrayList<>();
        delayNormalList = new ArrayList<>();
        delayServiceList = new ArrayList<>();
        serviceTimeNormalList = new ArrayList<>();
        serviceTimeServiceList = new ArrayList<>();
        queueMatrix = new ArrayList<>();
    }

    /**
     * Method to generate an interarrival time according to Poisson process
     * @param arrivalRate   double  The arrival rate lambda of the poisson process
     * @return              double  The generated interarrival time in seconds
     */
    public static double generate_interarrival_time(double arrivalRate) {
        // Poisson arrival times lead to exponential interarrival times
        double U = generator.nextDouble();
        double X = -1/arrivalRate*Math.log(1-U);
        return X;
    }

    /**
     * Method to generate a service time according to normal distribution
     * @param mean                  double  The mean of the normal distribution
     * @param standardDeviation     double  The standard deviation of the normal distribution
     * @return                      double  The generated service time
     */
    public static double generate_service_time(double mean, double standardDeviation,double minimumServiceTime) {
        // Generate using Box-Muller Transform
        double U1 = generator.nextDouble();
        double U2 = generator.nextDouble();
        double magnitude = standardDeviation * Math.sqrt(-2.0 * Math.log(U1));
        double X = magnitude * Math.cos(2*Math.PI * U2) + mean;
        return Math.max(X,minimumServiceTime);
    }

    /**
     * Method to print the ArrayLists with the recorded data
     */
    private static <T> void print(T s) {
        System.out.println(s);
    }

    /**
     * Method to print queueMatrix
     */
    public static void print_matrix(List<List<Double>> M) {
        for (List<Double> l : M) {
            print(l);
        }
    }

    /**
     * Method to write the recorded data to a file
     * @param L The list we want to write a file of
     * @param filename The name of the file
     */
    public static void write_to_file(List L, String filename) {
        try {
            FileWriter myWriter = new FileWriter("files/"+filename+".txt");
            String s = "";

            if(L.get(0) instanceof List){ // L is a matrix
                s += filename+" = [";
                for(Object l : L){
                    l = (List) l;
                    String ls = l.toString().replace("[","").replace("]","");
                    s += ls + ";";
                }
                s += "];";
            } else{ // L is a list
                s = filename+" = "+L.toString()+";";
            }
            myWriter.write(s);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
