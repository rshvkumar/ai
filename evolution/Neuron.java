package evolution;

public class Neuron {
	double value;
	double[] weight;
	
	/**
	 * Creates a neuron with the given number of incoming synapses.
	 * 
	 * @param inputs
	 */
	public Neuron(int inputs) {
		weight = new double[inputs];
		
		// Randomize the weights
		for (int i = 0 ; i < inputs ; i++) {
			weight[i] = Math.random() * 2 - 1;
		}
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param other - the Neuron object to copy
	 */
	public Neuron(Neuron other) {
		weight = new double[other.weight.length];
		
		for (int i = 0 ; i < other.weight.length ; i++) {
			weight[i] = other.weight[i];
		}
	}
}
