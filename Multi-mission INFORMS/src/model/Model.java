package model;
import java.util.ArrayList;
import java.util.Random;

import gurobi.*;

public class Model{
	
	public int _s, _m, _n, _v, _ell, _w;
	public ArrayList<int[]> _solutionOfPatroller;
	public ArrayList<int[]> _solutionOfAdversary;
	public int _jcol, _jrow;
	public double _objectiveValueOfPatroller = 0;
	public double _objectiveValueOfAdversary = 0;
	
	public Model(int T, int Tp, int Ta, int L, int W, int ell, int w, int alpha){
		_v = w*ell; _ell = ell; _w = w;
		_s = T*alpha*w/W; 
		_m = _s*Tp/T; 
		_n = _s*Ta/T; 
		_jcol=_w-1; _jrow=_ell-1;
	}
	
	public void solvePatroller(int sp, double[][][] zp, double [][][] tildeX, double wn, double ws){
		try{
		//preliminary calculations
		int constNum = 0; int temp= 0; int [] Kp = new int[_ell];
		for(int i=0; i<_ell; i++){
			Kp[i]=temp; temp+=_w;
		}
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
		//variables
		GRBVar[][][] x = new GRBVar[_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					String sx = "x"+i+j+t;
					x[i][j][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, sx);
				} 
			}
		}
		model.update();
		//objective function
		GRBLinExpr expr = new GRBLinExpr();
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					for(int t=0; t<_s; t++){
						expr.addTerm(wn*zp[i][j][t]+ws*(tildeX[i][j][t]+tildeX[j][i][t]+tildeX[j][j][t]), x[i][j][t]);
						ArrayList<Integer> n = getNeighbors(j);
						for(int k:n){
							expr.addTerm(ws*tildeX[k][j][t], x[i][j][t]);
						}
					}
				}
		}
		model.setObjective(expr, GRB.MAXIMIZE);
		//constraints
		//moving only once at time t
		for(int t=sp; t<sp+_m+1; t++){
			GRBLinExpr expr1 = new GRBLinExpr();
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){	
					expr1.addTerm(1.0, x[i][j][t]);
				}
			}	
			String s1 = "c"+constNum;
			model.addConstr(expr1, GRB.EQUAL, 1.0, s1);
			constNum++;
		}
		//starting from certain nodes		
		GRBLinExpr expr2 = new GRBLinExpr();
		for(int k: Kp){
			ArrayList<Integer> n = getNeighbors(k);
			for(int j:n){	
				expr2.addTerm(1.0, x[k][j][sp]);
			}
		}
		String s2 = "c"+constNum;
		model.addConstr(expr2, GRB.EQUAL, 1.0, s2);
		constNum++;	
		//ending at certain nodes
		GRBLinExpr expr3 = new GRBLinExpr();
		for(int i=0; i<_v; i++){
			for(int k: Kp){	
				expr3.addTerm(1.0, x[i][k][sp+_m]);
				
			}
		}
		String s3 = "c"+constNum;
		model.addConstr(expr3, GRB.EQUAL, 1.0, s3);
		constNum++;
		//setting off-patrol variables to zero
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					if(t<sp || t>sp+_m){
						GRBLinExpr expr4 = new GRBLinExpr();
						expr4.addTerm(1.0, x[i][j][t]);
						String s4 = "c"+constNum;
						model.addConstr(expr4, GRB.EQUAL, 0.0, s4);
						constNum++;
					}
				}
			}
		}
		
		//sub-tour elimination
		for(int t=sp; t<sp+_m; t++){
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					ArrayList<Integer> neighbors = getNeighbors(j);
					GRBLinExpr expr5 = new GRBLinExpr();
					for(int n: neighbors){
						expr5.addTerm(1.0, x[j][n][t+1]);
					}
					expr5.addTerm(-1.0, x[i][j][t]);
					String s5 = "c"+constNum;
					model.addConstr(expr5, GRB.GREATER_EQUAL, 0.0, s5);
					constNum++;
				}
			}
		}
		
		//solve
		model.optimize();
		//solution
		_objectiveValueOfPatroller = model.get(GRB.DoubleAttr.ObjVal);
		_solutionOfPatroller = new ArrayList<int[]>();
		for(int t=0; t<_s; t++){
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					if(x[i][j][t].get(GRB.DoubleAttr.X)>0){
						int [] vector = new int[]{i,j,t};
						_solutionOfPatroller.add(vector);
					}
				}
			}
		}
		//end
		model.dispose();
		env.dispose();	
		}
		catch(GRBException  e){
			e.printStackTrace();
		}
	}
	
	public void solveAdversary(int sa, double[][][] za, double [][][] tildeX, double wr, double wc){
		try{
			//preliminary calculations
			int constNum = 0; int temp= _w-1; int [] Ka = new int[_ell];
			for(int i=0; i<_ell; i++){
				Ka[i]=temp; temp+=_w;
			}		
			//model
			GRBEnv env   = new GRBEnv("Incursion");
			GRBModel model = new GRBModel(env);    
			//variables
			GRBVar[][][] x = new GRBVar[_v][_v][_s];
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					for(int t=0; t<_s; t++){
						String sx = "x"+i+j+t;
						x[i][j][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, sx);
					} 
				}
			}
			model.update();
			//objective function
			GRBLinExpr expr = new GRBLinExpr();
				for(int i=0; i<_v; i++){
					for(int j=0; j<_v; j++){
						for(int t=0; t<_s; t++){
							expr.addTerm(wr*za[i][j][t]-wc*(tildeX[i][j][t]+tildeX[j][i][t]), x[i][j][t]);
							ArrayList<Integer> n = getNeighbors(j);
							for(int k:n){
								expr.addTerm(-wc*tildeX[k][j][t], x[i][j][t]);
							}
						}
					}
			}
			model.setObjective(expr, GRB.MAXIMIZE);
			//constraints
			//moving only once at time t
			for(int t=sa; t<sa+_n+1; t++){
				GRBLinExpr expr1 = new GRBLinExpr();
				for(int i=0; i<_v; i++){
					for(int j=0; j<_v; j++){	
						expr1.addTerm(1.0, x[i][j][t]);
					}
				}	
				String s1 = "c"+constNum;
				model.addConstr(expr1, GRB.EQUAL, 1.0, s1);
				constNum++;
			}
			//starting from certain nodes		
			GRBLinExpr expr2 = new GRBLinExpr();
			for(int k: Ka){
				ArrayList<Integer> n = getNeighbors(k);
				for(int j:n){	
					expr2.addTerm(1.0, x[k][j][sa]);
				}
			}
			String s2 = "c"+constNum;
			model.addConstr(expr2, GRB.EQUAL, 1.0, s2);
			constNum++;	
			//ending at certain nodes
			GRBLinExpr expr3 = new GRBLinExpr();
			for(int i=0; i<_v; i++){
				for(int k: Ka){	
					expr3.addTerm(1.0, x[i][k][sa+_n]);
					
				}
			}
			String s3 = "c"+constNum;
			model.addConstr(expr3, GRB.EQUAL, 1.0, s3);
			constNum++;
			//setting off-patrol variables to zero
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					for(int t=0; t<_s; t++){
						if(t<sa || t>sa+_n){
							GRBLinExpr expr4 = new GRBLinExpr();
							expr4.addTerm(1.0, x[i][j][t]);
							String s4 = "c"+constNum;
							model.addConstr(expr4, GRB.EQUAL, 0.0, s4);
							constNum++;
						}
					}
				}
			}
			
			//sub-tour elimination
			for(int t=sa; t<sa+_n; t++){
				for(int i=0; i<_v; i++){
					for(int j=0; j<_v; j++){
						ArrayList<Integer> neighbors = getNeighbors(j);
						//neighbors.add(j);
						GRBLinExpr expr5 = new GRBLinExpr();
						for(int n: neighbors){
							expr5.addTerm(1.0, x[j][n][t+1]);
						}
						expr5.addTerm(-1.0, x[i][j][t]);
						String s5 = "c"+constNum;
						model.addConstr(expr5, GRB.GREATER_EQUAL, 0.0, s5);
						constNum++;
					}
				}
			}
			
			//solve
			model.optimize();
			//solution
			_objectiveValueOfAdversary = model.get(GRB.DoubleAttr.ObjVal);
			_solutionOfAdversary = new ArrayList<int[]>();
			for(int t=0; t<_s; t++){
				for(int i=0; i<_v; i++){
					for(int j=0; j<_v; j++){
						if(x[i][j][t].get(GRB.DoubleAttr.X)>0){
							int [] vector = new int[]{i,j,t};
							_solutionOfAdversary.add(vector);
						}
					}
				}
			}
			//end
			model.dispose();
			env.dispose();	
			}
			catch(GRBException  e){
				e.printStackTrace();
			}
	}

	
	public ArrayList<Integer> getNeighbors(int v){
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		if(v%_w!=0){neighbors.add(v-1);}
		if(v%_w!=_jcol){neighbors.add(v+1);}
		if(v<_w*_jrow){neighbors.add(v+_w);}
		if(v>_jcol){neighbors.add(v-_w);}
		return neighbors;
	}
	
	public void printOptimalPath(ArrayList<int[]> p){
		for(int t=0; t<p.size(); t++){
				System.out.println(p.get(t)[0]+" "+p.get(t)[1]+" "+p.get(t)[2]);
		}
	}

}
