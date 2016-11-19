package c45;

public class FuzzyFunc {

	public FuzzyFunc(){}

	int Fnum;

	int LargeK[];
	int SmallK[];

	FuzzyFunc(int Fnum){
		this.Fnum = Fnum;
		makeMembershipFunc();
	}

	void makeMembershipFunc(){

		LargeK = new int[Fnum];
		SmallK = new int[Fnum];

		for(int i=0; i<Fnum; i++){
			LargeK[i] = Fnum;
			SmallK[i] = i+1;
		}

	}

	public double calcMembership(int num, double x){

		double a = (double)(SmallK[num]-1) / (double)(LargeK[num]-1);
		double b = 1.0/(double)(LargeK[num]-1);

		double uuu = 1.0 - ( Math.abs(x - a) / b );

		if(uuu < 0.0){
			uuu = 0.0;
		}

		return uuu;
	}

}
