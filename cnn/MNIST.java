package convnet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class MNIST implements Iterable<Digit>, Dataset {
	// Size of each image.
	private int width;
	private int height;
	
	// Training data.
	private byte[] imageData;
	private byte[] labelData;
	private int length;
	
	// Randomize placement and rotation of the digit
	private boolean randomize = true;
	private static final double maxValue = Math.pow(2, 24);
	
	/**
	 * Reads the image and label data from the given paths, and stores their data.
	 * @param imagePath
	 * @param labelPath
	 */
	public MNIST(String imagePath, String labelPath) {
		try {
			System.out.println("Loading MNIST data.");
			
			// Append a trailing marker.
			if (! imagePath.endsWith("/")) imagePath = imagePath + "/";
			
			// Read all training images and labels.
			imageData = Files.readAllBytes(Paths.get(imagePath));
			labelData = Files.readAllBytes(Paths.get(labelPath));
			
			// Get the length, width, and height.
			length = readInt(imageData, 4);
			width = readInt(imageData, 8);
			height = readInt(imageData, 12);
			
			// Show size.
			System.out.println("Read " + length + " digits (" + width + " x " + height + ").");
		}
		catch (IOException e) {
			System.err.println("Could not find MNIST data at " + imagePath);
		}
	}
	
	/**
	 * Returns an integer array representing the handwriting sample at the given index.
	 *  
	 * @param index
	 * @return
	 */
	public double[][] getImage(int index) {
		double[][] export = new double[width][height];
		for (int y = 0, xy = 16 + index * width * height ; y != height ; y++) {
			for (int x = 0 ; x != width ; x++) {
				int g = 255 - (imageData[xy++] & 0xff);
				export[x][y] = (0xff000000 | g | ( g << 8 ) | ( g << 16 )) / -maxValue;
			}
		}
		return export;
	}
	
	/**
	 * Returns an integer array representing the handwriting sample at the given index.
	 *  
	 * @param index
	 * @return
	 */
	public boolean[][] getBinaryImage(int index) {
		boolean[][] export = new boolean[width][height];
		for (int y = 0, xy = 16 + index * width * height ; y != height ; y++) {
			for (int x = 0 ; x != width ; x++) {
				int g = 255 - (imageData[xy++] & 0xff);
				export[x][y] = (0xff000000 | g | ( g << 8 ) | ( g << 16 )) < -1;
			}
		}
		return export;
	}
	
	/**
	 * Reads an integer from the byte buffer at the given offset.
	 * @param image - image byte array
	 * @param offset - byte offset
	 * @return the integer at the given offset.
	 */
	private int readInt(byte[] image, int offset) {
		return (image[offset + 3] & 0xff) | ((image[offset + 2] & 0xff) << 8) | 
			   ((image[offset + 1] & 0xff) << 16) | ((image[offset] & 0xff) << 24);
	}
	
	/**
	 * Returns the label for the sample at the given index.
	 * @param index
	 * @return
	 */
	public int getLabel(int index) {
		return labelData[index+8] & 0xff; 
	}
	
	/**
	 * Returns the number of samples in this MNIST dataset.
	 * @return
	 */
	public int size() {
		return length;
	}
	
	/**
	 * Returns the width of each sample.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of each sample.
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public Iterator<Digit> iterator() {
		MNIST data = this;
		
		return new Iterator<Digit>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < length;
			}

			@Override
			public Digit next() {
				Digit digit = new Digit(data, index);
				index++;
				return digit;
			}
			
		};
	}

	@Override
	public int label(int index) {
		return getLabel(index);
	}

	@Override
	public double[][] value(int index) {
		return getImage(index);
	}

	@Override
	public double[][][] value3D(int index) {
		return null;
	}

	@Override
	public boolean is3D() {
		return false;
	}
}