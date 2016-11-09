package solver;

import java.util.ArrayList;
import java.util.Random;

public class Simulate {
	
	public static double numberOfRun = 7;

	Solver _solver = new Solver();
	double[][][] _tildeXPatroller = _solver._data.estimateTildeX();
	double[][][] _tildeXAdversary = _solver._data.estimateTildeX();
	
	public void run(){
		int m = _solver._model._m; int n = _solver._model._n;
		for(int i=0; i<numberOfRun; i++){
			//solve
			_solver.solve(_tildeXPatroller, _tildeXAdversary);
			ArrayList<int[]> patrollerPath = _solver.getPatrollerPath();
			ArrayList<int[]> adversaryPath = _solver.getAdversaryPath();
			//interdiction?
			int p1 = patrollerPath.get(0)[2]; int p2 = patrollerPath.get(m)[2]; 
			int a1 = adversaryPath.get(0)[2]; int a2 = adversaryPath.get(n)[2]; 
			if(necessaryToCheckInterdiction(p1, p2, a1, a2)){
				if(interdictionOccured()!=null){
					updateTildeYes(interdictionOccured());
				}else{
					updateTildeNo();
				}
			}else {
				updateTildeNo();
			}
			//go Step 2!
		}		
	}
	
	private void updateTildeYes (int [] interdiction){
		//
	}
	
	private void updateTildeNo (){
		//
	}
	
	private int[] interdictionOccured(){
		int[] interdiction = new int[3];
		if(2>1){
			return interdiction;
		}
		
		return null;
	}

	private boolean necessaryToCheckInterdiction(int p1, int p2, int a1, int a2){
		
		return false;
	}

}
