package solver;

public class Solver {

	public static int T = 24;
	public static int Tp = 6;
	public static int R = 50;
	public static int r = 3;
	public static int c = 3;
	public static int v = 25;
	public static double wn = .25;
	public static double ws = 1-wn;
	
	public static void main(String args[]){
		model.Model model = new model.Model(T, Tp, r, R, c, v);
		for(int i=0; i<model._theta-model._m; i++){
			//model.solve(i, z, wn, ws);
		}
	}
}
