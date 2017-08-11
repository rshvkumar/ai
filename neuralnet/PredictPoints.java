package neuralnet;

import java.util.ArrayList;

import apcs.Window;
import ridesharing.Slider;

public class PredictPoints {

	public static void main(String[] args) {
		// User defined constants
		String[] color = { "red", "green", "yellow", "blue" };
		int[] networkSize = { 2, 6, 4, color.length };
		double learningRate = 0.1;
		int epochs = 10;
		
		// Setup the window and scale
		Window.size(500, 500);
		int scale = 2;
		int width = Window.width() / scale;
		int height = Window.height() / scale;
		
		Network net = new Network(networkSize);
		
		// Store the input to the neural network for calculating the background color
		// in a 3D array, where the x, y coordinate is the first two indexes, and the
		// last index is 0 for x and 1 for y
		double[][][] position = new double[width][height][2];
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y ++) {
				position[x][y][0] = 1.0 * x / height;
				position[x][y][1] = 1.0 * y / height;
			}
		}
		
		// List of points that have been added to the viewer
		ArrayList <Point> points = new ArrayList <Point> ();
		// The color of the point currently being added
		int currentColor = 0;
		
		while (true) {
			Window.frame();
			
			// Add a new point at the mouse position when clicked
			if (Window.mouse.clicked()) {
				points.add(new Point(Window.mouse.getX(), Window.mouse.getY(), currentColor, color.length));
				Window.mouse.waitForRelease();
			}
			
			// Clear all the points when the space key is pressed
			if (Window.key.pressed("space")) {
				points.clear();
			}
			// Reset the network when the n key is pressed
			if (Window.key.pressed("n")) {
				net = new Network(networkSize);
			}
			
			// Check for changing the current color
			for (int i = 1 ; i <= color.length ; i++) {
				if (Window.key.pressed("" + i)) {
					currentColor = i - 1;
				}
			}
			
			// Compute the input for each position
			for (int x = 0 ; x < width ; x++) {
				for (int y = 0 ; y < height ; y++) {
					int max = softmax(net.compute(position[x][y]));
					Window.out.color(color[max]);
					Window.out.square(x * scale + scale / 2, y * scale + scale / 2, scale);
				}
			}
			
			// Draw the current color
			Window.out.color("black");
			Window.out.circle(Window.mouse.getX(), Window.mouse.getY(), 5);
			Window.out.color(color[currentColor]);
			Window.out.circle(Window.mouse.getX(), Window.mouse.getY(), 4);
			
			for (Point point : points) {
				point.draw(color);
			}
			for (int e = 0 ; e < epochs ; e++) {
				for (Point point : points) {
					net.compute(point.input);
					net.backpropagate(point.expected, learningRate);
				}
			}
		}
	}
	
	private static int softmax(double[] output) {
		int max = 0;
		for (int i = 1 ; i < output.length ; i++) {
			if (output[i] > output[max]) {
				max = i;
			}
		}
		return max;
	}

	static class Point {
		double[] input;
		double[] expected;
		
		int x;
		int y;
		
		int classIndex;
		int totalClasses;
		
		public Point(int x, int y, int classIndex, int totalClasses) {
			this.x = x;
			this.y = y;
			this.classIndex = classIndex;
			this.totalClasses = totalClasses;
			
			// Cache the input and expected output for network computing and 
			// backpropagating the expected output
			input = new double[] { 1.0 * x / Window.width(), 1.0 * y / Window.height() };
			expected = new double[totalClasses];
			expected[classIndex] = 1;
		}
		
		public void draw(String[] color) {
			Window.out.color("black");
			Window.out.circle(x, y, 4);
			Window.out.color(color[classIndex]);
			Window.out.circle(x, y, 3);
		}
	}
}
