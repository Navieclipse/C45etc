package c45;

import methods.MersenneTwisterFast;

public class C45 extends DecisionTree{

	public C45(double cf, boolean isPrune, double maxClassNum, double minNumPatterns, MersenneTwisterFast rnd){
		super(cf, isPrune, maxClassNum, minNumPatterns, rnd);
	}

}
