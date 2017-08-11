package neuralnet;

public class Digit {
	boolean[][] image;
	double[][] value;
	int label;
	int index;
	
	public Digit(MNIST data, int index) {
		this.index = index;
		image = data.getBinaryImage(index);
		value = data.getImage(index);
		label = data.getLabel(index);
	}
	
	public boolean[][] getImage() {
		return image;
	}
	
	public double[][] getValue() {
		return value;
	}
	
	public int getLabel() {
		return label;
	}
	
	public int getIndex() {
		return index;
	}
}