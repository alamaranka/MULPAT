package data;

public class Data {
	
	private int _v;
	private int _s;
	int _ell;
	int _w; 
	
	public Data(int v, int s, int ell, int w){
		_v=v; _s=s; _ell=ell; _w=w;
	}
	
	public double[][][] getZP(){
		double [][][] z = new double[_v][_v][_s];
		double probUpper = 1.00;
		double probLower = 0.10;
		double[] probMat = new double[_v];
		double diff = (probUpper - probLower)/(_w - 1);
		for (int node=0; node < _v; node++) {
			if (node%_w == 0){
				probMat[node] = probUpper;
			}
			else if (node%_w < _w-1){
				probMat[node] = probMat[node-1] - diff;
			}
			else {
				probMat[node] = probLower;
			}
		}
		// calculating average probabilities
		double[][] average = new double[_v][_v];
		for (int i = 0; i < _v; i++){
			for (int j = 0; j < _v; j++){
				if ((Math.abs(i-j)==1 & ((i+j)%_w == _w-1)) | Math.abs(i-j)==_w){
					average[i][j] = (probMat[i] + probMat[j])/2;
				}
				else {
					average[i][j] = 0;
				}
			}
		}
		// calculate z
		int sum = 0;
		for (int t=0; t<=_s; t++){
			for (int i=0; i<=_v; i++){
				for (int j=0; j<=_v; j++){
					z[i][j][t] = ((t<=_s*1/3)?1:0)*average[i][j]/3 //low probability in first 1/3 of day (12AM to 8AM)
							+ ((t>_s*1/3 & t<=_s*2/3)? 1:0)*average[i][j]/2 //medium probability in second 1/3rd of day (8AM to 4PM)
							+ ((t>_s*2/3 & t<=_s*5/6)? 1:0)*average[i][j] //high probability in the next 1/6th of day (4PM to 8PM)
							+ ((t>_s*5/6)? 1:0)*average[i][j]/2; //medium probability in the next 1/6th of day (8PM to 12AM)
					sum += z[i][j][t];
				}
			}
		}
		// normalize z
		for (int t=0; t<=_s; t++){
			for (int i=0; i<=_v; i++){
				for (int j=0; j<=_v; j++){
					z[i][j][t] /= sum;
				}
			}
		}
		return z;
	}

	public double[][][] getZA(){
		double [][][] z = new double[_v][_v][_s];
	
		return z;
	}
}
