package c45;

import java.util.ArrayList;

import javax.swing.RootPaneContainer;

import navier.FuzzyPattern;

public class FuzzyC45 extends DecisionTree{

	public FuzzyC45(double cf, boolean isPrune){
		super(cf, isPrune);
	}

	//パラメータ
	public void buildTree(ArrayList<FuzzyPattern> pat, int Cnum, int Ndim, double maxClassNum, double minNumPatterns){

		//分割数決定
		int Fnum = 3;

		//データ複製
		ArrayList<FuzzyPattern> fpat = new ArrayList<FuzzyPattern>();
		for(int i=0; i<pat.size(); i++){
			fpat.add(  new FuzzyPattern( pat.get(i) )  );
		}

		//全体の分割情報量を計算
		double IofD = Utils.calcAveInfo( patternCount(fpat, Cnum) );
		//各属性の分割情報量を計算
		double[] allAttVal = calcAttValue(fpat, Ndim, Cnum, Fnum, IofD);

		//子ノード生成
		if(fpat.size() > minNumPatterns){
			root.
		}



	}

	void recMakeNodes(ArrayList<FuzzyPattern> pat, int Cnum, int Ndim, double maxClassNum, double minNumPatterns){


	}

	double[] calcAttValue(ArrayList<FuzzyPattern> pat, int Ndim, int Cnum, int Fnum, double IofD){

		FuzzyFunc ff = new FuzzyFunc(Fnum);
		double[] eachAttGainRate = new double[Ndim];
		for(int n=0; n<Ndim; n++){

			double[] eachFuzzySetInfo = new double[Fnum];
			double[] eachFuzzySetVal = new double[Fnum];
			for(int f=0; f<Fnum; f++){
				double[] eachClassVal = new double[Cnum];
				for (int p=0; p<pat.size(); p++){
					eachClassVal[pat.get(p).getConClass()] += ff.calcMembership( f, pat.get(p).getX(n) ) * pat.get(p).getConClass();
				}
				eachFuzzySetInfo[f] = Utils.calcAveInfo(eachClassVal);
				eachFuzzySetVal[f] = Utils.calcAll(eachClassVal);
			}

			eachAttGainRate[n] = Utils.calcGainRate(eachFuzzySetInfo, eachFuzzySetVal, IofD);
		}

		return eachAttGainRate;
	}

	double[] patternCount(ArrayList<FuzzyPattern> pat, int Cnum){
		double[] eachClassCount = new double[Cnum];
		for (int i=0; i < pat.size(); i++) {
			eachClassCount[pat.get(i).getConClass()] += pat.get(i).getConfidence();
		}
		return eachClassCount;
	}

}



