package solver;

import java.util.ArrayList;

public class Solver {

	public static int T = 12;
	public static int Tp = 6;
	public static int Ta = 3;
	public static int W = 50;
	public static int L = 50;
	public static int ell = 5;
	public static int w = 5;
	public static int alpha = 25;
	public static double wn = 1;
	public static double ws = 1-wn;
	public static double wr = .5;
	public static double wc = 1-wr;
	//
	private ArrayList<int[]> _optimalPathOfPatroller;
	private ArrayList<int[]> _optimalPathOfAdversary;
	
	//models
	model.Model _model = new model.Model(T, Tp, Ta, L, W, ell, w, alpha);
	data.Data _data = new data.Data(_model._v, _model._s, _model._ell, _model._w);
	
	public void solve(double [][][] tildeXPatroller, double [][][] tildeXAdversary){	
		//preliminaries
		double optimalObjectivePatroller = -Double.MAX_VALUE;
		double optimalObjectiveAdversary = -Double.MAX_VALUE;
		_optimalPathOfPatroller = new ArrayList<int[]>(); 
		_optimalPathOfAdversary = new ArrayList<int[]>(); 
		double [][][] zp = _data.getZP();
		double [][][] za = _data.getZA();
		//solve patroller's problem
		for(int i=0; i<_model._s-_model._m; i++){
			_model.solvePatroller(i, zp, tildeXPatroller, wn, ws);
			if(_model._objectiveValueOfPatroller>optimalObjectivePatroller){
				optimalObjectivePatroller=_model._objectiveValueOfPatroller;
				_optimalPathOfPatroller = _model._solutionOfPatroller;
			}
		}
		for(int i=0; i<_model._s-_model._n; i++){
			_model.solveAdversary(i, za, tildeXAdversary, wr, wc);
			if(_model._objectiveValueOfAdversary>optimalObjectiveAdversary){
				optimalObjectiveAdversary=_model._objectiveValueOfAdversary;
				_optimalPathOfAdversary = _model._solutionOfAdversary;
			}
		}
		//printing results
		//print();
	}
	
	public ArrayList<int[]> getPatrollerPath(){
		return _optimalPathOfPatroller;
	}
	
	public ArrayList<int[]> getAdversaryPath(){
		return _optimalPathOfAdversary;
	}
	
	public void print(){
		System.out.println("The optimal path of the patroller:");
		_model.printOptimalPath(_optimalPathOfPatroller);
		System.out.println("The optimal path of the adversary:");
		_model.printOptimalPath(_optimalPathOfAdversary);
	}
	
}
