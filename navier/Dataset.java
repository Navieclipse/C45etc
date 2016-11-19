package navier;

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

}
