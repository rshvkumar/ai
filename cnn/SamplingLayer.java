package convnet;


public class SamplingLayer extends Layer {
	
	public SamplingLayer(int scale) {
		super(0, 0, 0);
		this.scale = scale;
	}

	@Override
	public void initialize(Layer previous) {
		setSize(previous.getWidth() / scale, previous.getHeight() / scale, previous.getDepth());
		initializeError();
		initializeActivations();
	}

	@Override
	public void compute(Layer previous) {
		for (int i = 0 ; i < previous.getDepth() ; i++) {
			setLevel(i, Matrix.scale(previous.getLevel(i), scale));
		}
	}

	@Override
	public void backpropagate(Layer next) {
		for (int i = 0 ; i < getDepth() ; i++) {
			double[][] sum = null;
			for (int j = 0; j < next.getDepth() ; j++) {
				double[][] error = next.getError(j);
				double[][] kernel = next.getKernel(i, j);
				
				// Rotate the kernel 180 degrees
				kernel = Matrix.rotate180(kernel);
				
				if (sum == null)
					sum = Matrix.fullConvolution(error, kernel);
				else
					sum = Matrix.add(Matrix.fullConvolution(error, kernel), sum);
			}
			setError(i, sum);
		}
	}

	@Override
	public void update(Layer previous) {
		// Unimplemented
	}

	@Override
	public int draw(int x, int y, int scale) {
		//return drawKernel(x, y, scale);
		return 0;
	}
	
}
