package neuralnetwork;

public class Network {
	Layer input;
	Layer hidden;
	Layer output;
	
	public Network(int inputs, int hiddens, int outputs) {
		// Create the groups of neurons (layers)
		input = new Layer(inputs);
		hidden = new Layer(hiddens);
		output = new Layer(outputs);
		
		// Connect the layers together
		input.connect(hidden);
		hidden.connect(output);
	}
	
	public double[] compute(double[] inputs) {
		// Put the inputs into the input layer neurons
		input.set(inputs);
		// Tell each layer to compute its new value
		hidden.compute();
		output.compute();
		// Return the values of the output layer
		return output.get();
	}
	
	public void backpropagate(double[] expected, double rate) {
		// Go to each output neuron
		for (int i = 0 ; i < output.neurons.length ; i++) {
			Neuron outputNeuron = output.neurons[i];
			
			// Get the value and the error from the expected value
			double value = outputNeuron.value;
			double error = expected[i] - value;
			
			// Go to each hidden neuron that is connected to this output neuron
			for (Neuron hiddenNeuron : outputNeuron.previous.keySet()) {
				// Get the value and the weight of the connection to this neuron
				double hiddenValue = hiddenNeuron.value;
				double weight = outputNeuron.previous.get(hiddenNeuron);
				
				// Calculate the slope
				double partialDerivative = -value * (1 - value) * hiddenValue * error;
				
				// Calculate the new weight by moving along the curve
				double newWeight = weight - rate * partialDerivative;
				
				outputNeuron.previous.put(hiddenNeuron, newWeight);
			}
		}
		
		// Go to each hidden neuron
		for (Neuron hiddenNeuron : hidden.neurons) {
			double value = hiddenNeuron.value;
			
			// Go to each previous input neuron
			for (Neuron inputNeuron : hiddenNeuron.previous.keySet()) {
				double inputValue = inputNeuron.value;
				double inputWeight = hiddenNeuron.previous.get(inputNeuron);
				double sum = 0;
				
				// Go to each output neuron that this hidden neuron is connected to
				for (int i = 0 ; i < output.neurons.length ; i++) {
					Neuron outputNeuron = output.neurons[i];
					// Get the weight, value, and error of this neuron
					double weight = outputNeuron.previous.get(hiddenNeuron);
					double outputValue = outputNeuron.value;
					double outputError = expected[i] - outputValue;
					
					// Calculate the partial derivative for the weight of this connection from
					// the hidden neuron to the output neuron
					double partialDerivative = -outputValue * (1 - outputValue) * outputError * weight;
					
					// Add the partial derivatives together
					sum = sum + partialDerivative;
				}
				
				double inputPartialDerivative = value * (1 - value) * inputValue * sum;
				double newWeight = inputWeight - rate * inputPartialDerivative;
				hiddenNeuron.previous.put(inputNeuron, newWeight);
			}
		}
	}
}
