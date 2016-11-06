package model;
import java.util.Random;

import gurobi.*;

public class Model{
	
	public int _s, _m, _n, _v, _ell, _w;
	public double[][][] _solutionOfPatroller;
	public double[][][] _solutionOfAdversary;
	
	public Model(int T, int Tp, int Ta, int L, int W, int ell, int w, int alpha){
		_v = w*ell; _ell = ell; _w = w;
		_s = T*alpha*w/W; 
		_m = _s*Tp/T; 
		_n = _s*Ta/T; 
	}
	
	public void solvePatroller(int sp, double[][][] zp, double wn, double ws){
		try{
		//preliminary calculations
		int constNum = 0; int temp= 0; int [] Kp = new int[_ell];
		for(int i=0; i<_ell; i++){
			Kp[i]=temp; temp+=_w;
		}
		//random adversary action estimate
		double [][][] tildeX = estimateTildeX();
		//distance matrix
		double [][] c = getDistanceMatrixOfPatroller();
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
		//variables
		GRBVar[][][] x = new GRBVar[_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					String s = "x"+i+j+t;
					x[i][j][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, s);
				} 
			}
		}	
		model.update();
		//objective function
		GRBLinExpr expr = new GRBLinExpr();
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					for(int t=0; t<_s; t++){
						expr.addTerm(wn*zp[i][j][t]*c[i][j]+ws*tildeX[i][j][t]*c[i][j], x[i][j][t]);
					}
				}
		}
		model.setObjective(expr, GRB.MAXIMIZE);
		//constraints
		//moving only once at time t
		for(int t=sp; t<sp+_m-1; t++){
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
			for(int j=0; j<_v; j++){	
				expr2.addTerm(1.0, x[k][j][sp]);
				String s2 = "c"+constNum;
				model.addConstr(expr2, GRB.EQUAL, 1.0, s2);
				constNum++;
			}
		}		
		//ending at certain nodes
		GRBLinExpr expr3 = new GRBLinExpr();
		for(int i=0; i<_v; i++){
			for(int k: Kp){	
				expr3.addTerm(1.0, x[i][k][sp+_m]);
				String s3 = "c"+constNum;
				model.addConstr(expr3, GRB.EQUAL, 1.0, s3);
				constNum++;
			}
		}
		//setting off-patrol variables to zero
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					if(t<sp-1 || t>sp+_m-1){
						GRBLinExpr expr4 = new GRBLinExpr();
						expr4.addTerm(1.0, x[i][j][t]);
						String s4 = "c"+constNum;
						model.addConstr(expr4, GRB.EQUAL, 0.0, s4);
						constNum++;
					}
				}
			}
		}
		
		//solve
		model.optimize();
		//solution
		_solutionOfPatroller = new double[_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					_solutionOfPatroller[i][j][t] = x[i][j][t].get(GRB.DoubleAttr.X);
				}
			}
		}
		//
		for(int t=0; t<_s; t++){
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					if(x[i][j][t].get(GRB.DoubleAttr.X)>0){
						System.out.println(i+" "+j+" "+t);
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
	
	public void solveAdversary(int sa, double[][][] za, double wr, double wc){
		try{
		//preliminary calculations
		int constNum = 0; int temp= _w-1; int [] Ka = {_ell};
		for(int i=0; i<_ell; i++){
			Ka[i]=temp; temp+=_w;
		}
		//random patroller action estimate
		double [][][] tildeX = estimateTildeX();
		//distance matrix
		double [][] c = getDistanceMatrixOfAdversary();
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
		//variables
		GRBVar[][][] x = new GRBVar[_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					String s = "x"+i+j+t;
					x[i][j][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,s);
				}
			}
		}	
		model.update();
		//objective function
		GRBLinExpr expr = new GRBLinExpr();
			for(int i=0; i<_v; i++){
				for(int j=0; j<_v; j++){
					for(int t=0; t<_s; t++){
						expr.addTerm(wr*za[i][j][t]*c[i][j]-wc*tildeX[i][j][t]*c[i][j], x[i][j][t]);
					}
				}
		}
		model.setObjective(expr, GRB.MAXIMIZE);
		//constraints
		//moving only once at time t
		for(int t=0; t<_s; t++){
			GRBLinExpr expr1 = new GRBLinExpr();
			for(int i=0; i<_s; i++){
				for(int j=0; j<_s; j++){
					expr1.addTerm(1.0, x[i][j][t]);
				}
			}	
			String s1 = "c"+constNum;
			model.addConstr(expr1, GRB.LESS_EQUAL, 1.0, s1);
			constNum++;
		}
		//starting from certain nodes
		GRBLinExpr expr2 = new GRBLinExpr();
		for(int k: Ka){
			for(int j=0; j<_v; j++){	
				expr2.addTerm(1.0, x[k][j][sa]);
				String s2 = "c"+constNum;
				model.addConstr(expr2, GRB.LESS_EQUAL, 1.0, s2);
				constNum++;
			}
		}
		//ending at certain nodes
		GRBLinExpr expr3 = new GRBLinExpr();
		for(int i=0; i<_v; i++){
			for(int k: Ka){	
				expr3.addTerm(1.0, x[i][k][sa+_n]);
				String s3 = "c"+constNum;
				model.addConstr(expr3, GRB.LESS_EQUAL, 1.0, s3);
				constNum++;
			}
		}
		//setting off-move variables to zero
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					if(t<sa-1 || t>sa+_n-1){
						GRBLinExpr expr4 = new GRBLinExpr();
						expr4.addTerm(1.0, x[i][j][t]);
						String s4 = "c"+constNum;
						model.addConstr(expr4, GRB.EQUAL, 0.0, s4);
						constNum++;
					}
				}
			}
		}
		//solve
		model.optimize();
		//solution
		_solutionOfAdversary = new double[_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					_solutionOfAdversary[i][j][t] = x[i][j][t].get(GRB.DoubleAttr.X);
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
	
	public double[][] getDistanceMatrixOfPatroller(){
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
					c[i][j]=-Double.MAX_VALUE;
				}
			}
		}
		return c;
	}
	
	public double[][] getDistanceMatrixOfAdversary(){
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
					c[i][j]=-Double.MAX_VALUE;
				}
				if(i==j){
					c[i][j]=1;
				}
			}
		}
		return c;
	}
	
	public double[][][] estimateTildeX(){
		Random rand = new Random(); 
		double sum = 0;
		double [][][] tildeX = new double [_v][_v][_s];
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					double r = rand.nextDouble();
					tildeX[i][j][t]=r;
					sum += r;
				}
			}
		}
		for(int i=0; i<_v; i++){
			for(int j=0; j<_v; j++){
				for(int t=0; t<_s; t++){
					tildeX[i][j][t] /=sum;
				}
			}
		}
		return tildeX;
	}
}
