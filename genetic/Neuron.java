package neuralnetwork;

public class Neuron {
	double value;
	double[] weight;
	
	public Neuron(int inputs) {
		weight = new double[inputs];
		for (int i = 0 ; i < weight.length ; i++) {
			weight[i] = Math.random() * 2 - 1;
		}
	}
	
	public Neuron(Neuron parent1, Neuron parent2,
			double mutationRate, double mutationRange) {
		
		weight = new double[parent1.weight.length];
		
		for (int i = 0 ; i < weight.length ; i++) {
			if (Math.random() < 0.5) { 
				weight[i] = parent1.weight[i];
			}
			else {
				weight[i] = parent2.weight[i];
			}
			
			if (Math.random() < mutationRate) {
				weight[i] += mutationRange * (Math.random() * 2 - 1);
			}
		}
	}
	
	/**
	 * Pre-condition: previous has same # of neurons as
	 * the length of the weight array.
	 * @param previous
	 */
	public void compute(Layer previous) {
		double total = 0;
		
		for (int i = 0 ; i < weight.length ; i++) {
			total += previous.neuron[i].value * weight[i];
		}
		
		// Sigmoid the total
		value = 1 / (1 + Math.pow(Math.E, -total));
	}
	
}
