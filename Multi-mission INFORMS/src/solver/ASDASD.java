package solver;

public class ASDASD {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int _V=9; int _rNode=3; int _cNode=3;
		double [][] c = new double[_V][_V];
		for(int i=0; i<_V; i++){
			if(i-1>=0 && i-1%_cNode!=_cNode-1){c[i][i-1]=1;}
			if(i+1<=_cNode && i+1%_cNode!=0){c[i][i+1]=1;}
			if(i+_cNode<=_V-1){c[i][i+_cNode]=1;}
			if(i-_cNode>=0){c[i][i-_cNode]=1;}
		}
		int z = 0; int f = 0;
		for(int i=0; i<_V; i++){
			System.out.println();
			for(int j=0; j<_V; j++){
				if(c[i][j]!=1){
					c[i][j] = 0;
				}
				System.out.print(c[i][j]+"\t");
			}
		}
	}

}
