package c45;

import java.util.ArrayList;

import genetics.FuzzyPattern;

public class Node{

	public int numOfFuzzy;
	public int attribute;
	public FuzzyFunc ff;
	public double divideValue;

	public int nowDepth;
	public Node parent;
    public ArrayList<Node> children = new ArrayList<Node>();

    boolean isRoot = false;
    boolean isLeaf = false;

    double[] eachClassConfidence;

    //コピぃコンストラクタ
    Node(Node node){
    	this.numOfFuzzy = node.numOfFuzzy;
    	this.attribute = node.attribute;
    	this.ff = node.ff;
    	this.divideValue = node.divideValue;

    	this.nowDepth = node.getNowDepth();
    	this.parent = new Node(node.getParent());;
    	for(int i=0; i<node.getChildren().size(); i++){
    		this.children.add( new Node(node.getChild(i)) ) ;
    	}

    	this.isRoot = node.isRoot;
    	this.isLeaf = node.isLeaf;
    	//アレイコピー
    }

    //ルート用
    Node(boolean isRoot){
    	this.nowDepth = 1;
    	this.isRoot = isRoot;
    	this.parent = null;
    }

    Node(int depth, int numOfFuzzy, Node parent){
		this.nowDepth = depth;
		this.numOfFuzzy = numOfFuzzy;
		this.parent = parent;
	}

    public double getDivideValue(){
    	return divideValue;
    }

    public void setDivideValue(double divideValue){
    	this.divideValue = divideValue;
    }

    public int getNumOfFuzzy(){
    	return numOfFuzzy;
    }

    public double calcNodeValue(int num, FuzzyPattern pat){
    	return ff.calcMembership( num, pat.getX(attribute) );
    }

    public int getCnum(){
    	return eachClassConfidence.length;
    }

    public int getFnum(){
    	return ff.Fnum;
    }

    public int getAttribute(){
    	return attribute;
    }

    public int getNumOfBranch(){
    	return children.size();
    }

    public FuzzyFunc getFF(){
    	return ff;
    }

    public void setFF(int Fnum){
    	ff = new FuzzyFunc(Fnum);
    }

    public void setAttribute(int attribute){
    	this.attribute = attribute;
    }

    public void addChild(Node node){
    	 children.add(node);
    }

    public Node getParent(){
    	return parent;
    }

    public int getNowDepth(){
    	return nowDepth;
    }

    public Node getChild(int index){
    	return children.get(index);
    }

    public ArrayList<Node> getChildren(){
    	return children;
    }

    public boolean getIsRoot(){
    	return isRoot;
    }

    public boolean getIsLeaf(){
    	return isLeaf;
    }

    public void setIsLeaf(){
    	this.isLeaf = true;
    }

    public double getClassConfidence(int classNum){
    	return eachClassConfidence[classNum];
    }

    public double[] getClassConfidence(){
    	return eachClassConfidence;
    }

    public void calcEachClassConfidence(ArrayList<FuzzyPattern> pat, int Cnum){
    	eachClassConfidence = new double [Cnum];
    	double allConfidence = 0.0;
    	for (int p = 0; p < pat.size(); p++) {
			eachClassConfidence[pat.get(p).getConClass()] += pat.get(p).getConfidence();
			allConfidence += pat.get(p).getConfidence();
		}
    	if(allConfidence == 0.0) return;

    	for (int i = 0; i < eachClassConfidence.length; i++) {
			eachClassConfidence[i] /= allConfidence;
		}
    }

    public void calcEachClassConfidence(ArrayList<FuzzyPattern> pat, int Cnum, int crisp){
    	eachClassConfidence = new double [Cnum];
    	for (int p = 0; p < pat.size(); p++) {
			eachClassConfidence[pat.get(p).getConClass()] ++;
		}
    	//int consequentClass = Utils.getMaxIndex(eachClassConfidence);
		/*System.out.print(consequentClass +" ");
		for(int i=0; i<Cnum; i++){
			System.out.print(eachClassConfidence[i] +" ");
		}System.out.println();*/
    	//eachClassConfidence[consequentClass] = 1.0;
    }


}