package c45;

public class Utils {

	public static int getMaxIndex(double[] array){		//同率のときは若いインデックスの勝ち．
		int maxIndex = 0;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if(array[i] > max){
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static int getMinIndex(double[] array){
		int minIndex = 0;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			if(array[i] < min){
				min = array[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	public static int getMaxIndex(int[] array){
		int maxIndex = 0;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if(array[i] > max){
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}


	public static double log2(double value){
		return  ( Math.log(value) / Math.log(2) );
	}


	public static double calcInfoVol(double part, double all){
		double info = 0.0;
		if(part != 0.0) info = -1 * (part/all) * log2(part/all);
		return info;
	}

	public static double calcAveInfo(double[] parts){
		double all = 0.0;
		for(int i=0; i<parts.length; i++){
			all += parts[i];
		}

		double H = 0.0;
		if(all != 0.0){
			for(int i=0; i<parts.length; i++){
				H += calcInfoVol(parts[i], all);
			}
		}
		return H;
	}

	public static double calcAll(double[] parts){
		double all = 0.0;
		for(int i=0; i<parts.length; i++){
			all += parts[i];
		}
		return all;
	}

	public static double calcExpectedVal(double[] eachInfo, double[] eachVal){

		double all = 0.0;
		for(int i=0; i<eachVal.length; i++){
			all += eachVal[i];
		}

		double E = 0.0;
		if(all != 0.0){
			for(int i=0; i<eachVal.length; i++){
				E += (eachVal[i]/all) * eachInfo[i];
			}
		}
		return E;

	}

	public static double calcGain(double[] eachInfo, double[] eachVal, double IofD){
		return IofD - calcExpectedVal(eachInfo, eachVal);
	}

	public static double calcGainRate(double[] eachInfo, double[] eachVal, double IofD){

		double G = calcGain(eachInfo, eachVal, IofD);
		double S = calcAveInfo(eachVal);
		double GR = 0.0;
		if(S != 0.0){
			GR = G / S;
		}
		return GR;
	}

	public static double calcGainRate(double Gain, double Split){
		double GR = 0.0;
		if(Split != 0.0){
			GR = Gain / Split;
		}
		return GR;
	}


	public static double calcModifGainRate(double[] eachInfo, double[] eachVal, double IofD,int numOfAtt, int p, int q){
		//p = 1, q=-1とするとGRと同じになる．
		//pが大きいほど属性値の少ない属性が選択される．
		//qが大きいほどデータが均等に分割されている属性が選択されやすく成る．
		double G = calcGain(eachInfo, eachVal, IofD);
		double S = calcAveInfo(eachVal);

		double MG = 0.0;
		if(numOfAtt != 0){
			MG =   ( G / Math.pow(numOfAtt, p) ) * Math.pow( (S / log2(numOfAtt)), q)   ;
		}

		return MG;
	}

	public static double calcModifGainRate(double Gain, double Split, int numOfAtt, int p, int q){
		//p = 1, q=-1とするとGRと同じになる．
		//pが大きいほど属性値の少ない属性が選択される．
		//qが大きいほどデータが均等に分割されている属性が選択されやすく成る．

		double MG = 0.0;
		if(numOfAtt != 0){
			MG =   ( Gain / Math.pow(numOfAtt, p) ) * Math.pow( (Split / log2(numOfAtt)), q)   ;
		}

		return MG;
	}


}
