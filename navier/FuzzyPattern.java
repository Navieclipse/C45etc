package navier;

public class FuzzyPattern {

	//コンストラクタ
	FuzzyPattern(){}

	public FuzzyPattern(Double[] pattern){
		int Ndim = pattern.length - 1;
		x = new double [Ndim];
		for (int i = 0; i < Ndim; i++) {
			x[i] = pattern[i];
		};
		conClass = pattern[Ndim].intValue();
	}

	public FuzzyPattern(FuzzyPattern pat){
		this.x = pat.getXs();
		this.conClass = pat.getConClass();
		this.confidence = pat.getConfidence();
	}

	public FuzzyPattern(FuzzyPattern pat, double confidence){
		this.x = pat.getXs();
		this.conClass = pat.getConClass();
		this.confidence = confidence;
	}

	/******************************************************************************/

	double[] x;
	int conClass;
	double confidence = 1.0;

	/******************************************************************************/

	public double[] getXs(){
		return x;
	}

	public double getX(int i){
		return x[i];
	}

	public int getConClass(){
		return conClass;
	}

	public void setConfidence(double confidence){
		this.confidence = confidence;
	}

	public double getConfidence(){
		return confidence;
	}

}
