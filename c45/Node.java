package c45;

import java.util.ArrayList;

public class Node{

	public int nowDepth;
	public Node parent;
    public ArrayList<Node> children;

    boolean isRoot = false;
    boolean isLeaf = false;

    //コピぃコンストラクタ
    Node(Node node){
    	this.nowDepth = node.getNowDepth();
    	this.parent = new Node(node.getParent());
    	this.children = node.getChildren();
    	for(int i=0; i<node.getChildren().size(); i++){
    		this.children.add( new Node(node.getChild(i)) ) ;
    	}
    }

    //ルート用
    Node(boolean isRoot){
    	this.nowDepth = 0;
    	this.isRoot = isRoot;
    	this.parent = null;
    }

    Node(int depth, Node parent){
		this.nowDepth = depth;
		this.parent = parent;
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


}