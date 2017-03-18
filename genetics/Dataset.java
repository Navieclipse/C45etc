package genetics;

import java.util.ArrayList;


public class Dataset {

	//コンストラクタ
	Dataset(){}

	public Dataset(int Ndim, int Cnum, int DataSize, ArrayList<FuzzyPattern> patterns){

		this.Ndim = Ndim;
		this.Cnum = Cnum;
		this.DataSize = DataSize;
		this.patterns = patterns;

	}

	public Dataset(int Ndim, int Cnum, int DataSize, double pattern[][], int ansClass[]){

		this.Ndim = Ndim;
		this.Cnum = Cnum;
		this.DataSize = DataSize;

		for(int i=0; i<ansClass.length; i++){
			this.patterns.add(new FuzzyPattern(pattern[i], ansClass[i]) );
		}

	}

	/******************************************************************************/

	int Ndim;
	int Cnum;
	int DataSize;

	ArrayList<FuzzyPattern> patterns = new ArrayList<FuzzyPattern>();

	/******************************************************************************/
	//メソッド

	public void setPattern(ArrayList<FuzzyPattern> patterns){
		this.patterns = patterns;
	}

	public void addPattern(Double[] pattern){
		patterns.add(new FuzzyPattern(pattern));
	}

	public void setNdim(int num){
		Ndim = num;
	}

	public void setCnum(int num){
		Cnum = num;
	}

	public void setDataSize(int num){
		DataSize = num;
	}

	public ArrayList<FuzzyPattern> getPattern(){
		return patterns;
	}

	public int getNdim(){
		return Ndim;
	}

	public int getCnum(){
		return Cnum;
	}

	public int getDataSize(){
		return DataSize;
	}

	//分割用
	public int getAnswer(int p){
		return patterns.get(p).getConClass();
	}

	public double getX(int dim, int p){
		return patterns.get(p).getX(dim);
	}


}
