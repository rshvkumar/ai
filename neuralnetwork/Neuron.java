package neuralnetwork;

import java.util.HashMap;

public class Neuron {
	double value = 0;
	HashMap <Neuron, Double> previous;
	
	public Neuron() {
		previous = new HashMap <Neuron, Double> ();
	}
	
	public void connect(Neuron n) {
		previous.put(n, Math.random() * 2 - 1);
	}
	
	public void compute() {
		double total = 0;
		
		for (Neuron n : previous.keySet() ) {
			total = total + n.value * previous.get(n);
		}
		
		// Compute the new value from the sigmoid
		value = 1 / (1 + Math.pow(Math.E, -total) );
	}
}
