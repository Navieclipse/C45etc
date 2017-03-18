package methods;

import java.util.Arrays;

import genetics.Dataset;

public class Divide {

	static MersenneTwisterFast rnd;
	static int dpop;
	static int Npop;

	int vecNum[][];

	public Divide(MersenneTwisterFast rnd , int dpop, int Npop){
		Divide.rnd = rnd;
		Divide.dpop = dpop;
		Divide.Npop = Npop;
	}

	public int[] CalcPopSizeNormal(int Dpop){
		int CenSize = Npop/Dpop;
		int IslandSize[] = new int[Dpop];
		for (int i = 0; i<Dpop; i++) {
			IslandSize[i] = CenSize;
		}
		return IslandSize;
	}

	public int[] CalcPopSize(int Dpop){
		int CenSize = Npop/Dpop;
		int Dif = CenSize / ((Dpop + 1)/2);
		int IslandSize[] = new int[Dpop];
		for (int i = 0; i<(Dpop - 1)/2; i++) {
			IslandSize[(Dpop - 1)/2 + i + 1] = CenSize - Dif*(i+1);
			IslandSize[i] = CenSize + Dif*((Dpop - 1)/2-i);
		}	IslandSize[(int)((Dpop - 1)/2)] = CenSize;
		return IslandSize;
	}

	public Dataset[] LetsDivide(Dataset trainingSet) {

		int partitionNum = dpop;
		int classNum = trainingSet.getCnum();
		int Ndim = trainingSet.getNdim();

		boolean multiUse = false;
		int multiNum = 1;

		Dataset[] dividedPSet = new Dataset[partitionNum];

		double[][][] dividedSet = new double[partitionNum][][];
		int[][] divideCla = new int[partitionNum][];

		//System.out.println("Data Partiton = "+partitionNum);

		if (partitionNum != 1) {

			int DatasetNum = trainingSet.getDataSize();
			int[] eachSize = new int[partitionNum];

			double[][][] eachClassDataset = new double[classNum][][];

			int[][] classDividedSize = new int[partitionNum][classNum];
			int[] eachClassSize = new int[classNum];

			for (int c = 0; c < classNum; c++) {
				eachClassSize[c] = 0;
			}
			for (int i = 0; i < partitionNum; i++) {
				eachSize[i] = 0;
			}
			for (int i = 0; i < DatasetNum; i++) {
				eachClassSize[trainingSet.getAnswer(i)]++;
			}
			for (int c = 0; c < classNum; c++) {
				eachClassDataset[c] = new double[eachClassSize[c]][Ndim];
			}

			int remainAddStart = 0;
			for (int c = 0; c < classNum; c++) {
				for (int i = 0; i < partitionNum; i++) {
					classDividedSize[i][c] = eachClassSize[c] / partitionNum;
				}
				int remain = eachClassSize[c] % partitionNum;
				for (int i = 0; i < remain; i++) {
					int point = remainAddStart + i;
					if (point >= partitionNum)
						point -= partitionNum;
					classDividedSize[point][c]++;
				}
				remainAddStart += remain;
				if (remainAddStart >= partitionNum)
					remainAddStart -= partitionNum;
			}

			for (int i = 0; i < partitionNum; i++) {
				for (int c = 0; c < classNum; c++) {
					eachSize[i] += classDividedSize[i][c];
				}
				dividedSet[i] = new double[eachSize[i]][Ndim];
				divideCla[i] = new int[eachSize[i]];
			}

			for (int i = 0; i < DatasetNum; i++) {
				eachClassSize[trainingSet.getAnswer(i)]--;
				for(int j=0;j<Ndim;j++){
					eachClassDataset[trainingSet.getAnswer(i)][eachClassSize[trainingSet.getAnswer(i)]][j] = trainingSet.getX(j, i);
				}
			}

			int[] eachCount = new int[partitionNum];
			for (int i = 0; i < partitionNum; i++) {
				eachCount[i] = 0;
			}

			for (int c = 0; c < classNum; c++) {
				int[] placeMaster = new int[eachClassDataset[c].length];
				int n = 0;//count
				for (int i = 0; i < partitionNum; i++) {
					for (int j = 0; j < classDividedSize[i][c]; j++) {
						placeMaster[n] = i;
						n++;
					}
				}
				RandomShuffle(placeMaster);
				for (int i = 0; i < placeMaster.length; i++) {
					dividedSet[placeMaster[i]][eachCount[placeMaster[i]]] = Arrays.copyOf(eachClassDataset[c][i], Ndim);
					divideCla[placeMaster[i]][eachCount[placeMaster[i]]] = c;

					eachCount[placeMaster[i]]++;
				}
			}

			for(int i=0;i<partitionNum;i++){
				dividedPSet[i] = new Dataset(Ndim, classNum,divideCla[i].length, dividedSet[i],divideCla[i]);
			}

			if(multiUse){

				double[][][] multiPSet = new double[partitionNum][][];
				int[][] multiCla = new int[partitionNum][];

				for(int p=0;p<partitionNum;p++){
					int multiSize = 0;

					for(int q=0;q<multiNum;q++){
						int position= p+q;
						if(position>=partitionNum)position-=partitionNum;
						multiSize +=dividedPSet[position].getDataSize();
					}

					multiPSet[p] = new double[multiSize][Ndim];
					multiCla[p] = new int[multiSize];
				}

				for(int p=0;p<partitionNum;p++){
					int multiSize = 0;

					for(int q=0;q<multiNum;q++){
						int position= p+q;
						if(position>=partitionNum)position-=partitionNum;

						for(int i=0;i<dividedPSet[position].getDataSize();i++){

							for(int j=0;j<Ndim;j++){
								multiPSet[p][multiSize][j] = dividedPSet[position].getX(j, i);
							}
							multiCla[p][multiSize] = divideCla[position][i];

							multiSize++;
						}

					}
				}

				Dataset [] multi = new Dataset[partitionNum];
				for(int i=0;i<partitionNum;i++){
					multi[i] = new Dataset(Ndim,classNum,multiCla[i].length,multiPSet[i],multiCla[i]);
				}

				dividedPSet = multi;
			}

		}

		else {
			dividedPSet[0] = new Dataset(Ndim, classNum, trainingSet.getDataSize(), trainingSet.getPattern());
		}


		return dividedPSet;
	}

	public static void RandomShuffle(int[] number) {

		for (int i = number.length - 1; i > 0; i--) {
			int t = rnd.nextInt(i + 1);
			//int t = rnd.getIntRand(0, i);

			int tmp = number[i];
			number[i] = number[t];
			number[t] = tmp;
		}
	}


}

