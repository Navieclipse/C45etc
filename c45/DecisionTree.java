package c45;

import methods.MersenneTwisterFast;

public class DecisionTree {

	public Node root;
	public int depth;

	public int numberOfNodes;
	public int numberOfLeafs;
	public double traErrRate;
	public double tstErrRate;

	public boolean prune;
	public double cf;
	public double maxClassRate;
	public double minNumPatterns;


	MersenneTwisterFast rnd;

    public DecisionTree(double cf, boolean prune, double maxClassRate, double minNumPatterns, MersenneTwisterFast rnd) {
        root = new Node(true);

        this.cf = cf;
        this.prune = prune;
        this.maxClassRate = maxClassRate;
        this.minNumPatterns = minNumPatterns;
        this.rnd = new MersenneTwisterFast(rnd.nextInt());

        numberOfNodes = 1;
        numberOfLeafs = 0;
    }

    public void setTraErrRate(double traErrRate){
    	this.traErrRate = traErrRate;
    }

    public void setTstErrRate(double tstErrRate){
    	this.tstErrRate = tstErrRate;
    }

    public double getTraErrRate(){
    	return traErrRate;
    }

    public double getTstErrRate(){
    	return tstErrRate;
    }

    public void addNode(){
    	numberOfNodes++;
    }

    public void addLeaf(){
    	numberOfLeafs++;
    }

    public void decNode(){
    	numberOfNodes--;
    }

    public void decLeaf(){
    	numberOfLeafs--;
    }

    public void updateDepth(int nowDepth){
    	if(nowDepth > depth){
    		this.depth = nowDepth;
    	}
    }

    public int getDepth(){
    	return depth;
    }

    public int getNumberOfNodes(){
    	return numberOfNodes;
    }

    public int getNumberOfLeafs(){
    	return numberOfLeafs;
    }

    public boolean getIsPrune(){
    	return prune;
    }

    public void setIsPrune(boolean prune){
    	this.prune = prune;
    }

    public double getCf(){
    	return cf;
    }


}

