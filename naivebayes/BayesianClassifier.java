package bayes;

public class BayesianClassifier {
	
	// Test the classifier
	public static void main(String[] args) {
		BayesianClassifier bayes = new BayesianClassifier();
		MNIST test = new MNIST(
			"t10k-images-idx3-ubyte", 
			"t10k-labels-idx1-ubyte"
		);
		
		// Count number of correct classifications
		double correct = 0;
		
		for (int i = 0 ; i < test.size() ; i++) {
			boolean[][] image = test.getBinaryImage(i);
			int label = test.getLabel(i);
			
			// Check the classifier to see what it thinks
			int result = bayes.classify(image);
			
			// If it was correct, add to the count
			if (result == label) {
				correct++;
			}
		}
		
		System.out.println("Got " + (correct / test.size() * 100) + "% correct.");
		
	}
	
	// Data for this class
	MNIST data;
	double[][][] frequency = new double[10][28][28];
	double[][] pixelFrequency = new double[28][28];
	int[] count = new int[10];

	public BayesianClassifier() {
		data = new MNIST(
				"train-images-idx3-ubyte", 
				"train-labels-idx1-ubyte"
				);

		train();
	}
	
	public int classify(boolean[][] image) {
		double max = 0;
		int maxDigit = 0;
		
		// Find the maximum digit
		for (int digit = 0 ; digit < 10 ; digit++) {
			double p = 1;
			double digit_p = 1; //0.1; // count[digit] / data.size();
			
			// Go to every pixel and resolve P(digit | image[x][y])  
			for (int y = 0 ; y < 28 ; y++) {
				for (int x = 0 ; x < 28 ; x++) {
					
					// If it is a black pixel
					if (image[x][y]) {
						p *= digit_p * frequency[digit][x][y] / pixelFrequency[x][y];
					}
					// If it is a white pixel
					else {
						p *= digit_p * (1 - frequency[digit][x][y]) / (1 - pixelFrequency[x][y]);
					}
					
				}
			}
			
			// Check if this is the max probability
			if (p > max) {
				max = p;
				maxDigit = digit;
			}
		}
		
		return maxDigit;
	}

	private void train() {

		// Get the frequencies for each digit
		for (int i = 0 ; i < data.size() ; i++) {
			boolean[][] image = data.getBinaryImage(i);
			int label = data.getLabel(i);

			// Add to the count for this digit
			count[label]++;

			// Add to the frequency table for each pixel
			for (int y = 0 ; y < 28 ; y++) {
				for (int x = 0 ; x < 28 ; x++) {
					if (image[x][y]) {
						frequency[label][x][y]++;
						pixelFrequency[x][y]++;
					}
				}
			}
		}

		// Resolve frequency to probability
		for (int digit = 0 ; digit < 10 ; digit++) {
			for (int y = 0 ; y < 28 ; y++) {
				for (int x = 0 ; x < 28 ; x++) {
					frequency[digit][x][y] = (frequency[digit][x][y] + 1) / (count[digit] + 1); 
				}
			}
		}
		
		// Resolve pixel frequency to probability
		for (int y = 0 ; y < 28 ; y++) {
			for (int x = 0 ; x < 28 ; x++) {
				pixelFrequency[x][y] = (pixelFrequency[x][y] + 1) / (data.size() + 1); 
			}
		}
	}
	
	

}
