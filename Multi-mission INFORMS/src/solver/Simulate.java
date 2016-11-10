package solver;

import java.util.ArrayList;
import java.util.Iterator;
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
				if(interdictionOccured(patrollerPath, adversaryPath, m, n)!=null){
					updateTildeYes(interdictionOccured(patrollerPath, adversaryPath, m, n));
				}else{
					updateTildeNo();
				}
			}else {
				updateTildeNo();
			}
			//normalize matrices
			normalizeTildeX(_tildeXPatroller);
			normalizeTildeX(_tildeXAdversary);
			//go Step 2!
		}		
	}
	
	private void updateTildeYes (int [] interdiction){
		_tildeXPatroller[interdiction[0]][interdiction[1]][interdiction[2]] += 1;
		_tildeXAdversary[interdiction[0]][interdiction[1]][interdiction[2]] += 1;
	}
	
	private void updateTildeNo (){
		//
	}
	
	private int[] interdictionOccured(ArrayList<int[]> patrollerPath, ArrayList<int[]> adversaryPath, int m, int n){
		int p1 = patrollerPath.get(0)[2]; int p2 = patrollerPath.get(m)[2]; 
		int a1 = adversaryPath.get(0)[2]; int a2 = adversaryPath.get(n)[2]; 
		int[] interdiction = new int[3];
		if(p1>=a1){
			for (int t=0; t<a2-p1; t++){
				if (patrollerPath.get(t)[0]==adversaryPath.get(n-(a2-p1)+t)[0] & patrollerPath.get(t)[1]==adversaryPath.get(n-(a2-p1)+t)[1] |//for checking ij-ij interdiction
					patrollerPath.get(t)[0]==adversaryPath.get(n-(a2-p1)+t)[1] & patrollerPath.get(t)[1]==adversaryPath.get(n-(a2-p1)+t)[0] |//for checking ij-ji interdiction	
					patrollerPath.get(t)[1]==adversaryPath.get(n-(a2-p1)+t)[1] & adversaryPath.get(t)[0]==adversaryPath.get(n-(a2-p1)+t)[1] //for checking ij-jj interdiction
					){
					interdiction = patrollerPath.get(t);
					return interdiction;
				}
				ArrayList<Integer> aL = _solver._model.getNeighbors(patrollerPath.get(t)[1]);
				Iterator<Integer> it = aL.iterator();
				while (it.hasNext()){
					if (patrollerPath.get(t)[1]==adversaryPath.get(t)[1] & adversaryPath.get(t)[0] == it.next()){
						interdiction = patrollerPath.get(t);
						return interdiction; 
					}
				}
			}
		}
		else {
			for (int t=0; t<p2-a1; t++){
				if (patrollerPath.get(t)[0]==adversaryPath.get(m-(p2-a1)+t)[0] & patrollerPath.get(t)[1]==adversaryPath.get(m-(p2-a1)+t)[1] |//for checking ij-ij interdiction
						patrollerPath.get(t)[0]==adversaryPath.get(m-(p2-a1)+t)[1] & patrollerPath.get(t)[1]==adversaryPath.get(m-(p2-a1)+t)[0] |//for checking ij-ji interdiction	
						patrollerPath.get(t)[1]==adversaryPath.get(m-(p2-a1)+t)[1] & adversaryPath.get(t)[0]==adversaryPath.get(m-(p2-a1)+t)[1] //for checking ij-jj interdiction
						){
						interdiction = patrollerPath.get(t);
						return interdiction;
				}
				ArrayList<Integer> aL = _solver._model.getNeighbors(patrollerPath.get(t)[1]);
				Iterator<Integer> it = aL.iterator();
				while (it.hasNext()){
					if (patrollerPath.get(t)[1]==adversaryPath.get(t)[1] & adversaryPath.get(t)[0] == it.next()){
						interdiction = patrollerPath.get(t);
						return interdiction; 
					}
				}
			}
		}
		return null;
	}
	
	private boolean necessaryToCheckInterdiction(int p1, int p2, int a1, int a2){
		return !(p1>a2 | p2>a1);	
	}
	
	public void normalizeTildeX(double [][][] tildeX){
		
	}


}
