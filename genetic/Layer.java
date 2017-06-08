package neuralnetwork;

public class Layer {
	
	Neuron[] neuron;
	
	public Layer(int neurons, int inputs) {
		neuron = new Neuron[neurons];
		
		for (int i = 0 ; i < neuron.length ; i++) {
			neuron[i] = new Neuron(inputs);
		}
	}
	
	public Layer(Layer parent1, Layer parent2, 
			double mutationRate, double mutationRange) {
		neuron = new Neuron[parent1.neuron.length];
		
		for (int i = 0 ; i < neuron.length ; i++) {
			neuron[i] = new Neuron(parent1.neuron[i], parent2.neuron[i], mutationRate, mutationRange);
		}
	}
	
	public void compute(Layer previous) {
		for (Neuron n : neuron) {
			n.compute(previous);
		}
	}
	
	public Neuron getNeuron(int index) {
		return neuron[index];
	}
	
	public int size() {
		return neuron.length;
	}
}
