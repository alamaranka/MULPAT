package data;

public class Data {
	
	private int _v;
	private int _s;
	
	public Data(int v, int s){
		_v=v; 
		_s=s;
	}

	public double[][][] getZP(){
		double[][][] z = new double[_v][_v][_s];
		//your code
		return z;
	}
	
}
