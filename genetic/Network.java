package neuralnetwork;

public class Network {
	
	Layer inputLayer;
	Layer hiddenLayer;
	Layer outputLayer;
	
	public Network(int inputs, int hiddens, int outputs) {
		inputLayer = new Layer(inputs, 0);
		hiddenLayer = new Layer(hiddens, inputs);
		outputLayer = new Layer(outputs, hiddens);
	}
	
	/**
	 * Pre-condition: parent1 and parent2 have the same configuration of
	 * inputs, hiddens, and outputs
	 * 
	 * @param parent1
	 * @param parent2
	 */
	public Network(Network parent1, Network parent2, 
			double mutationRate, double mutationRange) {
		
		inputLayer = new Layer(parent1.inputLayer.size(), 0);
		hiddenLayer = new Layer(parent1.hiddenLayer, parent2.hiddenLayer, mutationRate, mutationRange);
		outputLayer = new Layer(parent1.outputLayer, parent2.outputLayer, mutationRate, mutationRange);
	}
	
	/**
	 * Input an array of values into the network
	 * @param values
	 */
	public double[] input(double[] values) {
		// Go to each value in the array
		for (int i = 0 ; i < values.length ; i++) {
			// Get the corresponding neuron to the value
			Neuron inputNeuron = inputLayer.getNeuron(i);  
			// Set the neuron's value to the current value
			inputNeuron.value = values[i];
		}
		
		// Compute the output value
		hiddenLayer.compute(inputLayer);
		outputLayer.compute(hiddenLayer);
		
		// Copy the output neuron values into a new array
		double[] output = new double[outputLayer.size()];
		
		// Go to each output neuron and copy its value into the array
		for (int i = 0 ; i < outputLayer.size() ; i++) {
			Neuron outputNeuron = outputLayer.getNeuron(i);
			output[i] = outputNeuron.value;
		}
		
		// Return the output array
		return output;
	}
}
