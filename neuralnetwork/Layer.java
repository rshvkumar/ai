package neuralnetwork;

public class Layer {
	Neuron[] neurons;
	
	public Layer(int size) {
		neurons = new Neuron[size];
		
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i] = new Neuron();
		}
	}
	
	public void set(double[] values) {
		for (int i = 0 ; i < neurons.length ; i++) {
			neurons[i].value = values[i];
		}
	}
	
	public double[] get() {
		double[] output = new double[neurons.length];
		
		for (int i = 0 ; i < neurons.length ; i++) {
			output[i] = neurons[i].value;
		}
		
		return output;
	}
	
	public void connect(Layer layer) {
		// for every neuron in this layer
		for (Neuron previous : neurons) {
			// go to every neuron in the next layer
			for (Neuron next : layer.neurons) {
				// connect the neurons together
				next.connect(previous);
			}
		}
	}
	
	public void compute() {
		// Tell every neuron in this group to compute its new value
		for (Neuron n : neurons) {
			n.compute();
		}
	}
}
