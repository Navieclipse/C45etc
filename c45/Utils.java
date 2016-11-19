package c45;

public class Utils {

	public static int getMaxIndex(double[] array){		//同率のときは若いインデックスの勝ち．
		int maxIndex = 0;
		double max = 0.0;
		for (int i = 0; i < array.length; i++) {
			if(array[i] > max){
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static int getMaxIndex(int[] array){
		int maxIndex = 0;
		int max = 0;
		for (int i = 0; i < array.length; i++) {
			if(array[i] > max){
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static double calcInfoVol(double part, double all){
		double info = 0.0;
		if(part != 0.0) info = -1 * (part/all) * ( Math.log(part/all) / Math.log(2) ) ;
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

}
