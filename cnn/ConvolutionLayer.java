package convnet;

/**
 * A convolution layer in a CNN.
 * 
 * @author  Keshav Saharia
 *			keshav@keshav.is
 */
public  class ConvolutionLayer extends Layer {

	/**
	 * Create the convolution layer without setting the width and height yet. This is
	 * needed since the convolution layer is usually part of a network where the width 
	 * and height are chosen during initialization.
	 * 
	 * @param kernelSize - size of the kernel
	 * @param depth - depth of this layer
	 */
	public ConvolutionLayer(int kernelSize, int depth) {
		super(0, 0, depth);
		setKernelSize(kernelSize);
	}


	@Override
	public void initialize(Layer previous) {
		// Compute the width and height after convoluting the previous layer
		setSize(previous.getWidth() - kernelSize + 1, previous.getHeight() - kernelSize + 1);

		// Initialize the activation and error volume
		initializeActivations();
		initializeError();

		// Create the kernel mappings between the previous volume and this one
		initializeKernel(previous.getDepth(), getDepth());
	}

	@Override
	public void compute(Layer previous) {
		// For each outmap, sum the convolution output
		for (int j = 0 ; j < getDepth() ; j++) {
			double[][] sum = null;

			for (int i = 0; i < previous.getDepth() ; i++) {
				// Generate the first sum
				if (sum == null)
					sum = Matrix.convolution(previous.getLevel(i), getKernel(i, j));
				// Add to the previous sum
				else
					sum = Matrix.add(sum, Matrix.convolution(previous.getLevel(i), getKernel(i, j)));
			}

			// Add the layer bias to the sum
			// sum = Matrix.add(sum, bias[j]);
			// Sigmoid the result and save it as the output map
			setLevel(j, Matrix.sigmoid(sum));
		}
	}

	@Override
	public void backpropagate(Layer next) {
		for (int i = 0 ; i < getDepth() ; i++) {
			double[][] nextError = next.getError(i);
			double[][] map = getLevel(i);
			double[][] outMatrix = Matrix.multiply(map, Matrix.oneMinus(Matrix.copy(map)));

			setError(i, Matrix.multiply(outMatrix, Matrix.scaleUp(nextError, next.scale)));
		}
	}

	@Override
	public void update(Layer previous) {
		// For each pair of outmaps
		for (int j = 0 ; j < getDepth() ; j++) {
			for (int i = 0 ; i < previous.getDepth() ; i++) {
				// Generate a delta kernel by convoluting the previous level by the
				// error matrix, producing a kernel that can be added to the existing one
				double[][] delta = Matrix.convolution(previous.getLevel(i), getError(j));

				// Multiply the existing kernel by the inverse momentum * learning rate,
				// then add it to the delta scaled by the learning rate
				delta = Matrix.add(
						getKernel(i, j), (1.0 - CNN.momentum * CNN.learningRate), 
						delta, CNN.learningRate);

				// Store the new kernel
				kernel[i][j] = delta;
			}
		}
	}

	@Override
	public int draw(int x, int y, int scale) {
		int kernelScale = scale / kernelSize;
		int kernelScalePad = scale + 2;

		for (int i = 0 ; i < kernel.length ; i++) {
			for (int j = 0 ; j < kernel[0].length ; j++) {
				double kernelMin = Double.MAX_VALUE, kernelMax = Double.MIN_VALUE;

				for (int kx = 0 ; kx < kernelSize ; kx++) {
					for (int ky = 0; ky < kernelSize ; ky++) {
						double kernelValue = kernel[i][j][kx][ky];
						if (kernelValue < kernelMin) {
							kernelMin = kernelValue;
						}
						if (kernelValue > kernelMax) {
							kernelMax = kernelValue;
						}
					}
				}
				double kernelRange = kernelMax - kernelMin;
				for (int kx = 0 ; kx < kernelSize ; kx++) {
					for (int ky = 0; ky < kernelSize ; ky++) {
						int c = (int) ((kernel[i][j][kx][ky] - kernelMin) * 255 / kernelRange);
						Window.out.color(c, c, c);
						Window.out.square(x + i * kernelScalePad + kx * kernelScale, y + j * kernelScalePad + ky * kernelScale, kernelScale);
					}
				}

			}
		}
		return kernel.length * kernelScalePad;
	}



}
