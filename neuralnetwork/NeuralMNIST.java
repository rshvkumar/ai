package neuralnetwork;

import naivebayes.MNIST;

public class NeuralMNIST {

	public static void main(String[] args) {
		MNIST data = new MNIST("train-images", "train-labels");
		MNIST test = new MNIST("t10k-images", "t10k-labels");

		// Create a neural network
		Network net = new Network(28 * 28, 20, 10);
		double learningRate = 0.5;

		// Go to every training example
		for (int i = 0 ; i < data.size() ; i++) {
			// Get the image and the label
			boolean[][] image = data.getBinaryImage(i);
			int label = data.getLabel(i);

			// Convert the image into a 1D array input 
			double[] input = toInput(image);

			// Create a one-hot array for the expected value
			double[] expected = new double[10];
			expected[label] = 1;

			// Put the input into the network, then
			// backpropagate the expected value
			net.compute(input);
			net.backpropagate(expected, learningRate);
			learningRate = learningRate - 0.49 / 60000;

			if (i % 1000 == 0) {
				System.out.println("Trained " + i);
			}
		}

		// Test accuracy of the network
		int correct = 0;

		for (int i = 0 ; i < test.size() ; i++) {
			
			// Get the image and the label
			boolean[][] image = test.getBinaryImage(i);
			int label = test.getLabel(i);
			
			// Convert the 2D image into a 1D array
			double[] input = toInput(image);
			// Get the output from the neural network
			double[] output = net.compute(input);
			
			// Find the index of the maximum value (softmax)
			int max = 0;
			for (int k = 0 ; k < output.length ; k++) {
				if (output[k] > output[max]) {
					max = k;
				}
			}
			
			// The maximum value is the guess of the network
			if (max == label) {
				correct = correct + 1;
			}
		}
		
		System.out.println("Got " + correct + " out of " + test.size() + " correct.");
	}

	private static double[] toInput(boolean[][] image) {
		double[] input = new double[28 * 28];

		for (int x = 0 ; x < 28 ; x++) {
			for (int y = 0 ; y < 28 ; y++) {
				if (image[x][y]) {
					input[y * 28 + x] = 1;
				}
			}
		}

		return input;
	}

}
