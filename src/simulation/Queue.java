package simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *	Queue that stores products until they can be handled on a machine machine
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Queue implements ProductAcceptor {
	/** List in which the products are kept */
	private ArrayList<Product> row;
	/** Requests from machine that will be handling the products */
	private ArrayList<Machine> requests;
	private boolean working;
	private String type;
	private int queueNumber;
	
	/**
	*	Initializes the queue and introduces a dummy machine
	*	the machine has to be specified later
	*/
	public Queue(String queue_type, int queueNumber) {
		this.type = queue_type;
		this.working = queue_type.equals("open");
		this.row = new ArrayList<>();
		this.requests = new ArrayList<>();
		this.queueNumber = queueNumber;
	}
	
	/**
	*	Asks a queue to give a product to a machine
	*	True is returned if a product could be delivered; false if the request is queued
	*/
	public boolean askProduct(Machine machine) {
		// This is only possible with a non-empty queue
		if (row.size() > 0) {
			// If the machine accepts the product
			if (machine.giveProduct(row.get(0))) {
				// Store the time of the product leaving the queue
				double tme = row.get(0).getTimes().get(1);

				// Remove it from the queue
				row.remove(0);

				// Record the time at which the customer leaves the queue and the resulting queue-lengths
				this.recordQueueLeaving(tme);

				// The queue will be set as closed (working = false) if the queue is empty and its type is "switching"
				if (row.size()==0 && type.equals("switching")) {
					working = false;
				}

				return true;
			} else {
				return false; // Machine rejected; don't queue request
			}
		} else {
			requests.add(machine);
			return false; // queue request
		}
	}
	
	/**
	*	Offer a product to the queue
	*	It is investigated whether a machine wants the product, otherwise it is stored
	*/
	public boolean giveProduct(Product p) {
//		System.out.println("Queue ---> Source type: " + p.getSourceType());

		// Record the time at which the customer arrives at the queue and the resulting queue-lengths
		this.recordQueueArrival(p.getTimes().get(0));

		// Check if the machine accepts it
		if (requests.size() < 1) {
			row.add(p); // Otherwise store it
		} else {
			boolean delivered = false;
			while (!delivered & (requests.size() > 0)) {
				delivered = requests.get(0).giveProduct(p);

				// If the product is sent to the machine, it left the queue
				if (delivered) {
//					System.out.println("Queue ---> " + (p.getSourceType().equals("Source Service") ? "Service Desk" : "Regular") + " Customer was sent to " + requests.get(0).getName());

					// Record the time at which the customer leaves the queue and the resulting queue-lengths
					this.recordQueueLeaving(p.getTimes().get(1));
				}

				// remove the request regardless of whether or not the product has been accepted
				requests.remove(0);
			}
			if (!delivered)
				row.add(p); // Otherwise store it
		}
		return true;
	}

	// Method to record the arrival times
	private void recordQueueArrival(double tme) {
		List<Double> curr;
		if (Simulation.queueMatrix.size() == 0) {
			curr = new ArrayList<>();
			curr.add(tme);
			for (int i = 1; i <= 7; i++) {
				if (i == queueNumber) {
					curr.add(1.0);
				} else {
					curr.add(0.0);
				}
			}
			curr.add(1.0);
		} else {
			// Get the previous record to determine current record
			List<Double> prev = List.copyOf(Simulation.queueMatrix.get(Simulation.queueMatrix.size() - 1));
			curr = new ArrayList<>(prev);

			// Standard updates for the new record
			curr.set(0, tme);
			curr.set(prev.size()-1, 1.0);

			// Update corresponding queue-length after customer left the queue
			curr.set(queueNumber, (double) (this.getSize()+1));
		}

		Simulation.queueMatrix.add(curr);
		System.out.print("Queue ---> Recorded arrival time including queue-lengths: " + curr);
		System.out.println(" ---> queueMatrix.size() = " + Simulation.queueMatrix.size() + "\n");
	}

	private void recordQueueLeaving(double tme) {
		// Get the previous record to determine current record
		List<Double> prev = List.copyOf(Simulation.queueMatrix.get(Simulation.queueMatrix.size() - 1));
		List<Double> curr = new ArrayList<>(prev);

		// Standard updates for the new record
		curr.set(0, tme);
		curr.set(prev.size()-1, 2.0);

		// Update corresponding queue-length after customer left the queue
		curr.set(queueNumber, (double) this.getSize());

		// Add record to queueMatrix
		Simulation.queueMatrix.add(curr);
		System.out.print("Machine ---> Recorded time after customer leaves queue including queue-lengths: " + curr);
		System.out.println(" ---> queueMatrix.size() = " + Simulation.queueMatrix.size() + "\n");
	}

	public int getSize() {
		return row.size();
	}

	public void setToWork() {
		working = true;
	}

	public boolean getWorking() {
		return working;
	}

	public int getQueueNumber() {
		return queueNumber;
	}
}