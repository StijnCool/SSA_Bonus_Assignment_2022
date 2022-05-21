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
	 * Initializes the queue and introduces a dummy machine
	 * The machine has to be specified later
	 * @param queue_type Specifies whether the queue/cash register is always "open" or "switching" from open to closed and vice versa
	 * @param queueNumber Number of the queue (helps with recording the data)
	 */
	public Queue(String queue_type, int queueNumber) {
		this.type = queue_type;
		this.working = queue_type.equals("open");
		this.row = new ArrayList<>();
		this.requests = new ArrayList<>();
		this.queueNumber = queueNumber;
	}
	
	/**
	 * Asks a queue to give a product to a machine
	 * True is returned if a product could be delivered; false if the request is queued
	 * @param machine The machine that ask a new customer/product
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
				if (row.size() == 0 && type.equals("switching")) {
					working = false;
				}
				return true; // Machine accepted the customer/product
			} else {
				return false; // Machine rejected; don't queue request
			}
		} else {
			requests.add(machine);
			return false; // queue request
		}
	}
	
	/**
	 * Offer a product to the queue
	 * It is investigated whether a machine wants the product, otherwise it is stored
	 * @param p The product to be given to the queue
	 */
	public boolean giveProduct(Product p) {
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
					// Record time at which customer left the queue and the resulting queue-lengths
					this.recordQueueLeaving(p.getTimes().get(1));
				}

				// remove the request regardless of whether the product has been accepted
				requests.remove(0);
			}
			if (!delivered)
				row.add(p); // Otherwise, store it
		}
		return true;
	}

	/**
	 * Method to record the arrival times and the resulting queue-lengths
	 * @param tme Time of arrival of customer/product
	 */
	private void recordQueueArrival(double tme) {
		List<Double> curr;
		if (Simulation.queueMatrix.size() == 0) {
			// In case this is the first arrival we need to initialise the first entry
			curr = new ArrayList<>();
			curr.add(tme);
			for (int i = 1; i <= 7; i++) {
				if (i == queueNumber) {
					curr.add(1.0);
				} else {
					curr.add(0.0);
				}
			}
			curr.add(1.0); // 1.0 here means arrival
		} else {
			// Get the previous record to determine current record
			List<Double> prev = List.copyOf(Simulation.queueMatrix.get(Simulation.queueMatrix.size() - 1));
			curr = new ArrayList<>(prev);

			// Standard updates for the new record
			curr.set(0, tme); // set time of arrival
			curr.set(prev.size()-1, 1.0); // 1.0 here means arrival at queue

			// Update corresponding queue-length after customer left the queue
			curr.set(queueNumber, (double) (this.getSize()+1));
		}

		// Add new record to queueMatrix
		Simulation.queueMatrix.add(curr);
		System.out.print("Queue ---> Recorded arrival time including queue-lengths: " + curr);
		System.out.println(" ---> queueMatrix.size() = " + Simulation.queueMatrix.size() + "\n");
	}

	/**
	 * Method to record the times of departure from queue and the resulting queue-lengths
	 * @param tme Time of departure from queue of customer/product
	 */
	private void recordQueueLeaving(double tme) {
		// Get the previous record to determine current record
		List<Double> prev = List.copyOf(Simulation.queueMatrix.get(Simulation.queueMatrix.size() - 1));
		List<Double> curr = new ArrayList<>(prev);

		// Standard updates for the new record
		curr.set(0, tme);
		curr.set(prev.size()-1, 2.0); // 2.0 here means a departure from queue

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
}