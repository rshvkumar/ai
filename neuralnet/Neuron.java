package neuralnet;

public class Neuron {
	double value;
	double error;

	double[] weight;

	// Create a neuron of the given size
	public Neuron(int size) {
		weight = new double[size];

		// Initialize a random number for each weight
		for (int i = 0 ; i < weight.length ; i++) {
			weight[i] = Math.random() * 2 - 1;
		}
	}

	// Combine two neurons
	public Neuron(Neuron parent1, Neuron parent2) {
		weight = new double[parent1.weight.length];

		// Randomly pick a weight either from parent 1
		// or from parent 2
		for (int i = 0 ; i < weight.length ; i++) {
			if (Math.random() < 0.5) {
				weight[i] = parent1.weight[i];
			}
			else {
				weight[i] = parent2.weight[i];
			}
			// TODO: mutations
			if (Math.random() < 0.1) {
				weight[i] += Math.random() * 0.2 - 0.1;
			}
		}
	}

	// Compute the value
	public void compute(Layer previous) {
		double total = 0;

		// Go to every neuron in the previous layer
		for (int i = 0 ; i < previous.neurons.length ; i++) {
			total += previous.neurons[i].value * weight[i];
		}

		// Sigmoid it up
		value = 1 / (1 + Math.pow(Math.E, -total));
	}

	// Back propagation for the output layer
	public void backpropagate(double expected) {
		// error = (value error) * derivative of sigmoid(value)
		error = (expected - value) * value * (1 - value);
	}

	// Back propagation for the hidden layer
	public void backpropagate(Layer forward, int index) {
		double totalError = 0;
		// Go to each neuron in the layer in front of this one
		for (int i = 0 ; i < forward.neurons.length ; i++) {
			Neuron forwardNeuron = forward.neurons[i];
			double forwardWeight = forwardNeuron.weight[index];
			totalError += forwardWeight * forwardNeuron.error;
		}
		// Multiply total error by the derivative of sigmoid(value)
		error = totalError * value * (1 - value);
	}
	
	public void changeWeights(Layer previous, double rate) {
		for (int w = 0 ; w < weight.length ; w++) {
			weight[w] += rate * error * previous.neurons[w].value;
		}
	}
	
	
}