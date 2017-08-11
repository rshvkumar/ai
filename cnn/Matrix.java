package convnet;

public class Matrix {
	
	public static int[] range(int size) {
		int[] r = new int[size];
		for (int i = 0 ; i < r.length ; i++) {
			r[i] = i;
		}
		return r;
	}
	
	public static double[][] copy(double[][] matrix) {
		double[][] copy = new double[matrix.length][matrix[0].length];
		
		for (int x = 0 ; x < matrix.length ; x++) {
			for (int y = 0 ; y < matrix[0].length ; y++) {
				copy[x][y] = matrix[x][y];
			}
		}
		
		return copy;
	}
	
	public static double[][] oneMinus(double[][] matrix) {
		for (int x = 0 ; x < matrix.length ; x++) {
			for (int y = 0 ; y < matrix[0].length ; y++) {
				matrix[x][y] = 1 - matrix[x][y];
			}
		}
		return matrix;
	}
	
	/**
	 * Fischer-Yates shuffle
	 * @param array
	 * @return
	 */
	public static int[] shuffle(int[] array) {
		for (int i = array.length - 1 ; i > 0 ; i--) {
			int j = (int) (Math.random() * (i + 1));

			int temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
		return array;
	}
	
	public static double[][] randomize(double[][] matrix) {
		return randomize(matrix, 0, 1);
	}
	
	public static double[][] randomize(double[][] matrix, double min, double max) {
		double range = max - min;
		
		for (int x = 0 ; x < matrix.length ; x++) {
			for (int y = 0 ; y < matrix[0].length ; y++) {
				matrix[x][y] = Math.random() * range + min;
			}
		}
		return matrix;
	}
	
	public static double[][] add(double[][] matrix, double value) {
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix[0].length ; j++) {
				matrix[i][j] += value;
			}
		}
		return matrix;
	}
	
	public static double[][] rotate180(double[][] matrix) {
		matrix = Matrix.copy(matrix);
		int width = matrix.length;
		int height = matrix[0].length;
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height / 2; y++) {
				double temp = matrix[x][y];
				matrix[x][y] = matrix[x][height - y - 1];
				matrix[x][height - y - 1] = temp;
			}
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width / 2; x++) {
				double temp = matrix[x][y];
				matrix[x][y] = matrix[width - x - 1][y];
				matrix[width - x - 1][y] = temp;
			}
		}
		
		return matrix;
	}
	
	public static double[][] multiply(double[][] matrix, double value) {
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix[0].length ; j++) {
				matrix[i][j] = matrix[i][j] * value;
			}
		}
		return matrix;
	}
	
	public static double[][] multiply(double[][] matrixa, double[][] matrixb) {
		for (int x = 0 ; x < matrixa.length ; x++) {
			for (int y = 0 ; y < matrixa[0].length ; y++) {
				matrixa[x][y] = matrixa[x][y] * matrixb[x][y];
			}
		}
		return matrixa;
	}
	
	public static double[][] multiplyInverse(double[][] matrixa, double[][] matrixb) {
		for (int x = 0 ; x < matrixa.length ; x++) {
			for (int y = 0 ; y < matrixa[0].length ; y++) {
				matrixa[x][y] = matrixa[x][y] * (1 - matrixb[x][y]);
			}
		}
		return matrixa;
	}
	
	public static double[][] add(double[][] matrixa, double[][] matrixb) {
		for (int x = 0 ; x < matrixa.length ; x++) {
			for (int y = 0 ; y < matrixa[0].length ; y++) {
				matrixa[x][y] += matrixb[x][y];
			}
		}
		return matrixa;
	}
	
	public static double[][] add(double[][] matrixa, double scalea, double[][] matrixb, double scaleb) {
		for (int i = 0 ; i < matrixa.length ; i++) {
			for (int j = 0 ; j < matrixa[0].length ; j++) {
				matrixa[i][j] = matrixa[i][j] * scalea + matrixb[i][j] * scaleb;
			}
		}
		return matrixa;
	}
	
	public static double[][] sigmoid(double[][] matrix) {
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix[0].length ; j++) {
				matrix[i][j] = sigmoid(matrix[i][j]);
			}
		}
		return matrix;
	}
	
	public static double sigmoid(double value) {
		return 1 / (1 + Math.pow(Math.E, -value));
	}
	
	public static double[][] scale(double[][] matrix, int scale) {
		int width = matrix.length;
		int height = matrix[0].length;
		int outputWidth = width / scale;
		int outputHeight = height / scale;
		double[][] outMatrix = new double[outputWidth][outputHeight];
		
		if (outputWidth * scale != width || outputHeight * scale != height) {
			System.err.println("Matrix size doesn't match.");
		}
		
		int size = scale * scale;
		
		for (int x = 0; x < outputWidth; x++) {
			for (int y = 0; y < outputHeight; y++) {
				double sum = 0.0;
				for (int i = x * scale; i < (x + 1) * scale; i++) {
					for (int j = y * scale ; j < (y + 1) * scale; j++) {
						sum += matrix[i][j];
					}
				}
				outMatrix[x][y] = sum / size;
			}
		}
		return outMatrix;
	}
	
	public static double[][] maxPool(double[][] matrix, int scaleWidth, int scaleHeight) {
		int width = matrix.length;
		int height = matrix[0].length;
		int outputWidth = width / scaleWidth;
		int outputHeight = height / scaleHeight;
		double[][] outMatrix = new double[outputWidth][outputHeight];
		
		if (outputWidth * scaleWidth != width || outputHeight * scaleHeight != height) {
			System.err.println("Matrix size doesn't match.");
		}
		
		for (int x = 0; x < outputWidth; x++) {
			for (int y = 0; y < outputHeight; y++) {
				double max = Double.MIN_VALUE;
				for (int i = x * scaleWidth; i < (x + 1) * scaleWidth; i++) {
					for (int j = y * scaleHeight ; j < (y + 1) * scaleHeight; j++) {
						if (matrix[i][j] > max) {
							max = matrix[i][j];
						}
					}
				}
				outMatrix[x][y] = max;
			}
		}
		return outMatrix;
	}

	/**
	 * Extends this matrix back to the size it would be before a convolution over the given
	 * kernel, then apply the convolution to the extended matrix to produce a post-convolution
	 * matrix of the kernel applied to the matrix.
	 * 
	 * @param matrix
	 * @param kernel
	 * @return
	 */
	public static double[][] fullConvolution(double[][] matrix, double[][] kernel) {
		int width = matrix.length;
		int height = matrix[0].length;
		int kernelWidth = kernel.length;
		int kernelHeight = kernel[0].length;
		
		double[][] extendMatrix = new double[width + 2 * (kernelWidth - 1)][height + 2 * (kernelHeight - 1)];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				extendMatrix[x + kernelWidth - 1][y + kernelHeight - 1] = matrix[x][y];
			}
		}
		return convolution(extendMatrix, kernel);
	}

	/**
	 * Compute the convolution of the given kernel across the given matrix, and return the
	 * resulting matrix after the convolution.
	 * 
	 * @param matrix - the matrix being convolved
	 * @param kernel - the kernel that is doing the convolving
	 * @return the matrix where the resulting convolution has been performed
	 */
	public static double[][] convolution(double[][] matrix, double[][] kernel) {
		// Width and height of the matrix
		int width = matrix.length;
		int height = matrix[0].length;
		
		// Width and height of the kernel
		int kernelWidth = kernel.length;
		int kernelHeight = kernel[0].length;
		
		// The size of the output after the convolution
		int outputWidth = width - kernelWidth + 1;
		int outputHeight = height - kernelHeight + 1;
		
		// Create an output matrix for storing the result
		double[][] outMatrix = new double[outputWidth][outputHeight];

		// For each output element, calculate the convolution
		for (int x = 0; x < outputWidth ; x++) {
			for (int y = 0; y < outputHeight ; y++) {
				
				// Sum each pixel
				double sum = 0.0;
				
				// Go to each position in the kernel and add the matrix value scaled
				// by the kernel value at the corresponding position
				for (int kx = 0; kx < kernelWidth ; kx++) {
					for (int ky = 0; ky < kernelHeight ; ky++) {
						sum += matrix[x + kx][y + ky] * kernel[kx][ky];
					}
				}
				
				// Set the sum in the output matrix
				outMatrix[x][y] = sum;
			}
		}
		return outMatrix;

	}
	
	/**
	 * Scale up the matrix by the given width and height factor.
	 * @param matrix
	 * @param scaleWidth
	 * @param scaleHeight
	 * @return
	 */
	public static double[][] scaleUp(double[][] matrix, int scale) {
		// If the matrix is going to be unchanged by this operation
		if (scale == 1) {
			return matrix;
		}
		
		// Get the width and height of the matrix, and use it to calculate
		// the size of the resulting matrix
		int width = matrix.length;
		int height = matrix[0].length;
		double[][] outMatrix = new double[width * scale][height * scale];

		// Go to each position in the matrix
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				
				// Extend onto the resulting matrix 
				for (int kx = x * scale ; kx < (x + 1) * scale ; kx++) {
					for (int ky = y * scale ; ky < (y + 1) * scale ; ky++) {
						outMatrix[kx][ky] = matrix[x][y];
					}
				}
			}
		}
		
		// Return the matrix
		return outMatrix;
	}
}
