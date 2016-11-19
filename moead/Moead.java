package moead;

import java.util.ArrayList;
import java.util.Arrays;

import methods.MersenneTwisterFast;
import navier.Cons;
import navier.Pittsburgh;

public class Moead{

	private MersenneTwisterFast rnd;
	private MersenneTwisterFast rnd2;

	private int populationSize_;

	double[] z_; // ideal point
	double[] base_z_; // z_ = base_z_ * alpha
	double alpha_;

	double[] n_; // nadir point
	double[] base_n_; // z_ = base_n_ * alpha
	double alpha2_;

	public ArrayList<double[]> lambda_;

	public ArrayList<int[]> neighborhood_;

	int vecNum[];

	int H_; // H : 分割数
	int functionType_;
	int selectNeighborSize_; // 選択近傍サイズ
	int updateNeighborSize_; // 更新近傍サイズ
	int parcentNeighborSize; //　％で近傍サイズ指定

	int objective;

	public Moead(int popSize, int H, double alpha, int function, int objective, int seleN,int updaN,
			MersenneTwisterFast rand) {

		this.rnd = rand;
		this.rnd2 = new MersenneTwisterFast(rnd.nextInt());
		this.objective = objective;

		populationSize_ = popSize;

		H_ = H;
		functionType_ = function;
		alpha_ = alpha;

		selectNeighborSize_ = seleN;
		updateNeighborSize_ = updaN;

		int pers = Cons.neiPerSwit;

		if(pers == 1){
			parcentNeighborSize = calcNeiPer();
			if(parcentNeighborSize <= 0){
				parcentNeighborSize = 1;
			}
			selectNeighborSize_ = parcentNeighborSize;
			updateNeighborSize_ = parcentNeighborSize;
		}

		vecNum = new int[popSize];
		for (int i = 0; i < popSize; i++) {
			vecNum[i] = i;
		}

	}

	public Moead(Moead moe, int popSize, ArrayList<double[]> lambda, int vecNu[]) {

		this.rnd = moe.rnd;
		this.rnd2  = new MersenneTwisterFast(rnd.nextInt());
		this.objective = moe.objective;
		this.functionType_ = moe.functionType_;
		this.alpha_ = moe.alpha_;
		this.selectNeighborSize_ = moe.selectNeighborSize_;
		this.updateNeighborSize_ = moe.updateNeighborSize_;
		populationSize_ = popSize;

		int pers = Cons.neiPerSwit;

		if(pers == 1){
			parcentNeighborSize = calcNeiPer();
			if(parcentNeighborSize <= 0){
				parcentNeighborSize = 1;
			}
			selectNeighborSize_ = parcentNeighborSize;
			updateNeighborSize_ = parcentNeighborSize;

		}

		z_ = Arrays.copyOf(moe.z_, moe.z_.length);
		base_z_ = Arrays.copyOf(moe.base_z_, moe.base_z_.length);

		n_ = Arrays.copyOf(moe.n_, moe.n_.length);
		base_n_ = Arrays.copyOf(moe.base_n_, moe.base_n_.length);

		lambda_ = lambda;

		this.vecNum = vecNu;

	}

	int calcNeiPer(){
		int nei = 0;
		nei = (int)((populationSize_ * Cons.neiPer) / 100);
		return nei;
	}

	public void ini(){
		z_ = new double[objective];
		base_z_ = new double[objective];
		for (int i = 0; i < objective; i++) {
			z_[i] = 1.0e+30;
			base_z_[i] = 1.0e+30;
		}

		n_ = new double[objective];
		base_n_ = new double[objective];
		for (int i = 0; i < objective; i++) {
			n_[i] = 1.0e-6;
			base_n_[i] = 1.0e-6;
		}

		if(functionType_ != 5){
			initWeight(H_);
		}else{
			initWeightSSF(populationSize_);
		}
		initNeighborhood();

		/*for (int i = 0; i <  populationSize_; i++) {
			updateReference(pitsRules.get(i));
		}*/

	}

	public void inidvi(ArrayList<Pittsburgh> pitsRules){
		initNeighborhood();

		for (int i = 0; i <  populationSize_; i++) {
			updateReference(pitsRules.get(i));
		}

	}

	double imada(double x){

		return ( 4 * (x - 0.5)*(x - 0.5)*(x - 0.5) + 0.5 );

	}

	private void initWeight(int m) {
		this.lambda_ = new ArrayList<double[]>();
		for (int i = 0; i <= m; i++) {
			if (objective == 2) {
				double[] weight = new double[2];

				boolean bias = Cons.isBias;
				if(bias){
					weight[1] = imada(i / (double) m);
					weight[0] = 1.0 - imada(i / (double) m);
				}else{
					weight[0] = i / (double) m;
					weight[1] = (m - i) / (double) m;
				}

				//System.out.println(weight[0]+" "+weight[1]);
				this.lambda_.add(weight);
			} else if (objective == 3) {
				for (int j = 0; j <= m; j++) {
					if (i + j <= m) {
						int k = m - i - j;
						double[] weight = new double[3];
						weight[0] = i / (double) m;
						weight[1] = j / (double) m;
						weight[2] = k / (double) m;
						this.lambda_.add(weight);
						//System.out.println(weight[0]+" "+weight[1]+" "+weight[2]);
					}
				}
			} else {
				System.out.println("error");
			}
		}
		//System.out.println(lambda_.size());
		//this.populationSize_ = this.lambda_.size();
	}

	private void initWeightSSF(int PopSize) {

		this.lambda_ = new ArrayList<double[]>();

		for (int i = 1; i <= PopSize; i++) {
			if (objective == 2) {
				double[] weight = new double[2];

				weight[0] = i;
				weight[1] = i;

				this.lambda_.add(weight);

			} else {
				System.out.println("error");
			}
		}

	}

	private void initNeighborhood() {
		neighborhood_ = new ArrayList<int[]>(populationSize_);

		double[][] distancematrix = new double[populationSize_][populationSize_];
		for (int i = 0; i < populationSize_; i++) {
			distancematrix[i][i] = 0;
			for (int j = i + 1; j < populationSize_; j++) {
				distancematrix[i][j] = Utils.distVector(lambda_.get(i), lambda_.get(j));
				distancematrix[j][i] = distancematrix[i][j];
			}
		}

		for (int i = 0; i < populationSize_; i++) {
			int[] index = Utils.sorting(distancematrix[i]);
			//			int[] array = new int[T_];
			int[] array = new int[populationSize_]; // 変更
			//			System.arraycopy(index, 0, array, 0, T_);
			System.arraycopy(index, 0, array, 0, populationSize_); // 変更
			neighborhood_.add(array);
		}
	}

	public void updateReference(Pittsburgh individual) {
		for (int n = 0; n < objective; n++) {

			if (individual.GetFitness(n) < 1) continue;

			if (individual.GetFitness(n) < base_z_[n]) {
				base_z_[n] = individual.GetFitness(n);
			}
			z_[n] = base_z_[n] * alpha_;

			if(n == 0){
				z_[n] = base_z_[n] - base_z_[n] * Cons.idealDown;
			}
		}

		for (int n = 0; n < objective; n++) {

			if (individual.GetFitness(n) > 1000) continue;

			if (individual.GetFitness(n) > base_n_[n]) {
				base_n_[n] = individual.GetFitness(n);
			}
			n_[n] = base_n_[n] * alpha_;

		}
	}

	public int[] matingSelection(int cid, int size) {
		// cid : the id of current subproblem
		// size : the number of selected mating parents
		int pare;
		int[] numOfParents = new int[size];

		// 選択近傍
		for (int i = 0; i < size; i++) {
			pare = rnd2.nextInt(selectNeighborSize_);
			numOfParents[i] = neighborhood_.get(cid)[pare];
		}

		return numOfParents;
	}

	public void updateNeighbors(Pittsburgh offSpring, ArrayList<Pittsburgh> population_, int cid, int func) {

		for (int j = 0; j < updateNeighborSize_; j++) {
			int weightIndex = neighborhood_.get(cid)[j];
			Pittsburgh sol = population_.get(weightIndex);
			double f1 = fitnessFunction(offSpring, lambda_.get(weightIndex), func);
			double f2 = fitnessFunction(sol, lambda_.get(weightIndex), func);

			if (f1 <= f2 && offSpring.getRuleNum() > 0)
				population_.get(weightIndex).replace(new Pittsburgh(offSpring, vecNum[weightIndex]));
		}

	}

	double normalizationTch(double f , int ob){
		double normalized;

		normalized = (f - z_[ob]) / (n_[ob] - z_[ob]);

		return normalized;
	}

	double normalizationWS(double f , int ob){
		double normalized;

		normalized = f / (n_[ob] - z_[ob]);

		return normalized;
	}

	private double fitnessFunction(Pittsburgh individual, double[] lambda, int func) {

		int normal = Cons.Normalization;
		if (func == Cons.Tcheby) {
			double maxFun = -1.0 * Double.MAX_VALUE;

			for (int n = 0; n < objective; n++) {
				double diff = 0;
				if(normal == 1){
					diff = Math.abs( normalizationTch(individual.GetFitness(n), n) );
				}else{
					diff = Math.abs(individual.GetFitness(n) - z_[n]);
				}

				double feval;
				if (lambda[n] == 0) {
					feval = 0.00001 * diff;
				} else {
					feval = diff * lambda[n];
				}
				if (feval > maxFun) {
					maxFun = feval;
				}
			}
			return maxFun;
		}

		else if (func == Cons.WS) {
			boolean isNadia = Cons.isWSfromNadia;
			double sum = 0;
			if(normal == 1){
				for (int n = 0; n < objective; n++) {
					sum += normalizationWS(individual.GetFitness(n), n) * lambda[n];
				}
			}else if(isNadia){
				for (int n = 0; n < objective; n++) {
					sum += (n_[n] - individual.GetFitness(n)) * lambda[n];
				}
				sum = -sum;
			}
			else{
				for (int n = 0; n < objective; n++) {
					sum += individual.GetFitness(n) * lambda[n];
				}
			}
			return sum;
		}

		else if (func == Cons.PBI) {
			double d1, d2;
			double nd = Utils.norm_vector(lambda);
			double[] namda = new double[lambda.length];

			for (int i = 0; i < namda.length; i++) {
				namda[i] = lambda[i] / nd;
			}

			double[] realA = new double[objective];
			double[] realB = new double[objective];

			for (int n = 0; n < realA.length; n++) {
				if(normal == 1){
					realA[n] = normalizationTch(individual.GetFitness(n), n);
				}else{
					realA[n] = (individual.GetFitness(n) - z_[n]);
				}
			}

			d1 = Math.abs(Utils.prod_vector(realA, namda));

			for (int n = 0; n < realB.length; n++) {
				if(normal == 1){
					realB[n] = normalizationTch(individual.GetFitness(n), n) - d1 * namda[n];
				}else{
					realB[n] = (individual.GetFitness(n) - z_[n]) - d1 * namda[n];
				}
			}

			d2 = Utils.norm_vector(realB);

			return d1 + Cons.theta * d2;

		}

		else if (func == Cons.IPBI) {
			double d1, d2;
			double nd = Utils.norm_vector(lambda);
			double[] namda = new double[lambda.length];

			for (int i = 0; i < namda.length; i++) {
				namda[i] = lambda[i] / nd;
			}

			double[] realA = new double[objective];
			double[] realB = new double[objective];

			for (int n = 0; n < realA.length; n++) {
				realA[n] = (n_[n] - individual.GetFitness(n));
			}

			d1 = Math.abs(Utils.prod_vector(realA, namda));

			for (int n = 0; n < realB.length; n++) {
				realB[n] = (n_[n] - individual.GetFitness(n)) - d1 * namda[n];
			}

			d2 = Utils.norm_vector(realB);

			return -(d1 - Cons.theta  * d2);

		}

		else if (func == Cons.SSF) {
			double SF = 0;

			double S = individual.getRuleNum();

			double P = S - lambda[1]; //Wi = i ( I = 1, 2, 3, 4, …, N).

			if(P < 0.0)  P = 0.0;

			SF  = individual.GetFitness(0) + 100 * P + 0.01 * S;    //fi(S) = error(S) + 100 max{ (|S| - Ni), 0} + 0.01 |S|

			return SF;
		}

		else {
			System.out.println("nknown type " + functionType_);
			System.exit(-1);
			return 0;
		}
	}

	public double[] getIdeal(){
		return base_z_;
	}

	public void setIdeal(double[] inputZ){
		base_z_ = Arrays.copyOf(inputZ, objective);
	}

	public double[] getNadia(){
		return base_n_;
	}

}
