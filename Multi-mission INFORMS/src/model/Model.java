package model;
import gurobi.*;

public class Model{
	
	private int _theta, _m, _V, _rNode, _cNode;
	
	public Model(int T, int Tp, int R, int r, int c, int v){
		_theta = T*v*r/R; _m = _theta*Tp/T;
		_V = (r+1)*(c+1); _rNode=r+1; _cNode=c+1;
	}
	
	public void solve(int sp, double[][][] z, double [][][] tildeX, double wn, double ws){
		try{
		//preliminary calculations
		double [][] c = new double[_V][_V];
		for(int i=0; i<_V; i++){
				if(i-1>=0){c[i][i-1]=1;}
				if(i+1<=_cNode){c[i][i+1]=1;}
				if(i+_cNode<=_V-1){c[i][i+_cNode]=1;}
				if(i-_cNode>=0){c[i][i-_cNode]=1;}
				for(int j=0; j<_V; j++){
					if(c[i][j]!=1){
						c[i][j] = 0;
					}
				}
		}
		for(int i=0; i<_V; i++){
			System.out.println();
			for(int j=0; j<_V; j++){
				System.out.print(c[i][j]);
			}
		}
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
			
		}
		catch(GRBException  e){
			e.printStackTrace();
		}
	}
	
	
	
	
}
