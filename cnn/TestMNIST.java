package convnet;

public class TestMNIST {

	public static void main(String[] args) {
		
		CNN cnn = new CNN(
			new InputLayer(28, 28),
			new ConvolutionLayer(5, 6),
			new SamplingLayer(2),
			new ConvolutionLayer(5, 12),
			new SamplingLayer(2),
			new OutputLayer(10)
		);
		
		MNIST train = new MNIST("train-images", "train-labels");
		MNIST test = new MNIST("test-images", "test-labels");
		
		Window.size(800, 750);
		Window.setFrameRate(1000000);
		cnn.setLearningRate(0.5);
		cnn.train(train, 50, 10, true, 56);
		
//		cnn.train(train, 50, 20);
		
		
		int correct = 0;
		for (Digit digit : test) {
			if (cnn.compute(digit.getValue()) == digit.getLabel()) {
				correct++;
			}
		}
		
		System.out.println(correct + " / " + test.size());
		
	}

}
