package neuralnet;

import apcs.Window;

public class TestXOR {

	public static void main(String[] args) {
		Window.setFrameRate(1000);
		
		Network network = new Network(2, 3, 1);
		double learningRate = 0.5;
		
		while (true) {
			network.compute(new double[] { 0, 0 });
			network.backpropagate(new double[] { 0 }, learningRate);
			network.compute(new double[] { 0, 1 });
			network.backpropagate(new double[] { 1 }, learningRate);
			network.compute(new double[] { 1, 0 });
			network.backpropagate(new double[] { 1 }, learningRate);
			network.compute(new double[] { 1, 1 });
			network.backpropagate(new double[] { 0 }, learningRate);
			
			draw(network);
		}
		
	}

	private static void draw(Network network) {
		for (int x = 0 ; x < Window.width() ; x ++) {
			for (int y = 0 ; y < Window.height() ; y++) {
				double[] output = network.compute(1.0 * x / Window.width(), 1.0 * y / Window.height());
				int color = (output[0] > 0.5) ? 255 : 0;
				Window.out.color(color, color, color);
				Window.out.square(x, y, 1);
			}
		}
		Window.frame();
	}

}
