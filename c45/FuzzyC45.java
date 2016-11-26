package c45;

import java.util.ArrayList;
import java.util.Arrays;

import methods.MersenneTwisterFast;
import navier.FuzzyPattern;

public class FuzzyC45 extends DecisionTree{

	int maxFnum;

	public FuzzyC45(double cf, boolean isPrune, int maxFnum, double maxClassRate, double minNumPatterns, MersenneTwisterFast rnd){
		super(cf, isPrune, maxClassRate, minNumPatterns, rnd);
		this.maxFnum = maxFnum;
	}

	//パラメータ
	public void buildTree(ArrayList<FuzzyPattern> pat, int Cnum, int Ndim){
		boolean[] selectedAtt = new boolean[Ndim];
		//ここから再帰
		try {
			makeNodes(root, pat, Cnum, Ndim, selectedAtt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void makeNodes(Node node, ArrayList<FuzzyPattern> pat, int Cnum, int Ndim, boolean[] selectedAtt) throws Exception {
		//葉にする
		if(pat.size() < minNumPatterns || isSelectedFullAtt(selectedAtt, Ndim) || isOverMaxClass(pat, Cnum)){
			node.setIsLeaf();
			node.calcEachClassConfidence(pat, Cnum);	//結論部を割合で計算．
			updateDepth(node.getNowDepth());			//木の深さ更新
			addLeaf();									//葉の数をカウント
			return;
		}

		//分割数決定
		int Fnum = decideFnum(maxFnum);
		node.setFF(Fnum);

		//全体の分割情報量を計算
		double IofD = Utils.calcAveInfo( patternCount(pat, Cnum) );
		//各属性の分割情報量を計算
		double[] allAttVal = calcAttValue(pat, Ndim, Cnum, Fnum, IofD, node.getFF(), selectedAtt);
		//このノードの属性選択
		int maxAtt = Utils.getMaxIndex(allAttVal);
		selectedAtt[maxAtt] = true;
		node.setAttribute(maxAtt);

		//このノードの子個体生成
		for(int f=0; f<Fnum; f++){
			//子ノード用パターン集合(信頼度計算済み)生成からの子ノード生成
			ArrayList<FuzzyPattern> childPat = makeChildPat(f, pat, maxAtt, node.getFF());
			node.addChild(  new Node( (node.getNowDepth()+1), f, node )  );
			addNode();	//この木のノード数をカウント
			boolean[] childSeleAtt = Arrays.copyOf(selectedAtt, selectedAtt.length);
			makeNodes(node.getChild(f), childPat, Cnum, Ndim, childSeleAtt);
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

	boolean isOverMaxClass(ArrayList<FuzzyPattern> pat, int Cnum){
		int[] numOfEachClass = new int [Cnum];
		for (int i = 0; i < pat.size(); i++) {
			numOfEachClass[pat.get(i).getConClass()]++;
		}
		int maxClass = Utils.getMaxIndex(numOfEachClass);
		double rate = numOfEachClass[maxClass] / pat.size();

		return rate > maxClassRate;
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
					eachClassVal[pat.get(p).getConClass()] += ff.calcMembership( f, pat.get(p).getX(n) ) * pat.get(p).getConfidence();
				}
				eachFuzzySetInfo[f] = Utils.calcAveInfo(eachClassVal);
				eachFuzzySetVal[f] = Utils.calcAll(eachClassVal);
			}

			eachAttGainRate[n] = Utils.calcGainRate(eachFuzzySetInfo, eachFuzzySetVal, IofD);
			//eachAttGainRate[n] = Utils.calcModifGainRate(eachFuzzySetInfo, eachFuzzySetVal, IofD, ff.Fnum, 1, -1);
		}

		return eachAttGainRate;
	}

	double[] patternCount(ArrayList<FuzzyPattern> pat, int Cnum){
		double[] eachClassCount = new double[Cnum];
		for (int p=0; p < pat.size(); p++) {
			eachClassCount[pat.get(p).getConClass()] += pat.get(p).getConfidence();
		}
		return eachClassCount;
	}

	void graphTree(Node node, StringBuffer text , int Cnum){
		String indent ="";
		for(int i=0; i<node.nowDepth-1; i++){
			indent += "\t";
		}
		if(node.getIsLeaf()){
			for (int i = 0; i < Cnum; i++) {
				text.append(indent + "\t C"+i+": " + node.getClassConfidence(i) +"\n");
			}
		}else{
			text.append(indent + "Att" + node.getAttribute() +"\n");
			for(int i=0; i<node.getNumOfBranch(); i++){
				text.append(indent + "Branch"+i+"\n");
				graphTree(node.getChild(i), text, Cnum);
			}
		}
	}

	public void drowTree(StringBuffer text, int Cnum){
		graphTree(root, text, Cnum);
	}

	public void downTree(FuzzyPattern pat, Node node, double followValue, double[] eachClassValue){
		if(node.getIsLeaf()){
			for(int i=0; i<node.getCnum(); i++){
				eachClassValue[i] += followValue * node.getClassConfidence(i);
			}
		}else{
			for(int f=0; f<node.getFnum(); f++){
				double newFollowValue = followValue * node.calcNodeValue(f, pat);
				if(newFollowValue == 0.0) continue;
				downTree(pat, node.getChild(f), newFollowValue, eachClassValue);
			}
		}

	}

	public int calcNumOfCollect(ArrayList<FuzzyPattern> pat, int Cnum){

		int numOfCollect = 0;

		for (int p = 0; p < pat.size(); p++) {

			double[] classValue = new double[Cnum];
			downTree(pat.get(p), root, 1.0, classValue);

			int resultClass = -1;
			double max = 0.0;
			for(int c=0; c<Cnum; c++){
				if(classValue[c] > max){
					max = classValue[c];
					resultClass = c;
				}
			}
			if(resultClass == pat.get(p).getConClass()){
				numOfCollect++;
			}
		}

		return numOfCollect;
	}

}



