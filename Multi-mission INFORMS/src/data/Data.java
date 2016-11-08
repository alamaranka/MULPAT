package data;

public class Data {
	
	private int _v;
	private int _s;
	private int _ell;
	private int _w; 
	
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
			if (node/_w == 0){
				probMat[node] = probLower;	
			}
			else if (node/_w < _w-1){
				probMat[node] = probMat[node-1] + diff;
			}
			else {
				probMat[node] = probUpper;
			}
		}
		// calculating average probabilities
		double[][] average = new double[_v][_v];
		double [][] c = getC();
		for (int i = 0; i < _v; i++){
			for (int j = 0; j < _v; j++){
				average[i][j] = ((c[i][j]==1)?1:0)*(probMat[i] + probMat[j])/2;
			}
		}
		// calculate z
		int sum = 0;
		for (int t=0; t<_s; t++){
			for (int i=0; i<_v; i++){
				for (int j=0; j<_v; j++){
					z[i][j][t] = ((t<=_s*1/3)?1:0)*average[i][j]/3 //low probability in first 1/3 of day (12AM to 8AM)
							+ ((t>_s*1/3 & t<=_s*2/3)? 1:0)*average[i][j]/2 //medium probability in second 1/3rd of day (8AM to 4PM)
							+ ((t>_s*2/3 & t<=_s*5/6)? 1:0)*average[i][j] //high probability in the next 1/6th of day (4PM to 8PM)
							+ ((t>_s*5/6)? 1:0)*average[i][j]/2; //medium probability in the next 1/6th of day (8PM to 12AM)
					sum += z[i][j][t];
				}
			}
		}
		// normalize z
		for (int t=0; t<_s; t++){
			for (int i=0; i<_v; i++){
				for (int j=0; j<_v; j++){
					z[i][j][t] /= sum;
				}
			}
		}
		return z;
	}

	public double[][][] getZA(){
		double [][][] z = new double[_v][_v][_s];
		double [][] c = getC();		
		double [][] redSnapper = {{0.004127757,	0.005854278, 0.026890318, 0.003783775, 0.032595546},
				{0.01255322, 0.01472265, 0.02122790, 0.00000000, 0.02575735},
				{0.053440966, 0.074663301, 0.010621700, 0.008179715, 0.033285575},
				{0.06149286, 0.07463176, 0.06630607, 0.00000000, 0.04090737},
				{0.09046278, 0.12055458, 0.11539861, 0.00000000, 0.10254193}};
		for(int t=0; t<_s; t++){
			for (int i=0; i<_v; i++){
				for (int j=0; j<_v; j++){
					int a = i/_w; 
					int b = i%_w;
					int f = j/_w; 
					int g = j%_w;
					z[i][j][t] = ((c[i][j]==1)?1:0)*(redSnapper[a][b]+redSnapper[f][g])/2;					
				}
			}	
		}		
		return z;
	}
	
	public double[][] getC(){
		int jcol=_w-1; int jrow=_ell-1;
		double [][] c = new double [_v][_v];		
		for(int i=0; i<_v; i++){
			if(i%_w!=0){c[i][i-1]=1;}
			if(i%_w!=jcol){c[i][i+1]=1;}
			if(i<_w*jrow){c[i][i+_w]=1;}
			if(i>jcol){c[i][i-_w]=1;}
		}
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				if(c[i][j]!=1){
					c[i][j]=0.0;
				}
				if(i==j){
					c[i][j]=1;
				}
			}
		}
		return c;
	}
	
}
