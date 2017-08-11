package neuralnet;

import apcs.Photo;
import apcs.Window;

import ridesharing.Slider;

public class PredictImage {

	static double[][][] output;
	static double[][][] expected;
	
	public static void main(String[] args) {
		Window.setFrameRate(10000);
		Photo photo = new Photo("keshav.png");
		photo.display();
		
		Slider learningRate = new Slider();
		learningRate.setValue(10);
		int scale = 1;
		int trainingSpeed = 100;
		
		output = new double[Window.width()][Window.height()][];
		expected = new double[photo.getWidth()][photo.getHeight()][3];
		for (int x = 0 ; x < photo.getWidth() ; x++) {
			for (int y = 0 ; y < photo.getHeight() ; y++) {
				expected[x][y][0] = photo.getRed(x, y) / 255.0;
				expected[x][y][1] = photo.getGreen(x, y) / 255.0;
				expected[x][y][2] = photo.getBlue(x, y) / 255.0;
			}
		}
		
		Network network = new Network(2, 20, 10, 3);
		
		while (true) {
			double rate = learningRate.getValue() / 100.0;
			for (int i = 0 ; i < trainingSpeed ; i++) {
				double x = Math.random();
				double y = Math.random();
				network.compute(x, y);
				network.backpropagate(
					expected[(int) (x * photo.getWidth())][(int) (y * photo.getHeight())], rate);
			}
			draw(network, scale);
			Window.frame();
		}
	}
	
	

	private static void draw(Network network, int scale) {
		double[] max = new double[] { Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE };
		double[] min = new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		
		for (int x = 0 ; x < Window.width() ; x ++) {
			for (int y = 0 ; y < Window.height() ; y++) {
				output[x][y] = network.compute(1.0 * x / Window.width(), 1.0 * y / Window.height());
				
				for (int c = 0 ; c < 3 ; c++) {
					if (output[x][y][c] > max[c]) {
						max[c] = output[x][y][c];
					}
					if (output[x][y][c] < min[c]) {
						min[c] = output[x][y][c];
					}
				}
				
			}
		}
		
		for (int x = 0 ; x < Window.width() ; x ++) {
			for (int y = 0 ; y < Window.height() ; y++) {
				for (int c = 0 ; c < 3 ; c++) {
					output[x][y][c] = (output[x][y][c] - min[c]) / (max[c] - min[c]) * 255;
				}
				
				Window.out.color((int) output[x][y][0], (int) output[x][y][1], (int) output[x][y][2]);
				Window.out.dot(x, y);
			}
		}
	}

}
