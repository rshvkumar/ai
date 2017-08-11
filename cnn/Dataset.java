package convnet;

public interface Dataset {
	
	public int label(int index);
	public double[][] value(int index);
	
	public double[][][] value3D(int index);
	public boolean is3D();
	
	public int size();
	
	public int getWidth();
	public int getHeight();
	
}
