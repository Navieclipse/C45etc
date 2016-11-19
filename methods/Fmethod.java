package methods;

import navier.Cons;
import navier.Dataset;
import navier.FuzzyPattern;

public class Fmethod {

	public Fmethod(){}

	static int KK[] = {1,2,2,3,3,3,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7,7};					//メンバシップの時のK
	static int kk[] = {1,1,2,1,2,3,1,2,3,4,1,2,3,4,5,1,2,3,4,5,6,1,2,3,4,5,6,7};					//メンバシップの時のk

	int LargeK[];
	int SmallK[];

	static double w[]  = {100.0, 1.0, 1.0};							//ウェイト

	//******************************************************************************//*
    //関数

	public void KKkk(int maxFnum){
		int arrayNum = 0;
		for(int i=0; i<maxFnum; i++){
			arrayNum += (i+1);
		}
		LargeK = new int[arrayNum];
		SmallK = new int[arrayNum];

		int num = 0;
		for(int i=0; i<maxFnum; i++){
			for(int j=0; j<(i+1); j++){
				LargeK[num] = i+1;
				SmallK[num] = j+1;
				num++;
			}
		}
	}

	public static double menberMulPure(Dataset data,int DataNum, int rule[], int Ndim){

		int i;
		double ans = 1.0;

		for(i=0;i<Ndim;i++){
			ans *= menbershipCalc(rule[i], data.getPattern().get(DataNum).getX(i));
		}

		return ans;
	}

	public static double menberMulPure2(FuzzyPattern p, int rule[]){

		double ans = 1.0;
		int Ndim = rule.length;
		for(int i=0; i<Ndim; i++){
			ans *= menbershipCalc(rule[i], p.getX(i));
		}

		return ans;
	}

	public static double menbershipCalc(int num, double x){
		double uuu = 0.0;
		double a, b;

		if(num==0){
			uuu = 1.0;
		}
		else{

			a = (double)(kk[num]-1) / (double)(KK[num]-1);
			b = 1.0/(double)(KK[num]-1);

			uuu = 1.0 - ( Math.abs(x - a) / b );

			if(uuu < 0.0){
				uuu = 0.0;
			}
		}
		return uuu;
	}

	public static int[] selectRnd(int Ndim, MersenneTwisterFast rnd){

		int rule[] = new int[Ndim];
		boolean isRandDC = Cons.isRandDC;
		double dcRate;

		if(isRandDC){
			dcRate = (double)(((double)Ndim - (double)Cons.Len)/(double)Ndim);
		}
		else{
			dcRate = rnd.nextDouble() - 0.1;
		}

		boolean allDC = true;
		for (int n = 0; n < Ndim; n++) {
			if (rnd.nextDouble() < dcRate) {
				rule[n] = 0;
			} else {
				rule[n] = rnd.nextInt(Cons.Fnum) + 1;
				allDC = false;
			}
		}

		//全てのルールがDCの場合ランダムに１つ要素を付ける．
		if(allDC){
			int choro = rnd.nextInt(Ndim);
			rule[choro] = rnd.nextInt(Cons.Fnum) + 1;
		}

		return rule;
	}

	//適応度関数
	public static double fitness(double f1, double f2, double f3){
		return (double)(w[0] *  f1) + (double)(w[1] * f2) + (double)(w[2] * f3);
	}

}
