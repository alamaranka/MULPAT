package solver;

public class Solver {

	public static int T = 24;
	public static int Tp = 6;
	public static int Ta = 6;
	public static int W = 50;
	public static int L = 50;
	public static int ell = 5;
	public static int w = 5;
	public static int alpha = 25;
	public static double wn = .25;
	public static double ws = 1-wn;
	public static double wr = .25;
	public static double wc = 1-wr;
	
	public static void main(String args[]){
		//preliminaries
		model.Model model = new model.Model(T, Tp, Ta, L, W, ell, w, alpha);
		data.Data data = new data.Data(model._v, model._s);
		double [][][] zp = data.getZP();
		//solve patroller's problem
		for(int i=0; i<model._s-model._m; i++){
			model.solvePatroller(i, zp, wn, ws);
		}
	}
}
