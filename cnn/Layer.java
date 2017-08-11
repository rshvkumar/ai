package convnet;

public abstract class Layer {
	// Size of the 3D volume of the layer
	private int width, height, depth;
	
	// For layers with kernel maps
	protected int kernelSize;
	
	// For pooling layers
	protected int scale = 1;
	
	// Current record in the batch
	public static int current = 0;
	
	// Output of network for each member of the batch
	protected double[][][] value;
	protected double[][][][] kernel;
	protected double[][][] error;
	
	/**
	 * Initialize the layer with the given width, height and depth.
	 * @param width - width of the layer
	 * @param height - height of the layer
	 * @param depth - depth of the layer (number of output maps)
	 */
	public Layer(int width, int height, int depth) {
		setSize(width, height, depth);
	}
	
	/**
	 * Initialize the layer with a reference to the previous layer and the
	 * batch size used in training the network.
	 */
	public abstract void initialize(Layer previous);
	
	/**
	 * Feed forward from the previous layer.
	 * @param previous
	 */
	public abstract void compute(Layer previous);
	
	/**
	 * Backpropagate errors from the next layer to this one.
	 */
	public abstract void backpropagate(Layer next);
	
	/**
	 * 
	 */
	public abstract void update(Layer previous);
	
	/**
	 * Draw the layer
	 * @return the width taken by this layer
	 */
	public abstract int draw(int x, int y, int scale);
	
	/**
	 * Sets the width and height of the layer.
	 * @param width - the width
	 * @param height - the height
	 */
	protected void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the width, height, and depth of the layer's 3D volume.
	 * @param width - the width
	 * @param height - the height
	 * @param depth - the depth (number of outmaps)
	 */
	protected void setSize(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public void initializeActivations() {
		initializeLayer(width, height, depth);
	}
	
	public void initializeLayer(int width, int height, int depth) {
		value = new double[depth][width][height];
	}
	
	public void setKernelSize(int kernelSize) {
		this.kernelSize = kernelSize;
	}
	
	public void initializeKernel(int fromSize, int toSize) {
		kernel = new double[fromSize][toSize][kernelSize][kernelSize];
		for (int i = 0 ; i < fromSize ; i++) {
			for (int j = 0 ; j < toSize ; j++) {
				Matrix.randomize(kernel[i][j], -0.01, 0.1);
			}
		}
	}
	
	public void initializeError() {
		error = new double[depth][width][height];
	}
	
	public double[][] getLevel(int index) {
		return value[index];
	}
	
	public double[][] getKernel(int from, int to) {
		return kernel[from][to];
	}
	
	public void setKernel(int from, int to, double[][] kernel) {
		this.kernel[from][to] = kernel;
	}
	
	public double[][] getError(int index) {
		return error[index];
	}
	
	public void setLevel(int index, double[][] map) {
		value[index] = map;
	}
	
	public void setError(int index, double[][] error) {
		this.error[index] = error;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
}
