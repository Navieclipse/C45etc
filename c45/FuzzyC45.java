package c45;

import java.util.ArrayList;

import methods.MersenneTwisterFast;
import navier.FuzzyPattern;

public class FuzzyC45 extends DecisionTree{

	int maxFnum;

	public FuzzyC45(double cf, boolean isPrune, int maxFnum, double maxClassNum, double minNumPatterns, MersenneTwisterFast rnd){
		super(cf, isPrune, maxClassNum, minNumPatterns, rnd);
		this.maxFnum = maxFnum;
	}

	//パラメータ
	public void buildTree(ArrayList<FuzzyPattern> pat, int Cnum, int Ndim){
		boolean[] selectedAtt = new boolean[Ndim];
		makeNodes(root, pat, Cnum, Ndim, selectedAtt);
	}

	void makeNodes(Node node, ArrayList<FuzzyPattern> pat, int Cnum, int Ndim, boolean[] selectedAtt){

		if(pat.size() < minNumPatterns && isSelectedFullAtt(selectedAtt, Ndim)){	//葉にする
			node.setIsLeaf();
			node.calcEachClassConfidence(pat, Cnum);	//結論部を割合で計算．
			updateDepth(node.getNowDepth());			//木の深さ更新
			return;
		}
		//分割数決定
		int Fnum = decideFnum(maxFnum);
		FuzzyFunc ff = new FuzzyFunc(Fnum);

		//全体の分割情報量を計算
		double IofD = Utils.calcAveInfo( patternCount(pat, Cnum) );
		//各属性の分割情報量を計算
		double[] allAttVal = calcAttValue(pat, Ndim, Cnum, Fnum, IofD, ff, selectedAtt);
		//このノードの属性選択
		double maxVal = 0.0;
		int maxAtt = 0;
		for(int i=0; i<allAttVal.length; i++){
			if(allAttVal[i] > maxVal){
				maxAtt = i;
				maxVal = allAttVal[i];
			}
		}
		selectedAtt[maxAtt] = true;
		node.setAttribute(maxAtt);

		//このノードのの子個体生成
		if( && )
		updateDepth(root.getNowDepth()+1);
		for(int f=0; f<Fnum; f++){
			//子ノード用パターン集合(信頼度計算済み)生成からの子ノード生成
			ArrayList<FuzzyPattern> childPat = makeChildPat(f, pat, maxAtt, ff);
			root.addChild(  new Node( (root.getNowDepth()+1), root)  );
			addNode();	//この木のノード数
			//ここから再帰
			makeNodes(root.getChild(f), childPat, Cnum, Ndim, selectedAtt);
		}
	}

	ArrayList<FuzzyPattern> makeChildPat(int numOfFnum, ArrayList<FuzzyPattern> pat, int maxAtt, FuzzyFunc ff){

		ArrayList<FuzzyPattern> childpat = new ArrayList<FuzzyPattern>();
		for(int p=0; p<pat.size(); p++){
			double membershipVal = ff.calcMembership( numOfFnum, pat.get(p).getX(maxAtt) );
			if(membershipVal > 0.0){
				childpat.add(  new FuzzyPattern( pat.get(p), membershipVal )  );
			}
		}
		return childpat;

	}

	boolean isSelectedFullAtt(boolean[] selectedAtt, int Ndim){
		int numOfSelected = 0;
		for (int i = 0; i < selectedAtt.length; i++) {
			if(selectedAtt[i]) numOfSelected++;
		}
		return numOfSelected == Ndim;
	}

	int decideFnum(int maxFnum){
		//int Fnum = rnd.nextInt(maxFnum) + 1;
		return 3;
	}

	double[] calcAttValue(ArrayList<FuzzyPattern> pat, int Ndim, int Cnum, int Fnum, double IofD, FuzzyFunc ff, boolean[] selectedAtt){

		double[] eachAttGainRate = new double[Ndim];
		for(int n=0; n<Ndim; n++){
			if(selectedAtt[n]) continue;

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



