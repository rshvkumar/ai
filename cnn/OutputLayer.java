package convnet;

public class OutputLayer extends ConvolutionLayer {

	// The number of classes in this output layer
	int classes;
	
	public OutputLayer(int classes) {
		super(0, classes);
		setSize(1, 1);
		this.classes = classes;
	}

	@Override
	public void initialize(Layer previous) {
		// Set the kernel to 
		setKernelSize(previous.getWidth());
		initializeKernel(previous.getDepth(), classes);
		
		initializeActivations();
		initializeError();
	}
	
	public int maxClass() {
		int max = 0;
		for (int i = 1 ; i < classes ; i++) {
			if (value[max][0][0] < value[i][0][0]) {
				max = i;
			}
		}
		return max;
	}
	
	/**
	 * Given the correct index of the 
	 * @param label
	 */
	public void setError(int label) {
		for (int i = 0 ; i < getDepth() ; i++) {
			// Get the target value and the actual value
			double target = (label == i) ? 1 : 0;
			double outputValue = value[i][0][0];
			
			// Set the error at the given index to the value
			error[i][0][0] = outputValue * (1 - outputValue) * (target - outputValue);
		}
	}

	/**
	 * The output layer does not need to backpropagate since its errors are
	 * directly calculated in the setError method above as the gradient of the
	 * error curve.
	 */
	@Override
	public void backpropagate(Layer next) {
		// Unimplemented
	}

	@Override
	public int draw(int x, int y, int scale) {
		for (int i = 0 ; i < classes ; i++) {
			int c = (int) (value[i][0][0] * 255);
			Window.out.color(c, c, c);
			Window.out.square(x + scale / 2, y + scale / 2 + i * scale, scale * 4 / 5);
		}
		return scale;
	}

}
