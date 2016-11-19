package navier;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import javax.management.JMException;

import c45.FuzzyC45;
import methods.DataLoader;
import methods.Fmethod;
import methods.Gmethod;
import methods.MersenneTwisterFast;
import methods.Osget;
import methods.Resulton;
import moead.Moead;
import nsga2.Nsga2;
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
	    int func = Integer.parseInt(args[4]);

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

					//pall(i, traData, tstData, rand, objectives, gen, Dpop, func, Npop, res, time, i, j, pp, os);
					c(i, traData, tstData, rand, objectives, gen, Dpop, func, Npop, res, time, i, j, pp, os);

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

	static public void c(int i,Dataset traData, Dataset tstData, MersenneTwisterFast rnd, int objectives, int gen, ForkJoinPool Dpop,int func,int Npop, Resulton res, TimeWatch time ,int CV, int Rep, int Pon, int os){


		ArrayList<FuzzyPattern> pats = traData.getPattern();
		int Cnum = traData.getCnum();
		int Ndim = traData.getNdim();
		double maxClassRate = 0.9;
		double minNumPatterns = 10;
		double cf = 0.25;
		boolean isPrune = true;

		FuzzyC45 fuzzyc45 = new FuzzyC45(cf, isPrune);
		fuzzyc45.buildTree(pats, Cnum, Ndim, maxClassRate, minNumPatterns);

	}

	static public void pall(int i,Dataset traData, Dataset tstData, MersenneTwisterFast rnd, int objectives, int gen, ForkJoinPool Dpop,int func,int Npop, Resulton res, TimeWatch time ,int CV, int Rep, int Pon, int os){

		time.allStart();

		int popSize = Npop;

		RuleSet ruleset = new RuleSet(rnd, objectives, Dpop);
		ruleset.initialPal(traData, popSize);

		Moead moe = new Moead(popSize, Cons.H, Cons.alpha, func, objectives, Cons.seleN, Cons.upN, rnd);
		Nsga2 nsg = new Nsga2(objectives, rnd);

		GAH GA = new GAH(popSize, ruleset, nsg, moe, rnd, objectives, gen, Dpop, func, res, time);
		GA.GAFrame(traData, tstData, Pon, Rep, CV);

		time.allStop();

		//これ以降出力操作
		Pittsburgh best = GA.GetBestRuleSet(objectives, ruleset, res, tstData, true);
		res.setRare(best);
		res.writeRare(best);
		res.outputRules(ruleset, CV, Rep, Pon);
		res.outputVec(ruleset, CV, Rep, Pon);

	}
}

