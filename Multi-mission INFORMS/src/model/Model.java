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
		
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
			
		}
		catch(GRBException  e){
			e.printStackTrace();
		}
	}
	
	
	
	
}
