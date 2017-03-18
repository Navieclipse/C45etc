package genetics;

import java.util.Arrays;

import methods.Fmethod;
import methods.MersenneTwisterFast;

public class Michigan{

		/******************************************************************************/
		//コンストラクタ

		Michigan(){}

		public Michigan(Michigan mic){
			this.rnd = mic.rnd;

			this.Ndim = mic.Ndim;
			this.Cnum = mic.Cnum;
			this.DataSize = mic.DataSize;
			this.TstDataSize = mic.TstDataSize;

			this.rule = Arrays.copyOf(mic.rule, mic.Ndim);

			this.conclution = mic.conclution;
			this.cf = mic.cf;
			this.ruleLength = mic.ruleLength;

		}

		Michigan(MersenneTwisterFast rnd, int Ndim, int Cnum, int DataSize, int TstDataSize){
			this.rnd = new MersenneTwisterFast(rnd.nextInt());
			this.Ndim = Ndim;
			this.Cnum = Cnum;
			this.DataSize = DataSize;
			this.TstDataSize = TstDataSize;
		}

		Michigan(int conc){
			this.conclution = conc;
		}

		/******************************************************************************/
		//引数
		MersenneTwisterFast rnd;

		//******************************************************************************//
	    //学習用
		int Ndim;												//次元
		int Cnum;												//クラス数
		int DataSize;											//パターン数
		int TstDataSize;										//パターン数

		//基本値
		int rule[];

		int conclution;
		double cf;
		int ruleLength;

		/******************************************************************************/
		//method

		//ルール作成
		public void setMic(){
			rule = new int[Ndim];
		}

		public void setTest(int size){
			this.TstDataSize = size;
		}

		public void makeRuleRnd1(MersenneTwisterFast rnd2){
			rule = Fmethod.selectRnd(Ndim, rnd2);
		}

		public void makeRuleRnd2(){
	    	conclution = rnd.nextInt(Cnum);
	        ruleLength = ruleLengthCalc();
	        do{
	        	 cf = rnd.nextDouble();
	        }while(cf == 0);

		}

		public void makeRuleNoCla(int[] noClass){
	    	conclution = noClass[rnd.nextInt(noClass.length)];
	        ruleLength = ruleLengthCalc();
	        do{
	        	 cf = rnd.nextDouble();
	        }while(cf == 0);
		}

		public void makeRuleCross(int ansCla, double cf){
	    	conclution = ansCla;
	        ruleLength = ruleLengthCalc();
	        this.cf = cf;
		}

		public int getRuleLength(){
			return ruleLength;
		}

		public double calcAdaptationPure(Dataset data,int dataNum){
	    	return  Fmethod.menberMulPure(data, dataNum, rule, Ndim);
		}

		public double calcAdaptationPure2(FuzzyPattern p){
	    	return  Fmethod.menberMulPure2(p, rule);
		}

		public double getCf(){
			return cf;
		}

		public void changeCF(){
			if(cf!=0){
				cf += rnd.nextDouble()/50 - 0.01;
				if(cf<0){
					cf = 0;
				}else if(cf > 1){
					cf = 1;
				}
			}
		}

		public int getConc(){
			return conclution;
		}

		public int getLength(){
			return ruleLength;
		}

		public int ruleLengthCalc(){
			int ans=0;
			for(int i=0;i<Ndim;i++){
				if(rule[i]!=0){
					ans++;
				}
			}
			return ans;
		}

		public void setRule(int num, int ruleN){
			rule[num] = ruleN;
		}

		public int getRule(int num){
			return rule[num];
		}

		public void mutation(int i, MersenneTwisterFast rnd2){

			int v;
			do {
				v = rnd2.nextInt(Cons.Fnum + 1);
			} while (v == rule[i]);
			rule[i] = v;

			 do{
	        	 cf = rnd.nextDouble();
	        }while(cf == 0);

		    //総ルール長
		    ruleLength = ruleLengthCalc();

		}

		//NSGA2用
		public void CalcMuData2(double pattern[][], int newDataSize){
			Fmethod kk = new Fmethod();
			kk.KKkk(Cons.MaxFnum);
			this.DataSize = newDataSize;
		}

		public void setSize(int m){
			this.DataSize = m;
		}

		public void setConc(int conc){
			this.conclution = conc;
		}

		public int getNdim(){
			return Ndim;
		}

		public void micCopy(Michigan mic){
			this.rnd = new MersenneTwisterFast(rnd.nextInt());

			this.Ndim = mic.Ndim;
			this.Cnum = mic.Cnum;
			this.DataSize = mic.DataSize;
			this.TstDataSize = mic.TstDataSize;

			this.rule = Arrays.copyOf(mic.rule, mic.Ndim);

			this.conclution = mic.conclution;;
			this.cf = mic.cf;
			this.ruleLength = mic.ruleLength;

		}



}
