package naivebayes;

import apcs.Window;

public class CountPixels {
	public static void main(String[] args) {
		// Get the training MNIST data.
		MNIST data = new MNIST(
			"mnist/train-images-idx3-ubyte", 
			"mnist/train-labels-idx1-ubyte"
		);
		
		// Width, height, and count for each class.
		int width = data.getWidth();
		int height = data.getHeight();
		long[][][] count = new long[10][width][height];
		
		// Get the image and add it to the count
		for (int i = 0 ; i < data.size() ; i++) {
			int[][] image = data.getImage(i);
			int label = data.getLabel(i);
			
			for (int y = 0 ; y < height ; y++) {
				for (int x = 0 ; x < width ; x++) {
					count[label][x][y] += image[x][y];
				}
			}
		}
		
		// Draw each number with the given scale.
		int scale = 10;
		Window.size(width * 5 * scale, height * 2 * scale);
		Window.frame();
		
		for (int x = 0 ; x < 5 ; x++) {
			for (int y = 0 ; y < 2 ; y++) {
				int n = y * 5 + x;
				
				for (int i = 0 ; i < width ; i++) {
					for (int j = 0 ; j < height ; j++) {
						int c = (int)( count[n][i][j] / data.size() / 5000 );
						if (c > 255)
							Window.out.color(0, c, 0);
						else
							Window.out.color(c, 0, 255 - c);
						Window.out.square(
								x * width * scale + i * scale + scale / 2, 
								y * height * scale + j * scale + scale / 2, 
								scale
							);
					}
				}
				
			}
		}
		Window.frame();
	}
}
