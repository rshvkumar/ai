package neuralnet;

public class Layer {
	Neuron[] neurons;
	
	public Layer(int size, int previousSize) {
		neurons = new Neuron[size];
		
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i] = new Neuron(previousSize);
		}
	}
	
	public Layer(Layer parent1, Layer parent2) {
		neurons = new Neuron[parent1.neurons.length];
		
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i] = new Neuron(parent1.neurons[i], parent2.neurons[i]);
		}
	}
	
	public void set(double[] values) {
		for (int i = 0 ; i < values.length ; i++) {
			neurons[i].value = values[i];
		}
	}
	
	public double[] get() {
		double[] output = new double[neurons.length];
		// put each neuron i's value into output[i]
		for (int i = 0 ; i < output.length ; i++) {
			output[i] = neurons[i].value;
		}
		
		return output;
	}
	
	public void compute(Layer previous) {
		for (Neuron n : neurons) {
			n.compute(previous);
		}
	}

	// Back propagation for the output layer
	public void backpropagate(double[] expected) {
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i].backpropagate(expected[i]);
		}
	}
	
	// Back propagation for the hidden layer
	public void backpropagate(Layer forward) {
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i].backpropagate(forward, i);
		}
	}
	
	public void changeWeights(Layer previous, double rate) {
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i].changeWeights(previous, rate);
		}
	}
}