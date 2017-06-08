package neuralnetwork;

public class Genome {
	Network network;
	double score = 0;
	
	public Genome(int inputs, int hiddens, int outputs) {
		network = new Network(inputs, hiddens, outputs);
	}
	
	/**
	 * Construct a genome by breeding the two parents
	 * @param parent1
	 * @param parent2
	 */
	public Genome(Genome parent1, Genome parent2, 
		double mutationRate, double mutationRange) {
		network = new Network(parent1.network, parent2.network, mutationRate, mutationRange);
	}
}
