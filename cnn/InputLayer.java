package convnet;

public class InputLayer extends Layer {
	
	public InputLayer(int width, int height) {
		super(width, height, 1);
	}
	
	public InputLayer(int width, int height, int depth) {
		super(width, height, depth);
	}

	@Override
	public void initialize(Layer previous) {
		initializeActivations();
	}

	@Override
	public void compute(Layer previous) {
		// Unimplemented
	}

	@Override
	public void backpropagate(Layer next) {
		// Unimplemented
	}

	@Override
	public void update(Layer previous) {
		// Unimplemented
	}
	
	public void setInput(double[][] matrix) {
		double[][] inputMap = getLevel(0);
		
		for (int x = 0 ; x < getWidth() ; x++) {
			for (int y = 0 ; y < getHeight() ; y++) {
				inputMap[x][y] = matrix[x][y];
			}
		}
	}
	
	public void setInput(double[][][] matrix) {
		
		for (int i = 0 ; i < getDepth() ; i++) {
			for (int x = 0 ; x < getWidth() ; x++) {
				for (int y = 0 ; y < getHeight() ; y++) {
					value[i][x][y] = matrix[i][x][y];
				}
			}
		}
	}

	@Override
	public int draw(int x, int y, int scale) {
		int inputScale = 5 * scale / getWidth();
		
		if (getDepth() == 1) {
			for (int ix = 0 ; ix < getWidth() ; ix++) {
				for (int iy = 0; iy < getHeight() ; iy++) {
					int c = (int) (value[0][ix][iy] * 255);
					Window.out.color(c, c, c);
					Window.out.square(x + ix * inputScale, y + iy * inputScale, inputScale);
				}
			}
		}
		else if (getDepth() == 3) {
			for (int ix = 0 ; ix < getWidth() ; ix++) {
				for (int iy = 0; iy < getHeight() ; iy++) {
					Window.out.color((int) (value[0][ix][iy] * 255), (int) (value[1][ix][iy] * 255), (int) (value[2][ix][iy] * 255));
					Window.out.square(x + ix * inputScale, y + iy * inputScale, inputScale);
				}
			}
		}
		else {
			for (int i = 0 ; i < getDepth() ; i++) {
				for (int ix = 0 ; ix < getWidth() ; ix++) {
					for (int iy = 0; iy < getHeight() ; iy++) {
						int c = (int) (value[i][ix][iy] * 255);
						Window.out.color(c, c, c);
						Window.out.square(x + ix * inputScale, y + iy * inputScale + i * (scale + 2), inputScale);
					}
				}
			}
		}
		
		return scale * 5;
	}
	
}
