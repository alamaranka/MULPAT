package data;

public class Data {
	int nRow = 3;
	int nCol = 3;
	int totalNodes = nRow*nCol;
	int totalTime = 10;
	double probUpper = 1.00;
	double probLower = 0.10; 
	double[][][] z = new double[totalNodes][totalNodes][totalTime];
	
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
		// calculate z
		int sum = 0;
		for (int t=0; t<=totalTime; t++){
			for (int i=0; i<=totalNodes; i++){
				for (int j=0; j<=totalNodes; j++){
					z[i][j][t] = ((t<=totalTime*1/3)?1:0)*average[i][j]/3 //low probability in first 1/3 of day (12AM to 8AM)
							+ ((t>totalTime*1/3 & t<=totalTime*2/3)? 1:0)*average[i][j]/2 //medium probability in second 1/3rd of day (8AM to 4PM)
							+ ((t>totalTime*2/3 & t<=totalTime*5/6)? 1:0)*average[i][j] //high probability in the next 1/6th of day (4PM to 8PM)
							+ ((t>totalTime*5/6)? 1:0)*average[i][j]/2; //medium probability in the next 1/6th of day (8PM to 12AM)
					sum += z[i][j][t];
				}
			}
		}
		// normalize z
		for (int t=0; t<=totalTime; t++){
			for (int i=0; i<=totalNodes; i++){
				for (int j=0; j<=totalNodes; j++){
					z[i][j][t] /= sum;
				}
			}
		}
		return z;
	}
}
