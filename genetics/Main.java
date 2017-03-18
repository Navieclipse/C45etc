package genetics;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import javax.management.JMException;

import c45.C45;
import c45.FuzzyC45;
import methods.DataLoader;
import methods.Divide;
import methods.Fmethod;
import methods.Gmethod;
import methods.MersenneTwisterFast;
import methods.Osget;
import methods.Resulton;
import time.TimeWatch;

public class Main {

	public static void main(String[] args) throws JMException {

		System.out.print("ver." + 10.0);

		int os = 0;		//win
		if(Osget.isLinux()==true || Osget.isMac()==true){
			os = 1;		//linux mac
			System.out.println(" OS: Linux or Mac");
		}else{
			System.out.println(" OS: Windows");
		}

		/******************************************************************************/
	    String dataName = args[0];
	    int gen = Integer.parseInt(args[1]);
	    int objectives = Integer.parseInt(args[2]);
	    int dpop = Integer.parseInt(args[3]);
	    int func = Integer.parseInt(args[4]);		//c45＝０，fc45＝１

	    int Npop = Integer.parseInt(args[5]);

	    int CV = Integer.parseInt(args[6]);
	    int Rep = Integer.parseInt(args[7]);
	    int Pon = Integer.parseInt(args[8]);
		/******************************************************************************/

	    Fmethod kk = new Fmethod();

	    kk.KKkk(Cons.MaxFnum);

	    /******************************************************************************/

	    String Log = "log";
	    Date date = new Date();
		Gmethod.stringWrite(Log, dataName);
		Gmethod.stringWrite(Log, date.toString());

		System.out.print("START: ");
		System.out.println(date);
		System.out.print("Processors:" + Runtime.getRuntime().availableProcessors()+ " ");

		//String threds = Integer.toString(dpop);
		//System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", threds);
		ForkJoinPool Dpop = new ForkJoinPool(dpop);

		for(int i=0; i<args.length; i++){
			System.out.print(args[i] + " ");
		}
		System.out.println();

		CC(dataName, objectives, gen, dpop, Dpop, func, Npop, CV, Rep, Pon, os);

	}

	static public void CC(String dataName, int objectives, int gen,int dpop, ForkJoinPool Dpop, int func,int Npop,int CV, int Rep, int Pon ,int os){

		//読み込みファイル名
		String traFiles[][] = new String[Rep][CV];
	    String tstFiles[][] = new String[Rep][CV];
	    Gmethod.makeFile(dataName, traFiles, tstFiles);

	    //データディレクトリ作成
	    String resultDir;
	    resultDir = Gmethod.makeDir(dataName, func, os);
	    Gmethod.makeDirRule(resultDir, os);

	    //実験パラメータ出力
	    String st = "DataName: " + dataName + " 0: NSGAII, 1: WS, 2: TCH, 3: PBI, 4: IPBI, 5: SSF"
	    		+ "\n gen: " + gen + " cv: " + CV + " Rep: " + Rep + " Pon: " + Pon + " seed: " + Cons.Seed + " 2objWay: " + Cons.Way
	    		+ "\n Npop: " + Npop + " Nini: " + Cons.Nini + "objectives: " + objectives + " dpop: " + dpop + " func: " + func
	    		+ "\n Len: " + Cons.Len + " Dont: " + Cons.Dont + " dWitch: " + Cons.isRandDC
	    		+ "\n Fnum: " + Cons.Fnum + " MaxFnum: " + Cons.MaxFnum + " Rmax: " + Cons.Rmax + " Rmin: " + Cons.Rmin
	    		+ "\n micope: " + Cons.Micope + " micNum: " + Cons.MicNum + " CrossM: " + Cons.CrossM + " CrossP: " + Cons.CrossP
	    		+ "\n Fnum: " + Cons.Fnum + " MaxFnum: " + Cons.MaxFnum + " Rmax: " + Cons.Rmax + " Rmin: " + Cons.Rmin
	    		+ "\n inclination: " + Cons.inclination + " isCDnormalize: " + Cons.isCDnormalize + " isParent: " + Cons.isParent
	    		+ "\n neiPerSwhit: " + Cons.neiPerSwit + " Neiper: " + Cons.neiPer+ " H: " + Cons.H + " alpha: " + Cons.alpha + " theta: "+ Cons.theta
	    		+ "\n seleN: " + Cons.seleN + " upN: " +Cons.upN  + " normalization: " +Cons.Normalization + " isBias: " +Cons.isBias
	    		+ "\n idealDown: " + Cons.idealDown + " isWSfromNadia: " +Cons.isWSfromNadia  + " isNewGen: " +Cons.isNewGen + " ShowRate: " +Cons.ShowRate
	    		;
	    Gmethod.writeExp(dataName, resultDir, st, os);

	    //出力専用クラス
	    Resulton res = new Resulton(Pon, Rep, CV, gen, resultDir, os);

		for(int pp=0;pp<Pon;pp++){
			MersenneTwisterFast rand = new MersenneTwisterFast(Cons.Seed * (pp+1));
			//繰り返し回数
			for(int j=0;j<Rep; j++){
				//ＣＶ
				for(int i=0;i<CV; i++){
					System.out.print(pp + " " + j + " " + i);

					Dataset traData = new Dataset();
					Dataset tstData = new Dataset();
					DataLoader.inputFile(traData, traFiles[j][i]);
					DataLoader.inputFile(tstData, tstFiles[j][i]);

					TimeWatch time = new TimeWatch();

					if(Npop == 1){
						simpleC45(traData, tstData, rand, objectives, gen, Dpop, func, Npop, res, time, i, j, pp);
					}else{
						GPC45(traData, tstData, rand, objectives, gen, Dpop, func, Npop, res, time, i, j, pp);
					}


					res.setTime(time.getEvoSec(), time.getAllSec());
					res.writeTime(time.getEvoSec(), time.getEvoNano(), time.getAllSec(), time.getAllNano());

					if(objectives!=1){
						res.outSolution(i, j, pp);
						res.resetSolution();
					}

					res.setRareAve();
					System.out.println();

				}
				res.setRareAveRep(pp, j);
			}
			res.setRareAveRepAll(pp);
		}
		res.setRareAveRepAllFinal();

		res.setTimeAve();
		res.writeAveTime();
		Date date2 = new Date();
		System.out.print("END: ");
		System.out.println(date2);

	}

	static public void simpleC45(Dataset traData, Dataset tstData, MersenneTwisterFast rnd, int objectives, int gen, ForkJoinPool Dpop,int func,int Npop, Resulton res, TimeWatch time ,int CV, int Rep, int Pon){

		//データ処理
		ArrayList<FuzzyPattern> pats = traData.getPattern();
		ArrayList<FuzzyPattern> patsTst = tstData.getPattern();
		int Cnum = traData.getCnum();
		int Ndim = traData.getNdim();

		int size = pats.size();
		ArrayList<FuzzyPattern> pats2 = new ArrayList<FuzzyPattern>();
		for(int p=0; p<size; p++){
			pats2.add(  new FuzzyPattern( pats.get(p) )  );
		}

		//基本的パラメータ
		int maxFnum = Cons.MaxFnum;
		double cutValue = Cons.cutValue;

		double maxClassRate = Cons.maxClassRate;
		double minNumPatterns = Cons.minNumPatterns;
		double cf = Cons.cf;
		boolean isPrune = Cons.isPrune;


		//ここから学習と識別
		if(func == 0){//普通のC45

			C45 c45 = new C45(cf, isPrune, maxClassRate, minNumPatterns, rnd);

			//木の生成
			time.allStart();
			c45.buildTree(pats2, Cnum, Ndim);
			time.allStop();

			//木の可視化
			StringBuffer text = new StringBuffer();
			c45.drowTreeWeka(text, Cnum);
			res.outputTree(text, CV, Rep, Pon);

			//木の学習用誤識別率
			int numOfcollect = c45.calcNumOfCollect(pats, Cnum);
			double rate = 100.0 - (  (double)numOfcollect / pats.size()  ) * 100.0;

			//木の評価用誤識別率
			int numOfcollectTst = c45.calcNumOfCollect(patsTst, Cnum);
			double rateTst = 100.0 - (  (double)numOfcollectTst / patsTst.size()  ) * 100.0;

			//出力
			res.setSolution(c45.getNumberOfLeafs(), rate, rateTst, c45.getNumberOfNodes(), c45.getDepth());
			res.setRare(rate, rateTst, c45.getNumberOfLeafs(), c45.getNumberOfNodes());
		}
		else if(func == 1){

			FuzzyC45 fuzzyc45 = new FuzzyC45(cf, isPrune, maxFnum, cutValue,  maxClassRate, minNumPatterns, rnd);

			//木の生成
			time.allStart();
			fuzzyc45.buildTree(pats2, Cnum, Ndim);
			time.allStop();

			//木の可視化
			StringBuffer text = new StringBuffer();
			fuzzyc45.drowTreeWeka(text, Cnum);
			res.outputTree(text, CV, Rep, Pon);

			//木の学習用誤識別率
			int numOfcollect = fuzzyc45.calcNumOfCollect(pats, Cnum);
			double rate = 100.0 - (  (double)numOfcollect / pats.size()  ) * 100.0;

			//木の評価用誤識別率
			int numOfcollectTst = fuzzyc45.calcNumOfCollect(patsTst, Cnum);
			double rateTst = 100.0 - (  (double)numOfcollectTst / patsTst.size()  ) * 100.0;

			//出力
			res.setSolution(fuzzyc45.getNumberOfLeafs(), rate, rateTst, fuzzyc45.getNumberOfNodes(), fuzzyc45.getDepth());
			res.setRare(rate, rateTst, fuzzyc45.getNumberOfLeafs(), fuzzyc45.getNumberOfNodes());
		}





	}

	static public void GPC45(Dataset traData, Dataset tstData, MersenneTwisterFast rnd, int objectives, int gen, ForkJoinPool Dpop,int func,int Npop, Resulton res, TimeWatch time ,int CV, int Rep, int Pon){

		//データ分割数
		int divideNum = Npop;

		//データ処理
		ArrayList<FuzzyPattern> pats = traData.getPattern();
		ArrayList<FuzzyPattern> patsTst = tstData.getPattern();
		int Cnum = traData.getCnum();
		int Ndim = traData.getNdim();

		//学習用パターン分割
		MersenneTwisterFast randiv  = new MersenneTwisterFast(  ( 1 + rnd.nextInt() )  );
		Divide divide = new Divide(randiv, divideNum, Npop);
		Dataset[] divideData = divide.LetsDivide(traData);

		//基本的パラメータ
		int maxFnum = Cons.MaxFnum;
		double cutValue = Cons.cutValue;

		double maxClassRate = Cons.maxClassRate;
		double minNumPatterns = Cons.minNumPatterns;
		double cf = Cons.cf;
		boolean isPrune = Cons.isPrune;

		//ここから学習と識別
		if(func == 0){//普通のC45

			ArrayList<C45> c45s = new ArrayList<C45>();
			for(int i=0; i<divideNum; i++){
				c45s.add( new C45(cf, isPrune, maxClassRate, minNumPatterns, rnd) );
			}

			time.allStart();		//学習計算時間開始

			for(int i=0; i<c45s.size(); i++){
				//木の生成
				ArrayList<FuzzyPattern> eachPat = divideData[i].getPattern();
				c45s.get(i).buildTree(eachPat, Cnum, Ndim);

				//木の学習用誤識別率
				int numOfcollect = c45s.get(i).calcNumOfCollect(pats, Cnum);
				double rate = 100.0 - (  (double)numOfcollect / pats.size()  ) * 100.0;
				c45s.get(i).setTraErrRate(rate);
			}

			//ベスト木の選別
			double minErrRate = c45s.get(0).getTraErrRate();
			int bestTree = 0;
			for(int i=1; i<c45s.size(); i++){
				if(c45s.get(i).getTraErrRate() < minErrRate){
					minErrRate = c45s.get(i).getTraErrRate();
					bestTree = i;
				}
			}

			time.allStop();			//学習計算時間終了

			//木の可視化
			StringBuffer text = new StringBuffer();
			c45s.get(bestTree).drowTreeWeka(text, Cnum);
			res.outputTree(text, CV, Rep, Pon);

			//木の評価用誤識別率
			int numOfcollectTst = c45s.get(bestTree).calcNumOfCollect(patsTst, Cnum);
			double rateTst = 100.0 - (  (double)numOfcollectTst / patsTst.size()  ) * 100.0;
			c45s.get(bestTree).setTstErrRate(rateTst);

			//出力
			res.setSolution( c45s.get(bestTree).getNumberOfLeafs(), c45s.get(bestTree).getTraErrRate(),
							c45s.get(bestTree).getTstErrRate(), c45s.get(bestTree).getNumberOfNodes(),
							c45s.get(bestTree).getDepth() );

			res.setRare( c45s.get(bestTree).getTraErrRate(), c45s.get(bestTree).getTstErrRate(),
						c45s.get(bestTree).getNumberOfLeafs(), c45s.get(bestTree).getNumberOfNodes() );

		}
		else if(func == 1){

			ArrayList<FuzzyC45> c45s = new ArrayList<FuzzyC45>();
			for(int i=0; i<divideNum; i++){
				c45s.add( new FuzzyC45(cf, isPrune, maxFnum, cutValue,  maxClassRate, minNumPatterns, rnd) );
			}

			time.allStart();		//学習計算時間開始

			for(int i=0; i<c45s.size(); i++){
				//木の生成
				ArrayList<FuzzyPattern> eachPat = divideData[i].getPattern();
				c45s.get(i).buildTree(eachPat, Cnum, Ndim);

				//木の学習用誤識別率
				int numOfcollect = c45s.get(i).calcNumOfCollect(pats, Cnum);
				double rate = 100.0 - (  (double)numOfcollect / pats.size()  ) * 100.0;
				c45s.get(i).setTraErrRate(rate);
			}

			//ベスト木の選別
			double minErrRate = c45s.get(0).getTraErrRate();
			int bestTree = 0;
			for(int i=1; i<c45s.size(); i++){
				if(c45s.get(i).getTraErrRate() < minErrRate){
					minErrRate = c45s.get(i).getTraErrRate();
					bestTree = i;
				}
			}

			time.allStop();			//学習計算時間終了

			//木の可視化
			StringBuffer text = new StringBuffer();
			c45s.get(bestTree).drowTreeWeka(text, Cnum);
			res.outputTree(text, CV, Rep, Pon);

			//木の評価用誤識別率
			int numOfcollectTst = c45s.get(bestTree).calcNumOfCollect(patsTst, Cnum);
			double rateTst = 100.0 - (  (double)numOfcollectTst / patsTst.size()  ) * 100.0;
			c45s.get(bestTree).setTstErrRate(rateTst);

			//出力
			res.setSolution( c45s.get(bestTree).getNumberOfLeafs(), c45s.get(bestTree).getTraErrRate(),
							c45s.get(bestTree).getTstErrRate(), c45s.get(bestTree).getNumberOfNodes(),
							c45s.get(bestTree).getDepth() );

			res.setRare( c45s.get(bestTree).getTraErrRate(), c45s.get(bestTree).getTstErrRate(),
						c45s.get(bestTree).getNumberOfLeafs(), c45s.get(bestTree).getNumberOfNodes() );
		}
	}



}

