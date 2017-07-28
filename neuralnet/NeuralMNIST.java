package neuralnet;

import apcs.Window;

public class NeuralMNIST {

	public static void main(String[] args) {
		MNIST train = new MNIST("train-images", "train-labels");
		MNIST test = new MNIST("t10k-images", "t10k-labels");
		
		// Make a neural network (784 inputs, 50 hiddens, and 10 outputs)
		Network network = new Network(28 * 28, 50, 10);
		//Network network = new Network(28 * 28, 300, 100, 50, 10);
		
		// Go to each training example
		double maxLearningRate = 0.5;
		double minLearningRate = 0.1;
		double learningRate = maxLearningRate;
		double learningRateDrop = (maxLearningRate - minLearningRate) / train.size();
		long start = System.currentTimeMillis();
		
		for (int i = 0 ; i < train.size() ; i++) {
			// Get the image and its label
			boolean[][] image = train.getBinaryImage(i);
			int label = train.getLabel(i);
			
			double[] input = toInput(image);
			// one-hot array
			double[] expected = oneHotArray(10, label);
			
			network.compute(input);
			network.backpropagate(expected, learningRate);
			learningRate = learningRate - learningRateDrop;
			
			// Print your progress
			if (i % 1000 == 0 || i == train.size() - 1) {
				System.out.println("Completed " + i + " training examples in " +
						(System.currentTimeMillis() - start) + " ms.");
				start = System.currentTimeMillis();
			}
		}
		
		// Measure the accuracy of the network
		int correct = 0;
		
		// Go to each test image
		for (int i = 0 ; i < test.size() ; i++) {
			// Get the image and its label
			boolean[][] image = test.getBinaryImage(i);
			int label = test.getLabel(i);
			
			// Convert the image to an input, then give it to the network
			double[] input = toInput(image);
			double[] output = network.compute(input);
			int guess = softmax(output);
			
			// If I guessed correctly
			if (guess == label) {
				correct++;
			}
		}
		// At the end, print out the accuracy
		System.out.println("Got " + correct + " right out of " + test.size());
		
		interactiveTest(network);
	}

	private static void interactiveTest(Network network) {
		Window.size(700, 600);
		MNIST test = new MNIST("t10k-images", "t10k-labels");
		
		boolean[][] grid = new boolean[28][28];
		
		int testImage = 0; 
		
		while (true) {
			Window.frame();
			// Clear the grid
			if (Window.key.pressed("space")) {
				grid = new boolean[28][28];
			}
			// Get a test image from the list of images
			if (Window.key.pressed("up") || Window.key.pressed("down") || Window.key.pressed("r")) {
				if (Window.key.pressed("up") && testImage > 0) {
					testImage--;
				}
				if (Window.key.pressed("down") && testImage + 1 < test.size()) {
					testImage++;
				}
				if (Window.key.pressed("r")) {
					testImage = Window.random(0, test.size() - 1);
				}
				grid = test.getBinaryImage(testImage);
			}
			
			
			if (Window.mouse.clicked()) {
				int x = (Window.mouse.getX() - 20) / 20;
				int y = (Window.mouse.getY() - 20) / 20;
				if (x >= 0 && x < 28 && y >= 0 && y < 28) {
					grid[x][y] = true;
				}
			}
			
			Window.out.color("red");
			Window.out.line(20, 20, 580, 20);
			Window.out.line(20, 20, 20, 580);
			Window.out.line(580, 20, 580, 580);
			Window.out.line(20, 580, 580, 580);
			
			Window.out.color("white");
			for (int x = 0 ; x < 28 ; x++) {
				for (int y = 0 ; y < 28 ; y++) {
					if (grid[x][y]) {
						Window.out.square(30 + x * 20, 30 + y * 20, 20);
					}
				}
			}
			
			double[] output = network.compute(toInput(grid));
			int guess = 0;
			double maxValue = output[0];
			
			for (int i = 1 ; i < output.length ; i++) {
				if (output[i] > output[guess]) {
					maxValue = output[i];
					guess = i;
				}
			}
			
			for (int i = 0 ; i < output.length ; i++) {
				Window.out.color("white");
				Window.out.font("Courier", 30);
				Window.out.print(i, 590, i * 50 + 50);
				
				Window.out.color((i == guess) ? "green" : "red");
				double outval = output[i] / maxValue;
				Window.out.rectangle((int) (620 + 25 * outval), i * 50 + 35, (int) (50 * outval), 25);
			}
		}
		
	}

	private static int softmax(double[] output) {
		int maxIndex = 0;
		for (int i = 0 ; i < output.length ; i++) {
			if (output[i] > output[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	private static double[] oneHotArray(int length, int hotIndex) {
		double[] array = new double[length];
		array[hotIndex] = 1;
		return array;
	}

	private static double[] toInput(boolean[][] image) {
		double[] input = new double[28 * 28];
		// go to each pixel in the image
		for (int x = 0 ; x < 28 ; x++) {
			for (int y = 0 ; y < 28 ; y++) {
				// Put a one into the array
				if (image[x][y]) {
					input[y * 28 + x] = 1;
				}
			}
		}
		// Return the array
		return input;
	}

}
