package c45;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import genetics.FuzzyPattern;
import methods.MersenneTwisterFast;

public class C45 extends DecisionTree{

	public C45(double cf, boolean isPrune, double maxClassRate, double minNumPatterns, MersenneTwisterFast rnd){
		super(cf, isPrune, maxClassRate, minNumPatterns, rnd);
	}

	//パラメータ
	public void buildTree(ArrayList<FuzzyPattern> pat, int Cnum, int Ndim) {
		//ここから再帰
		try {
			makeNodes(root, pat, Cnum, Ndim);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void makeNodes(Node node, ArrayList<FuzzyPattern> pat, int Cnum, int Ndim) throws Exception {
		//葉にする
		if(pat.size() < minNumPatterns || isOverMaxClassNum(pat, Cnum)){
			node.setIsLeaf();
			node.calcEachClassConfidence(pat, Cnum, 0);		//結論部をクリスプで計算．
			updateDepth(node.getNowDepth());				//木の深さ更新
			addLeaf();										//葉の数をカウント
			return;
		}

		//各属性の分割情報量を計算，ノードの属性選択，ノードの分割値を決定．
		calcMaxAttAndVal(pat, Ndim, Cnum, node);

		//このノードの子個体生成
		//データ分割
		ArrayList<FuzzyPattern> childPatLeft = new ArrayList<FuzzyPattern>();
		ArrayList<FuzzyPattern> childPatRight= new ArrayList<FuzzyPattern>();

		for(int p=0; p<pat.size(); p++){
			if(  pat.get(p).getX( node.getAttribute() ) <= node.getDivideValue() ){
				childPatLeft.add(  new FuzzyPattern(pat.get(p))  );
			}else{
				childPatRight.add(  new FuzzyPattern(pat.get(p))  );
			}
		}
		//左子ノード
		node.addChild(  new Node( (node.getNowDepth()+1), 0, node )  );
		addNode();	//この木のノード数をカウント
		makeNodes(node.getChild(0), childPatLeft, Cnum, Ndim);
		//右子ノード
		node.addChild(  new Node( (node.getNowDepth()+1), 1, node )  );
		addNode();	//この木のノード数をカウント
		makeNodes(node.getChild(1), childPatRight, Cnum, Ndim);

	}

	boolean isOverMaxClassNum(ArrayList<FuzzyPattern> pat, int Cnum){
		int[] numOfEachClass = new int [Cnum];
		for (int i = 0; i < pat.size(); i++) {
			numOfEachClass[pat.get(i).getConClass()]++;
		}
		int maxClass = Utils.getMaxIndex(numOfEachClass);

		double rate = numOfEachClass[maxClass] / (double)pat.size();

		return rate > maxClassRate;
	}

	double[] calcDivideExpect(int Cnum, ArrayList<FuzzyPattern> pat,  ArrayList<Integer> dividePoints){

		double[] eachDivideExpect = new double [dividePoints.size()];
		for(int d=0; d<dividePoints.size(); d++){

			double[] eachInfo = new double[2];
			double[] eachVal = new double[2];

			//バラすことにより計算効率化
			//木の左側
			double[][] eachClassVal = new double[2][Cnum];
			for (int p=0; p<dividePoints.get(d) + 1; p++){
				eachClassVal[0][pat.get(p).getConClass()]++;
			}
			eachInfo[0] = Utils.calcAveInfo(eachClassVal[0]);
			eachVal[0] = dividePoints.get(d)+1;

			//木の右側
			for (int p=dividePoints.get(d) + 1; p<pat.size(); p++){
				eachClassVal[1][pat.get(p).getConClass()]++;
			}
			eachInfo[1] = Utils.calcAveInfo(eachClassVal[1]);
			eachVal[1] = pat.size() - (dividePoints.get(d)+1);

			//それぞれの分割による期待値
			eachDivideExpect[d] = Utils.calcExpectedVal(eachInfo, eachVal);
		}

		return eachDivideExpect;
	}

	void calcMaxAttAndVal(ArrayList<FuzzyPattern> pat, int Ndim, int Cnum, Node node){

		//全体の分割情報量を計算
		double IofD = Utils.calcAveInfo( patternCount(pat, Cnum) );

		double[] eachAttGainRate = new double[Ndim];
		double[] divideValue = new double[Ndim];
		for(int n=0; n<Ndim; n++){
			//クラスと値が異なる（分割点）
			ArrayList<Integer> dividePoints = makeDividePoints(pat, n);

			if(dividePoints.size() != 0){
				double[] eachDivideExpect = calcDivideExpect(Cnum, pat, dividePoints);
				int minExpectIndex = Utils.getMinIndex(eachDivideExpect);
				int divideIndex = dividePoints.get(minExpectIndex);
				divideValue[n] = pat.get(divideIndex).getX(n);

				double Gain = IofD - eachDivideExpect[minExpectIndex];
				double num = dividePoints.get(minExpectIndex) + 1;
				double[] array = {num, (pat.size() - num)};
				double Split = Utils.calcAveInfo(array);

				eachAttGainRate[n] = Utils.calcGainRate(Gain, Split);
				//System.out.println(Gain +" "+ Split+" "+eachAttGainRate[n]);
			}else{
				eachAttGainRate[n] = Double.MIN_VALUE;
				divideValue[n] = 0.0;
				//System.out.println(eachAttGainRate[n]);
			}

		}
		//System.out.println();

		//このノードの属性選択と分割する値を決定．
		int maxAtt = Utils.getMaxIndex(eachAttGainRate);
		node.setAttribute(maxAtt);
		node.setDivideValue(divideValue[maxAtt]);
		//System.out.println(node.getAttribute() +" "+ node.getDivideValue());

	}

	public ArrayList<Integer> makeDividePoints(ArrayList<FuzzyPattern> pat, int AttNum){

		Collections.sort(pat, new patternComparator(AttNum));
		ArrayList<Integer> dividePoints = new ArrayList<Integer>();

		double nowValue = pat.get(0).getX(AttNum);
		for (int i=1; i < pat.size(); i++) {
			if( nowValue != pat.get(i).getX(AttNum) ){
				nowValue = pat.get(i).getX(AttNum);
				dividePoints.add(i-1);
			}
		}
		return dividePoints;
	}

	public class patternComparator implements Comparator<FuzzyPattern> {

		//比べる属性の番号
		int num;
		patternComparator(int num){
			this.num = num;
		}
	    //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
	    public int compare(FuzzyPattern a, FuzzyPattern b) {
	        double no1 = a.getX(num);
	        double no2 = b.getX(num);

	        //昇順でソート
	        if (no1 > no2) {
	            return 1;

	        } else if (no1 == no2) {
	            return 0;

	        } else {
	            return -1;

	        }
	    }

	}

	double[] patternCount(ArrayList<FuzzyPattern> pat, int Cnum){
		double[] eachClassCount = new double[Cnum];
		for (int p=0; p < pat.size(); p++) {
			eachClassCount[pat.get(p).getConClass()] ++;
		}
		return eachClassCount;
	}

	public void drowTree(StringBuffer text, int Cnum){
		graphTree(root, text, Cnum);
	}

	void graphTree(Node node, StringBuffer text , int Cnum){
		String indent ="";
		for(int i=0; i<node.nowDepth-1; i++){
			indent += "\t";
		}
		if(node.getIsLeaf()){
			int conClass = Utils.getMaxIndex(node.getClassConfidence());
			text.append("class: "+ conClass +"\n");
		}else{
			text.append("attribute: " + (double)node.getAttribute() +"\n");
			for(int i=0; i<node.getNumOfBranch(); i++){
				if(i==0){
					text.append(indent + "<="+ node.getDivideValue()+"|_");
				}else{
					text.append(indent + node.getDivideValue()+"<|_");
				}
				graphTree(node.getChild(i), text, Cnum);
			}
		}
	}

	 public void drowTreeWeka(StringBuffer text, int Cnum){
			graphTreeWeka(root, text, Cnum);
		}

	void graphTreeWeka(Node node, StringBuffer text , int Cnum){
		String indent ="";
		for(int i=0; i<node.nowDepth-1; i++){
			indent += "|   ";
		}
		if(node.getIsLeaf()){
			int conClass = Utils.getMaxIndex(node.getClassConfidence());
			double numOfMainC = node.getClassConfidence(conClass);
			double numOfAllC = 0;
			for(int c =0; c<Cnum; c++){
				numOfAllC += node.getClassConfidence(c);
			}
			if(numOfAllC == numOfMainC) text.append(": "+ conClass + " (" + numOfMainC + ")" + "\n");
			else  text.append(": "+ conClass + " (" + numOfAllC + "/" + ( numOfAllC - numOfMainC ) + ")" + "\n");

		}else{

			for(int i=0; i<node.getNumOfBranch(); i++){
				text.append(indent + node.getAttribute() + " ");
				if(i==0){
					text.append("<= "+ node.getDivideValue() );
				}else{
					text.append("> "+node.getDivideValue() );
				}
				if(!node.children.get(i).isLeaf) text.append("\n");

				graphTreeWeka(node.getChild(i), text, Cnum);
			}
		}
	}


	public void downTree(FuzzyPattern pat, Node node, double[] eachClassValue){
		if(node.getIsLeaf()){
			for(int i=0; i<node.getCnum(); i++){
				eachClassValue[i] += node.getClassConfidence(i);
			}
		}else{

			if(pat.getX(node.getAttribute()) <= node.getDivideValue()){
				downTree(pat, node.getChild(0), eachClassValue);
			}else{
				downTree(pat, node.getChild(1), eachClassValue);
			}

		}

	}

	public int calcNumOfCollect(ArrayList<FuzzyPattern> pat, int Cnum){

		int numOfCollect = 0;

		for (int p = 0; p < pat.size(); p++) {

			double[] classValue = new double[Cnum];
			downTree(pat.get(p), root, classValue);

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
