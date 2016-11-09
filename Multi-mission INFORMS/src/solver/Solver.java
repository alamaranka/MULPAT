package solver;

import java.util.ArrayList;

public class Solver {

	public static int T = 12;
	public static int Tp = 4;
	public static int Ta = 2;
	public static int W = 50;
	public static int L = 50;
	public static int ell = 5;
	public static int w = 5;
	public static int alpha = 25;
	public static double wn = 0.25;
	public static double ws = 1-wn;
	public static double wr = .25;
	public static double wc = 1-wr;
	//
	private ArrayList<int[]> _optimalPathOfPatroller;
	private ArrayList<int[]> _optimalPathOfAdversary;
	private int _spStar; private int _saStar;
	
	public void solve(){
		//models
		model.Model model = new model.Model(T, Tp, Ta, L, W, ell, w, alpha);
		data.Data data = new data.Data(model._v, model._s, model._ell, model._w);
		//preliminaries
		double optimalObjectivePatroller = -Double.MAX_VALUE;
		double optimalObjectiveAdversary = -Double.MAX_VALUE;
		_optimalPathOfPatroller = new ArrayList<int[]>(); 
		_optimalPathOfAdversary = new ArrayList<int[]>(); 
		_spStar = 0; _saStar = 0;
		double [][][] zp = data.getZP();
		double [][][] za = data.getZA();
		//solve patroller's problem
		for(int i=0; i<model._s-model._m; i++){
			model.solvePatroller(i, zp, wn, ws);
			if(model._objectiveValueOfPatroller>optimalObjectivePatroller){
				optimalObjectivePatroller=model._objectiveValueOfPatroller;
				_spStar = i;
				_optimalPathOfPatroller = model._solutionOfPatroller;
			}
		}
		for(int i=0; i<model._s-model._n; i++){
			model.solveAdversary(i, za, wr, wc);
			if(model._objectiveValueOfAdversary>optimalObjectiveAdversary){
				optimalObjectiveAdversary=model._objectiveValueOfAdversary;
				_saStar = i;
				_optimalPathOfAdversary = model._solutionOfAdversary;
			}
		}
		//printing results
		System.out.println("The optimal path of the patroller:");
		model.printOptimalPath(_optimalPathOfPatroller);
		System.out.println("Optimal starting time to patrol: "+_spStar);
		System.out.println("The optimal path of the adversary:");
		model.printOptimalPath(_optimalPathOfAdversary);
		System.out.println("Optimal starting time to incursion: "+_saStar);
	}
	
	public ArrayList<int[]> getPatrollerPath(){
		return _optimalPathOfPatroller;
	}
	
	public ArrayList<int[]> getAdversaryPath(){
		return _optimalPathOfAdversary;
	}
	
	public int getPatrollerTime(){
		return _spStar;
	}
	
	public int getAdversaryTime(){
		return _saStar;
	}
	
}
