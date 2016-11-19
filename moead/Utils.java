package moead;

public class Utils {

	// vector1とvector2の距離を計算
	public static double distVector(double[] vector1, double[] vector2) {
		int dim = vector1.length;
		double sum = 0;
		for (int n = 0; n < dim; n++) {
			sum += (vector1[n] - vector2[n]) * (vector1[n] - vector2[n]);
		}
		return Math.sqrt(sum);
	}

	public static double norm_vector(double[] vec){
		int dim = vec.length;
		double sum = 0;
		for(int i = 0; i < dim; i++){
			sum += vec[i] * vec[i];
		}
		return Math.sqrt(sum);
	}

	public static double prod_vector(double[] vec1, double[] vec2){
		int dim = vec1.length;
		double sum = 0;
		for(int i = 0 ; i < dim; i++){
			sum += vec1[i] * vec2[i];
		}
		return sum;
	}

	public static int[] sorting(double[] tobesorted) {
		int[] index = new int[tobesorted.length];
		for (int i = 0; i < index.length; i++)
			index[i] = i;

		for (int i = 1; i < tobesorted.length; i++) {
			for (int j = 0; j < i; j++) {
				if (tobesorted[index[i]] < tobesorted[index[j]]) {
					// insert and break;
					int temp = index[i];
					for (int k = i - 1; k >= j; k--) {
						index[k + 1] = index[k];
					}
					index[j] = temp;
					break;
				}
			}
		}
		return index;
	} // sorting

}
