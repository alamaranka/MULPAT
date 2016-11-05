package data;

public class Data {
	int nRow = 3;
	int nCol = 3;
	int totalNodes = nRow*nCol;
	int t = 10;
	double probUpper = 1.00;
	double probLower = 0.10; 
	double[][][] z = new double[totalNodes][totalNodes][t];
	
	public double[][][] createRescueData(){
		double[] probMat = new double[totalNodes];
		double diff = (probUpper - probLower)/(nCol - 1);
		for (int node=0; node < totalNodes; node++) {
			if (node%nCol == 0){
				probMat[node] = probUpper;
			}
			else if (node%nCol < nCol-1){
				probMat[node] = probMat[node-1] - diff;
			}
			else {
				probMat[node] = probLower;
			}
		}
		// calculating average probabilities
		double[][] average = new double[totalNodes][totalNodes];
		for (int i = 0; i < totalNodes; i++){
			for (int j = 0; j < totalNodes; j++){
				if ((Math.abs(i-j)==1 & ((i+j)%nCol == nCol-1)) | Math.abs(i-j)==nCol){
					average[i][j] = (probMat[i] + probMat[j])/2;
				}
				else {
					average[i][j] = 0;
				}
			}
		}
		// final z
		for (int time=0; time<=t; time++){
			for (int i=0; i<=totalNodes; i++){
				for (int j=0; j<=totalNodes; j++){
					
				}
			}
		}
		return z;
	}

	
	
}
