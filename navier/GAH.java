package navier;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import methods.Gmethod;
import methods.MersenneTwisterFast;
import methods.Resulton;
import moead.Moead;
import nsga2.Nsga2;
import time.TimeWatch;


public class GAH {

	RuleSet divideHyb;

	Nsga2 nsg;

	Moead moe;

	MersenneTwisterFast rnd;

	TimeWatch timeWatch;

	ForkJoinPool Dpop;

	int way = Cons.Way;

	Resulton result;

	int objectives;
	long GenNum;

	int func;
	int popSize;

	public static final int Parent = 1;
	public static final int Son = 0;


	public GAH( int popSize, RuleSet divideHyb, Nsga2 nsga2, Moead sai ,MersenneTwisterFast rnd, int objectives,int gen, ForkJoinPool Dpop, int func, Resulton res, TimeWatch timeWatch) {

		this.divideHyb = divideHyb;
		nsg = nsga2;
		moe = sai;

		this.rnd = rnd;
		this.result = res;

		this.objectives = objectives;
		this.GenNum = gen;
		this.Dpop = Dpop;
		this.func = func;
		this.popSize = popSize;

		this.timeWatch = timeWatch;

	}

	public void GAini(Dataset data) {
		Evoluation(data, divideHyb, Parent);
		if (objectives != 1 && func == 0){
			nsg.DisideRank(divideHyb.pitsRules);
		}
	}

	public void GAFrame(Dataset traData, Dataset tstData, int pon, int repeat, int cv){

		//初期個体群
		GAini(traData);

		//MOEAD初期化 （２目的のみ）
		if(func > 0){
			moe.ini();
			moe.inidvi(divideHyb.pitsRules);
		}

		boolean isNewGen = Cons.isNewGen;

		for (int gen = 0; gen < GenNum; gen++) {

			if(gen % Cons.ShowRate == 0){
				System.out.print(".");
			}
			if(isNewGen){		//途中結果保持
				genCheck(gen, tstData, pon, repeat, cv);
			}

			//GA操作
			if(func == 0||objectives == 1){
				GAStartNS(traData, gen);

			}else{
				GAStartMO(traData, gen);
			}
		}
	}

	public void genCheck(int gen, Dataset tstData, int pon, int repeat, int cv){
		if( (gen+1) <=10 ||
			(gen+1) %10==0 && gen<=100||
			(gen+1) %100==0 && gen<=1000||
			(gen+1) %1000==0 && gen<=10000||
			(gen+1) %10000==0 && gen<=100000||
			(gen+1) %100000==0 && gen<=1000000||
			(gen+1) %1000000==0
		){

		Pittsburgh bestb;
		bestb = bestGenCalc(tstData);
		double trat = bestb.getMissRate();
		double tstt = bestb.GetTestMissRate();
		double numr = bestb.getRuleNum();
		double lengtht = bestb.getRuleLength();
		result.writeBestLog(trat, tstt, numr, lengtht, gen+1, pon, repeat, cv);

		}
	}

	public void GAStartNS(Dataset data, int gen) {

		PitsAndMic();
		Delete();

		timeWatch.evoStart();
		Evoluation(data, divideHyb, Son);
		timeWatch.evoStop();

		if(objectives == 1){
			oneObjUpdate();
		}
		else{
			nsg.GenChange(divideHyb);
		}

	}

	public void GAStartMO(Dataset data, int gen) {

		for(int i = 0;i < divideHyb.pitsRules.size(); i++){
			divideHyb.pitsRules.get(i).setSize(data.DataSize);
		}
		List<Integer> UseVecNums = new ArrayList<Integer>();
		for (int i = 0; i < popSize; i++) {
			UseVecNums.add(i);
		}
		//Gmethod.shuffle(UseVecNums, rnd);

		divideHyb.newPitsRules.clear();
		divideHyb.addNewPits(popSize);

		for (int i = 0; i < popSize; i++) {

			int nowVec = UseVecNums.get(i);

			divideHyb.pitsCrossRam(nowVec, popSize, moe);
			divideHyb.pitsMutation(nowVec);
			divideHyb.micGA(nowVec);

			divideHyb.newPitsRules.get(nowVec).removeRule();
			EvoluationOne(data, divideHyb.newPitsRules.get(nowVec));

			moe.updateReference(divideHyb.newPitsRules.get(nowVec));
			moe.updateNeighbors(divideHyb.newPitsRules.get(nowVec), divideHyb.pitsRules,nowVec, func);
		}

	}

	public void Evoluation(Dataset data, RuleSet r, int PorS){


		if(PorS ==Parent){
			for (int i = 0; i < r.pitsRules.size(); i++) {
				EvoluationOne(data, r.pitsRules.get(i));
			}
		}
		else if(PorS == Son){
			for (int i = 0; i < r.newPitsRules.size(); i++) {
				EvoluationOne(data, r.newPitsRules.get(i));
			}
		}

	}

	public void EvoluationOne(Dataset data, Pittsburgh r) {

		double fitness = 0;

		r.setNumAndLength();

		if (r.getRuleNum() != 0) {
			double ans = r.CalcAccuracyPal(data, Dpop);
			double acc = ans / data.getDataSize();
			r.SetMissRate((1 - acc) * 100);

			if (objectives == 1) {
				fitness = Cons.W1 * r.getMissRate() + Cons.W2 * r.getRuleNum() + Cons.W3 * r.getRuleLength();
				r.SetFitness(fitness, 0);
			}

			else if (objectives == 2) {
				r.SetFitness((r.getMissRate() ), 0);
				r.SetFitness((out2obje(r, way)), 1);
			}

			else if (objectives == 3) {
				r.SetFitness(r.getMissRate(), 0);
				r.SetFitness(r.getRuleNum(), 1);
				r.SetFitness(r.getRuleLength(), 2);
			}

			else {
				System.out.println("not be difined");
			}

			if(r.getRuleLength() == 0){
				for (int o = 0; o < objectives; o++) {
					fitness = 100000;
					r.SetFitness(fitness, o);
				}
			}
		}
		else {
			for (int o = 0; o < objectives; o++) {
				fitness = 100000;
				r.SetFitness(fitness, o);
			}
		}
	}

	//2目的目変更
	double out2obje(Pittsburgh pit, int way){
		if(way == 4){
			return (double)(pit.getRuleLength() / pit.getRuleNum());
		}else if(way == 3){
			return (double)(pit.getRuleNum() + pit.getRuleLength());
		}else if(way == 2){
			return (double)(pit.getRuleNum() * pit.getRuleLength());
		}else if(way == 1){
			return (double)(pit.getRuleLength());
		}else {
			return (double)(pit.getRuleNum());
		}
	}

	public void Michigan() {
		int size =  divideHyb.newPitsRules.size();
		for (int i = 0; i < size; i++) {
			divideHyb.micGA(i);
		}
	}

	public void Delete() {
		for (int i = 0; i < divideHyb.newPitsRules.size(); i++) {
			divideHyb.newPitsRules.get(i).removeRule();
		}
	}

	public void UniformCross() {

		int length = divideHyb.pitsRules.size();

		//子個体初期化
		divideHyb.newPitsRules.clear();
		for(int i = 0;i < length; i++){
			divideHyb.newPitsCreat();
		}

		for (int s = 0; s < length; s++) {
			divideHyb.pitsCross(s, popSize);
			divideHyb.pitsMutation(s);
		}
	}

	public void PitsAndMic(){

		int length = divideHyb.pitsRules.size();
		divideHyb.newPitsRules.clear();
		for(int i = 0;i < length; i++){
			divideHyb.newPitsCreat();
		}

		for (int s = 0; s < length; s++) {
			divideHyb.pitsAndMic(s, popSize);
			divideHyb.pitsMutation(s);
		}

	}

	public void oneObjUpdate() {

		Collections.sort(divideHyb.pitsRules, new PittsComparator());
		Collections.sort(divideHyb.newPitsRules, new PittsComparator());

		ArrayList<Pittsburgh> temp = new ArrayList<Pittsburgh>();

		Gmethod.mergeSort(temp, divideHyb.pitsRules, divideHyb.newPitsRules);

		divideHyb.pitsRules = new ArrayList<Pittsburgh>(temp);
		divideHyb.newPitsRules.clear();

	}

	double out2objeAnother(Pittsburgh pit, int way){
		if(way == 0){
			return (double)(pit.getRuleLength());
		}else {
			return (double)(pit.getRuleNum());
		}
	}

	//ベスト系
	public Pittsburgh bestGenCalc(Dataset tstData) {

		Pittsburgh best;
		best = new Pittsburgh(divideHyb.pitsRules.get(0));
		if (objectives == 1) {
			for (int i = 0; i < divideHyb.pitsRules.size(); i++) {
				if (divideHyb.pitsRules.get(i).GetFitness(0) < best.GetFitness(0)) {
					best = new Pittsburgh(divideHyb.pitsRules.get(i));
				}
				else if (divideHyb.pitsRules.get(i).GetFitness(0) == best.GetFitness(0)) {
					if (divideHyb.pitsRules.get(i).getMissRate() < best.getMissRate()) {
						best = new Pittsburgh(divideHyb.pitsRules.get(i));
					}
				}
			}
		}

		else {

			for (int i = 0; i < divideHyb.pitsRules.size(); i++) {
				if (divideHyb.pitsRules.get(i).GetRank() == 0) {
					if (divideHyb.pitsRules.get(i).getMissRate() < best.getMissRate()) {
						best = new Pittsburgh(divideHyb.pitsRules.get(i));
					}
					else if (divideHyb.pitsRules.get(i).getMissRate() == best.getMissRate()) {
						if (divideHyb.pitsRules.get(i).getRuleNum() <= best.getRuleNum()) {
							if (divideHyb.pitsRules.get(i).getRuleLength() <= best.getRuleLength()) {
								best = new Pittsburgh(divideHyb.pitsRules.get(i));
							}
						}
					}
				}
			}
		}

		double accTest = (double) best.CalcAccuracyPal(tstData, Dpop) / tstData.DataSize;
		best.SetTestMissRate((1 - accTest) * 100);

		return best;

	}

	public Pittsburgh GetBestRuleSet(int objectives, RuleSet all, Resulton res, Dataset data, boolean isTest) {

		Pittsburgh best;

		for (int i = 0; i < all.pitsRules.size(); i++) {

			double fitness = 0;

			if(all.pitsRules.get(i).getRuleNum() != 0){

				all.pitsRules.get(i).setNumAndLength();

				if(isTest){
					double acc = (double) all.pitsRules.get(i).CalcAccuracyPal(data, Dpop);
					all.pitsRules.get(i).SetTestMissRate(((data.DataSize - acc)/data.DataSize) * 100);
				}

				all.pitsRules.get(i).setNumAndLength();

				if (objectives == 1) {
					fitness = Cons.W1 * all.pitsRules.get(i).getMissRate() + Cons.W2 * all.pitsRules.get(i).getRuleNum() + Cons.W3 * all.pitsRules.get(i).getRuleLength();
					all.pitsRules.get(i).SetFitness(fitness, 0);
				} else if (objectives == 2) {
					all.pitsRules.get(i).SetFitness(all.pitsRules.get(i).getMissRate(), 0);
					all.pitsRules.get(i).SetFitness(out2obje(all.pitsRules.get(i), way), 1);
				} else if (objectives == 3) {
					all.pitsRules.get(i).SetFitness(all.pitsRules.get(i).getMissRate(), 0);
					all.pitsRules.get(i).SetFitness(all.pitsRules.get(i).getRuleNum(), 1);
					all.pitsRules.get(i).SetFitness(all.pitsRules.get(i).getRuleLength(), 2);
				} else {
					System.out.println("not be difined");
				}
			}

			else {
				for (int o = 0; o < objectives; o++) {
					fitness = 100000;
					all.pitsRules.get(i).SetFitness(fitness, o);
				}
			}
		}


		best = new Pittsburgh(all.pitsRules.get(0));
		if (objectives == 1) {
			for (int i = 0; i < all.pitsRules.size(); i++) {

				if (all.pitsRules.get(i).GetFitness(0) < best.GetFitness(0)) {
					best = new Pittsburgh(all.pitsRules.get(i));
				}

				else if (all.pitsRules.get(i).GetFitness(0) == best.GetFitness(0)) {
					if (all.pitsRules.get(i).getMissRate() < best.getMissRate()) {
						best = new Pittsburgh(all.pitsRules.get(i));
					}
				}

			}
		}

		else {

			//DisideRank(all.pitsRules);
			for (int i = 0; i < all.pitsRules.size(); i++) {
				int claNum = all.pitsRules.get(i).mulCla();		//そのルール集合の識別するクラス数

				if (all.pitsRules.get(i).GetRank() == 0) {

					res.setSolution(out2obje(all.pitsRules.get(i), way),
									all.pitsRules.get(i).GetFitness(0),
									all.pitsRules.get(i).GetTestMissRate(),
									out2objeAnother(all.pitsRules.get(i), way),
									claNum);


					if (all.pitsRules.get(i).GetFitness(0) < best.GetFitness(0)) {
						best = new Pittsburgh(all.pitsRules.get(i));
					}
					else if (all.pitsRules.get(i).GetFitness(0) == best.GetFitness(0)) {
						if (all.pitsRules.get(i).GetFitness(1) <= best.GetFitness(1)) {
							if (all.pitsRules.get(i).getRuleLength() <= best.getRuleLength()) {
								best = new Pittsburgh(all.pitsRules.get(i));
							}
						}
					}

				}
			}

		}
		if(isTest){
			double accTest = (double) best.CalcAccuracyPal(data, Dpop)	/ data.DataSize;
			best.SetTestMissRate((1 - accTest) * 100);
		}

		best.setNumAndLength();

		return best;

	}

	void RandomShuffle(ArrayList<Pittsburgh> rules) {
		for (int i = rules.size() - 1; i > 0; i--) {
			int t = rnd.nextInt(i + 1);

			Pittsburgh tmp = rules.get(i);
			rules.get(i).pitsCopy(rules.get(t));
			rules.get(t).pitsCopy(tmp);

		}
	}

	public class PittsComparator implements Comparator<Pittsburgh> {
	    //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
	    public int compare(Pittsburgh a, Pittsburgh b) {
	        double no1 = a.GetFitness(0);
	        double no2 = b.GetFitness(0);

	        //昇順でソート
	        if (no1 > no2) {
	            return 1;

	        } else if (no1 == no2) {
	            return 0;

	        } else {
	            return -1;

	        }
	    }

	}

}
