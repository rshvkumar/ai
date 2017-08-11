package convnet;

/**
 * 
 * 
 */
public class CNN {
	
	// learning rate and momentum
	public static double learningRate = 0.1;
	public static double momentum = 0;
	
	// For drawing layers
	public static int padding = 10;
	
	// Array of Layer objects
	Layer[] layer;
	
	// Input and output layer
	InputLayer inputLayer;
	OutputLayer outputLayer;
	
	/**
	 * Initialize the convolution neural network with the given set of layers.
	 * @param layers - an array of Layer objects representing each layer in the CNN
	 */
	public CNN(Layer ... layers) {
		this.layer = layers;
		inputLayer = (InputLayer) layers[0];
		outputLayer = (OutputLayer) layers[layers.length - 1];
		
		inputLayer.initialize(null);
		
		// Initialize each layer with a reference to the previous layer
		for (int i = 1 ; i < layers.length ; i++) {
			layers[i].initialize(layers[i - 1]);
		}
	}
	
	/**
	 * Set the learning rate of the CNN.
	 * @param rate
	 */
	public void setLearningRate(double rate) {
		learningRate = rate;
	}
	
	// Train the CNN with a default batch size of 50 and without drawing the kernels
	public void train(Dataset dataset, int epochs) {
		train(dataset, 50, epochs, false, 0);
	}
	
	// Train the dataset with the given batch size
	public void train(Dataset dataset, int batchSize, int epochs) {
		train(dataset, batchSize, epochs, false, 0);
	}
	
	public void train(Dataset dataset, int batchSize, int epochs, boolean draw, int scale) {
		
		// Array of all indexes in the dataset. This array gets shuffled and the
		// subarray from index 0 to index batchSize is passed through the network,
		// then the weights and kernels are updated.
		int[] index = Matrix.range(dataset.size());
		
		// Repeat the training process for the given number of epochs
		for (int e = 0 ; e < epochs ; e++) {
			
			// Store the number of correct 
			int correct = 0, total = 0;
			
			// Repeatedly take a permutation of the index array and push it through
			int repetitions = (int) Math.ceil(dataset.size() / batchSize);
			for (int r = 0 ; r < repetitions ; r++) {
				Matrix.shuffle(index);
				
				// For each randomly chosen index in this batch
				for (int i = 0 ; i < batchSize ; i++) {
					int result = (dataset.is3D()) ? 
							compute(dataset.value3D(index[i])) :
							compute(dataset.value(index[i]));
					backpropagate(dataset.label(index[i]));
					
					if (result == dataset.label(index[i])) {
						correct++;
					}
					total++;
				}
				
				// Update kernels and weights
				update();
				if (draw) {
					draw(scale);
				}
				
				if (r % 100 == 0)
					System.out.println("Completed repetition " + r + " out of " + repetitions);
			}
			
			System.out.print("Completed epoch " + (e + 1));
			System.out.println(" with accuracy " + (100.0 * correct / total) + "%");
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public int compute(double[][][] input) {
		// Copy the input matrix into the input layer
		inputLayer.setInput(input);
		
		// Compute each hidden layer and output layer
		for (int i = 1; i < layer.length ; i++) {
			layer[i].compute(layer[i - 1]);
		}
		
		// Return the softmax class of the output layer
		return outputLayer.maxClass();
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public int compute(double[][] input) {
		// Copy the input matrix into the input layer
		inputLayer.setInput(input);
		
		// Compute each hidden layer and output layer
		for (int i = 1; i < layer.length ; i++) {
			layer[i].compute(layer[i - 1]);
		}
		
		// Return the softmax class of the output layer
		return outputLayer.maxClass();
	}
	
	/**
	 * Backpropagate based on the correct class
	 * @param correctClass
	 */
	public void backpropagate(int correctClass) {
		// Set the error for the output layer
		outputLayer.setError(correctClass);
		
		// Set the errors for each hidden layer with a reference to the
		// layer after it (used in backpropagation)
		for (int i = layer.length - 2 ; i > 0 ; i--) {
			layer[i].backpropagate(layer[i + 1]);
		}
	}
	
	/**
	 * 
	 */
	private void update() {
		for (int l = 1; l < layer.length ; l++) {
			layer[l].update(layer[l - 1]);
		}
	}
	
	public void draw(int scale) {
		draw(layer.length, scale);
	}
	
	public void draw(int maxLayer, int scale) {
		int startx = 0;
		for (int i = 0 ; i < maxLayer ; i++) {
			int width = layer[i].draw(padding + startx, padding, scale);
			if (width > 0) {
				startx += width + padding;
			}
		}
		Window.frame();
	}
}
