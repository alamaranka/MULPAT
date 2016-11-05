package model;
import java.util.Random;

import gurobi.*;

public class Model{
	
	public int _theta, _m, _V, _rNode, _cNode;
	public double[][][] _solutionOfPatroller;
	
	public Model(int T, int Tp, int R, int r, int c, int v){
		_theta = T*v*r/R; _m = _theta*Tp/T;
		_V = (r+1)*(c+1); _rNode=r+1; _cNode=c+1;
	}
	
	public void solve(int sp, double[][][] z, double wn, double ws){
		try{
		//preliminary calculations
		int constNum = 0; int temp= 0; int [] Kp = {_rNode};
		for(int i=0; i<_rNode; i++){
			Kp[i]=temp; temp+=_rNode;
		}
		//random adversary action estimate
		double [][][] tildeX = estimateTildeX();
		//distance matrix
		double [][] c = getDistanceMatrix();
		//model
		GRBEnv env   = new GRBEnv("Patrol");
		GRBModel model = new GRBModel(env);    
		//variables
		GRBVar[][][] x = new GRBVar[_V][_V][_theta];
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				for(int t=0; t<_theta; t++){
					String s = "x"+i+j+t;
					x[i][j][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,s);
				}
			}
		}	
		model.update();
		//objective function
		GRBLinExpr expr = new GRBLinExpr();
			for(int i=0; i<_V; i++){
				for(int j=0; j<_V; j++){
					for(int t=0; t<_theta; t++){
						expr.addTerm(wn*z[i][j][t]*c[i][j]+ws*tildeX[i][j][t]*c[i][j], x[i][j][t]);
					}
				}
		}
		model.setObjective(expr, GRB.MAXIMIZE);
		//constraints
		//moving only once at time t
		for(int t=0; t<_theta; t++){
			GRBLinExpr expr1 = new GRBLinExpr();
			for(int i=0; i<_V; i++){
				for(int j=0; j<_V; j++){
					expr1.addTerm(1.0, x[i][j][t]);
				}
			}	
			String s1 = "c"+constNum;
			model.addConstr(expr1, GRB.EQUAL, 1.0, s1);
			constNum++;
		}
		//starting from certain nodes
		GRBLinExpr expr2 = new GRBLinExpr();
		for(int k=0; k<Kp.length; k++){
			for(int j=0; j<_V; j++){	
				expr2.addTerm(1.0, x[k][j][sp]);
				String s2 = "c"+constNum;
				model.addConstr(expr2, GRB.EQUAL, 1.0, s2);
				constNum++;
			}
		}
		//ending at certain nodes
		GRBLinExpr expr3 = new GRBLinExpr();
		for(int i=0; i<_V; i++){
			for(int k=0; k<Kp.length; k++){	
				expr3.addTerm(1.0, x[i][k][sp+_m]);
				String s3 = "c"+constNum;
				model.addConstr(expr3, GRB.EQUAL, 1.0, s3);
				constNum++;
			}
		}
		//setting off-patrol variables to zero
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				for(int t=0; t<_theta; t++){
					if(t<sp-1 || t>sp+_m-1){
						GRBLinExpr expr4 = new GRBLinExpr();
						expr4.addTerm(0.0, x[i][j][t]);
						String s4 = "c"+constNum;
						model.addConstr(expr4, GRB.EQUAL, 1.0, s4);
						constNum++;
					}
				}
			}
		}
		//solve
		model.optimize();
		//solution
		_solutionOfPatroller = new double[_V][_V][_theta];
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				for(int t=0; t<_theta; t++){
					_solutionOfPatroller[i][j][t] = x[i][j][t].get(GRB.DoubleAttr.X);
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
	
	public double[][] getDistanceMatrix(){
		int jcol=_cNode-1; int jrow=_rNode-1;
		double [][] c = new double [_V][_V];		
		for(int i=0; i<_V; i++){
			if(i%_cNode!=0){c[i][i-1]=1;}
			if(i%_cNode!=jcol){c[i][i+1]=1;}
			if(i<_cNode*jrow){c[i][i+_cNode]=1;}
			if(i>jcol){c[i][i-_cNode]=1;}
		}
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				if(c[i][j]!=1){
					c[i][j]=-Double.MAX_VALUE;
				}
			}
		}
		return c;
	}
	
	public double[][][] estimateTildeX(){
		Random rand = new Random(); 
		double sum = 0;
		double [][][] tildeX = new double [_V][_V][_theta];
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				for(int t=0; t<_theta; t++){
					double r = rand.nextDouble();
					tildeX[i][j][t]=r;
					sum += r;
				}
			}
		}
		for(int i=0; i<_V; i++){
			for(int j=0; j<_V; j++){
				for(int t=0; t<_theta; t++){
					tildeX[i][j][t] /=sum;
				}
			}
		}
		return tildeX;
	}
}
