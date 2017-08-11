package neuralnet;

public class Network {
	Layer[] layers;
	
	// new Network(784, 100, 10)
	public Network(int... sizes) {
		// Create an array of Layer objects
		layers = new Layer[sizes.length];
		
		layers[0] = new Layer(sizes[0], 0);
		for (int i = 1 ; i < sizes.length ; i++) {
			layers[i] = new Layer(sizes[i], sizes[i - 1]);
		}
	}
	
	public Network(Network parent1, Network parent2) {
		layers = new Layer[parent1.layers.length];
		
		for (int i = 0 ; i < layers.length ; i++) {
			layers[i] = new Layer(parent1.layers[i], parent2.layers[i]);
		}
	}

	public double[] compute(double ... inputs) {
		// Put the inputs into the input layer
		layers[0].set(inputs);
		
		// Tell each subsequent layer to compute
		for (int i = 1 ; i < layers.length ; i++) {
			layers[i].compute(layers[i - 1]);
		}
		
		// Return the value of the last layer
		return layers[layers.length - 1].get();
	}
	
	// TODO: implement backpropagation
	public void backpropagate(double[] expected, double rate) {
		layers[layers.length - 1].backpropagate(expected);
		
		for (int i = layers.length - 2 ; i > 0 ; i--) {
			layers[i].backpropagate(layers[i + 1]);
		}
		
		for (int i = layers.length - 1 ; i > 0 ; i--) {
			layers[i].changeWeights(layers[i - 1], rate);
		}
	}
}