package c45;

import methods.MersenneTwisterFast;

public class C45 extends DecisionTree{

	public C45(double cf, boolean isPrune, double maxClassRate, double minNumPatterns, MersenneTwisterFast rnd){
		super(cf, isPrune, maxClassRate, minNumPatterns, rnd);
	}



}
