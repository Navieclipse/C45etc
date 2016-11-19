package c45;

import java.util.ArrayList;

public class DecisionTree {

	public Node root;
	public int depth;

	public int numberOfNodes;
	public int numberOfLeafs;

	public boolean prune;

	public double cf;

    public DecisionTree(double cf, boolean prune) {
        root = new Node(true);
        root.children = new ArrayList<Node>();

        this.cf = cf;
        this.prune = prune;

        numberOfNodes = 0;
        numberOfLeafs = 0;

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

