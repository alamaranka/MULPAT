package solver;

public class Solver {

	public static int T = 24;
	public static int Tp = 6;
	public static int R = 50;
	public static int r = 3;
	public static int c = 3;
	public static int v = 25;
	
	public static void main(String args[]){
		model.Model model = new model.Model(T, Tp, r, R, c, v);
		//model.solve(sp, z, tildeX, wn, ws);
	}
}
