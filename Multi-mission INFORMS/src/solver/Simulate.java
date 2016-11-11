package solver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Simulate {
	
	public static double numberOfRun = 3;
	public static double interdictionValue = .5;
	public static double noninterdictionCost = .5;
	public ArrayList<ArrayList<int[]>> _pathsOfPatroller = new ArrayList<ArrayList<int[]>>();
	public ArrayList<ArrayList<int[]>> _pathsOfAdversary = new ArrayList<ArrayList<int[]>>();
	public ArrayList<int[]> _interdictions = new ArrayList<int[]>();

	Solver _solver = new Solver();
	double[][][] _tildeXPatroller = _solver._data.estimateTildeX();
	double[][][] _tildeXAdversary = _solver._data.estimateTildeX();
	
	public void run() throws FileNotFoundException{
		int m = _solver._model._m; int n = _solver._model._n;
		for(int i=0; i<numberOfRun; i++){
			//solve
			_solver.solve(_tildeXPatroller, _tildeXAdversary);
			ArrayList<int[]> patrollerPath = _solver.getPatrollerPath(); _pathsOfPatroller.add(patrollerPath);
			ArrayList<int[]> adversaryPath = _solver.getAdversaryPath(); _pathsOfAdversary.add(adversaryPath);
			//interdiction?
			int p1 = patrollerPath.get(0)[2]; int p2 = patrollerPath.get(m)[2]; 
			int a1 = adversaryPath.get(0)[2]; int a2 = adversaryPath.get(n)[2]; 
			if(necessaryToCheckInterdiction(p1, p2, a1, a2)){
				if(interdictionOccured(patrollerPath, adversaryPath, m, n)!=null){
					_interdictions.add(interdictionOccured(patrollerPath, adversaryPath, m, n));
					updateTildeYes(interdictionOccured(patrollerPath, adversaryPath, m, n));
				}else{
					updateTildeNo(patrollerPath, adversaryPath);
				}
			}else {
				updateTildeNo(patrollerPath, adversaryPath);
			}
			//normalize matrices
			normalizeTildeX(_tildeXPatroller);
			normalizeTildeX(_tildeXAdversary);
			//go Step 2!
		}		
		print(m, n);
	}
	
	private void updateTildeYes (int [] interdiction){
		_tildeXPatroller[interdiction[0]][interdiction[1]][interdiction[2]] += interdictionValue;
		_tildeXAdversary[interdiction[0]][interdiction[1]][interdiction[2]] += interdictionValue;
	}
	
	private void updateTildeNo (ArrayList<int[]> patrollerPath, ArrayList<int[]> adversaryPath){
		for(int i=0; i<patrollerPath.size(); i++){
			_tildeXPatroller[patrollerPath.get(i)[0]][patrollerPath.get(i)[1]][patrollerPath.get(i)[2]] -=noninterdictionCost;
			double temp = _tildeXPatroller[patrollerPath.get(i)[0]][patrollerPath.get(i)[1]][patrollerPath.get(i)[2]];
			if(temp<0){_tildeXPatroller[patrollerPath.get(i)[0]][patrollerPath.get(i)[1]][patrollerPath.get(i)[2]]=0;}
		}
		
		for(int i=0; i<adversaryPath.size(); i++){
			_tildeXAdversary[adversaryPath.get(i)[0]][adversaryPath.get(i)[1]][adversaryPath.get(i)[2]] -= noninterdictionCost;
			double temp = _tildeXAdversary[adversaryPath.get(i)[0]][adversaryPath.get(i)[1]][adversaryPath.get(i)[2]];
			if(temp<0){_tildeXAdversary[adversaryPath.get(i)[0]][adversaryPath.get(i)[1]][adversaryPath.get(i)[2]]=0;}
		}
		
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
	
	private void normalizeTildeX(double [][][] tildeX){
		double sum =0;
		for(int i=0; i<_solver._model._v; i++){
			for(int j=0; j<_solver._model._v; j++){
				for(int t=0; t<_solver._model._s; t++){
					sum += tildeX[i][j][t];
				}
			}
		}
		for(int i=0; i<_solver._model._v; i++){
			for(int j=0; j<_solver._model._v; j++){
				for(int t=0; t<_solver._model._s; t++){
					tildeX[i][j][t] /=sum;
				}
			}
		}
	}

	private void print(int m, int n) throws FileNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream("results.txt"));
		System.setOut(out);
		////////////////
		System.out.println("Patroller's Path:");
		System.out.println("------------------");
		for(int r=0; r<numberOfRun; r++){
			int t = r+1;
			System.out.println("Simulation"+t+":");
			for(int i=0; i<m+1; i++){
				System.out.println(_pathsOfPatroller.get(r).get(i)[0]+" "+_pathsOfPatroller.get(r).get(i)[1]+" "+_pathsOfPatroller.get(r).get(i)[2]);
			}
		}
		System.out.println();
		System.out.println("Adversary's Path:");
		System.out.println("------------------");
		for(int r=0; r<numberOfRun; r++){
			int t = r+1;
			System.out.println("Simulation"+t+":");
			for(int i=0; i<n+1; i++){
				System.out.println(_pathsOfAdversary.get(r).get(i)[0]+" "+_pathsOfAdversary.get(r).get(i)[1]+" "+_pathsOfAdversary.get(r).get(i)[2]);
			}
		}
		System.out.println();
		System.out.println("Interdictions occurred:");
		System.out.println("------------------");
		if(!_interdictions.isEmpty())
			for(int i=0; i<_interdictions.size(); i++){
			System.out.println(_interdictions.get(i)[0]+" "+_interdictions.get(i)[1]+" "+_interdictions.get(i)[2]);
			}
		}
	
}
