package methods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import genetics.Cons;
import genetics.Pittsburgh;
import genetics.RuleSet;

public class Resulton {

	public Resulton() {}

	public Resulton(int pon, int repeat, int cv, int gen, String nameDir, int os){

		this.os = os;

		this.pon = pon;
		this.repeat = repeat;
		this.cv = cv;
		this.gen = gen;
		this.nameDir = nameDir;

		for(int i=0; i<gen; i++){
			genTra.add(new ArrayList<Double>());
			genNum.add(new ArrayList<Double>());
			genLen.add(new ArrayList<Double>());
			genTst.add(new ArrayList<Double>());
		}

		for(int i=0; i<gen; i++){
			NoChange.add(new ArrayList<Double>());
			Child.add(new ArrayList<Double>());
			Grand.add(new ArrayList<Double>());
			HV.add(new ArrayList<Double>());
		}

		time = new double[cv * repeat * pon];
		timeCount = 0;
		evoTime = new double[cv * repeat * pon];
		evoTimeCount = 0;
	}

	/******************************************************************************/
	int pon;
	int repeat;
	int cv;
	int gen;
	public String nameDir;

	int os;

	//時間
	double time[];
	double timeAve;
	int timeCount;

	double evoTime[];
	double evoTimeAve;
	int evoTimeCount;

	//最終結果用
	double tra;
	double tst;
	double num;
	double len;

	double rareAve[] = new double[4];
	double rareAveRep[] = new double[4];
	double rareAveRepAll[] = new double[4];

	//非劣解
	ArrayList<Double> Tra = new ArrayList<Double>();
	ArrayList<Double> Tst = new ArrayList<Double>();
	ArrayList<Double> Rul = new ArrayList<Double>();
	ArrayList<Double> Len = new ArrayList<Double>();
	ArrayList<Integer> Clanum = new ArrayList<Integer>();

	//世代
	ArrayList<ArrayList<Double>> genTra = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> genNum = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> genLen = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> genTst = new ArrayList<ArrayList<Double>>();

	//使用
	ArrayList<ArrayList<Double>> NoChange = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> Child = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> Grand= new ArrayList<ArrayList<Double>>();

	//HV
	ArrayList<ArrayList<Double>> HV= new ArrayList<ArrayList<Double>>();

	public String getDirName(){
		return nameDir;
	}


	/******************************************************************************/
	public void setRare(double train,double tstn,double rules,double length){
		double tra = train;
		double tst = tstn;
		double num = rules;
		double len = length;
		this.tra = tra;
		this.tst = tst;
		this.num = num;
		this.len = len;

		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/Allwrite" + ".txt";
		}else{
			fileName = nameDir + "\\write\\Allwrite" + ".txt";
		}
		 try {
	        FileWriter fw = new FileWriter(fileName, true);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.println(tra+" "+tst+" "+num+" "+len);

	        pw.close();

	        }
		 catch (IOException ex) {
	           ex.printStackTrace();
		 }
	}

	/******************************************************************************/

	public void outputTree(StringBuffer text, int cc, int rr, int pp){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/treeset/besttree"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}else{
			fileName = nameDir + "\\treeset\\besttree"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.print(text);
	        pw.println();

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }
	}
	/******************************************************************************/

	//ツリー用
	public void setSolution( double rul,double traErr,double tstErr,double leng){
			this.Rul.add(rul);
			this.Tra.add(traErr);
			this.Tst.add((tstErr));
			this.Len.add(leng);
	}

	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	public void setHV(int nowGen, double hv){
		HV.get(nowGen).add(hv);
	}

	public void deletHV(){
		for(int i=0; i<gen; i++){
			HV.get(i).clear();
		}
	}

	public void outHV(int rr){

		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/HV_" + rr + "_" +".txt";
		}else{
			fileName = nameDir + "\\write\\HV_" + rr + "_" +".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			double hvAve = 0;

			int arraySize = HV.get(1).size();
			//System.out.println(HV.size() +" " + HV.get(4).size());

	        for(int i=0;i<gen;i++){;
	        	hvAve = 0;
	        	for(int j=0; j<arraySize; j++){
	        		hvAve += HV.get(i).get(j);
	        	}

	        	pw.println(i + " " + hvAve/arraySize);
	        }

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }

	}

	/******************************************************************************/
	public void setGra(int nowGen, double best[]){

		NoChange.get(nowGen).add(best[0]);
		Child.get(nowGen).add(best[1]);
		Grand.get(nowGen).add(best[2]);

	}

	public void deletGra(){
		for(int i=0; i<gen; i++){
			NoChange.get(i).clear();
			Child.get(i).clear();
			Grand.get(i).clear();
		}
	}

	public void outGra(int rr){

		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/GrandChild_" + rr + "_" +".txt";
		}else{
			fileName = nameDir + "\\write\\GrandChild_" + rr + "_" +".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			double parentAve = 0;
			double childAve = 0;
			double grandAve = 0;

			int arraySize = genTra.get(1).size();

	        for(int i=0;i<gen;i++){
	        	parentAve = 0;
	        	childAve = 0;
	        	grandAve = 0;
	        	for(int j=0; j<arraySize; j++){
	        		parentAve += NoChange.get(i).get(j);
	        		childAve += Child.get(i).get(j);
	        		grandAve += Grand.get(i).get(j);
	        	}

	        	pw.println(i + " " + parentAve/arraySize+ " " + childAve/arraySize+ " " + grandAve/arraySize);
	        }

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }

	}
	/******************************************************************************/
	public void setGen(int nowGen, double best[], double test){

		genTra.get(nowGen).add(best[0]);
		genNum.get(nowGen).add(best[1]);
		genLen.get(nowGen).add(best[2]);
		genTst.get(nowGen).add(test);

	}

	public void deletGen(){
		for(int i=0; i<gen; i++){
			genTra.get(i).clear();
			genNum.get(i).clear();
			genLen.get(i).clear();
			genTst.get(i).clear();
		}
	}

	public void outGen(int rr){

		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/genLogAve_" + rr + "_" +".txt";
		}else{
			fileName = nameDir + "\\write\\genLogAve_" + rr + "_" +".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			double aveTra = 0;
			double aveRule = 0;
			double aveLength = 0;
			double aveTest = 0;

			int arraySize = genTra.get(1).size();

	        for(int i=0;i<gen;i++){
	        	aveTra = 0;
				aveRule = 0;
				aveLength = 0;
				aveTest = 0;
	        	for(int j=0; j<arraySize; j++){
	        		aveTra += genTra.get(i).get(j);
	        		aveRule += genNum.get(i).get(j);
	        		aveLength += genLen.get(i).get(j);
	        		aveTest += genTst.get(i).get(j);
	        	}

	        	pw.println(i + " " + aveTra/arraySize+ " " + aveTest/arraySize+ " " + aveRule/arraySize+ " " + aveLength/arraySize);
	        }

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }

	}

	/******************************************************************************/

	public void setSolution( double rul,double traErr,double tstErr,double leng ,int claNum){
			this.Rul.add(rul);
			this.Tra.add(traErr);
			this.Tst.add((tstErr));
			this.Len.add(leng);
			this.Clanum.add(claNum);
	}

	public void resetSolution(){
		Tra.clear();
		Tst.clear();
		Rul.clear();
		Len.clear();
		Clanum.clear();
	}

	public void outSolution(int cc, int rr, int pp){

		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/solution/solution_"+pp+"_"+rr+"_"+cc+ "_" + Tra.size() +".txt";
		}else{
			fileName = nameDir + "\\solution\\solution_"+pp+"_"+rr+"_"+cc+ "_" + Tra.size() +".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        for(int i=0;i<Tra.size();i++){
	        	pw.println(Rul.get(i) + " " + Tra.get(i)+ " " + Tst.get(i)+ " " + Len.get(i)+ " " + Clanum.get(i));
	        }

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }

	}

	/******************************************************************************/

	public void setTime(double evoTime , double time){
		this.evoTime[evoTimeCount] = evoTime;
		evoTimeCount++;

		this.time[timeCount] = time;
		timeCount++;
	}

	public void setTimeAve(){
		evoTimeAve = 0.0;
		for(int i = 0; i < evoTime.length; i++){
			evoTimeAve += evoTime[i];
		}
		evoTimeAve = evoTimeAve/evoTime.length;

		timeAve = 0.0;
		for(int i = 0; i < time.length; i++){
			timeAve += time[i];
		}
		timeAve = timeAve/time.length;
	}

	public void writeTime(double evoSec, double evoNano, double sec, double nano){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/Atime" + ".txt";
		}else{
			fileName = nameDir + "\\write\\Atime" + ".txt";
		}
		 try {
	        FileWriter fw = new FileWriter(fileName, true);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.println(evoSec + " " + evoNano + " " + sec + " " + nano);

	        pw.close();

	        }
		 catch (IOException ex) {
	           ex.printStackTrace();
		 }
	}

	public void writeAveTime(){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/Avetime" + ".txt";
		}else{
			fileName = nameDir + "\\write\\Avetime" + ".txt";
		}
		 try {
	        FileWriter fw = new FileWriter(fileName, false);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.println(evoTimeAve + " " + timeAve);

	        pw.close();

	        }
		 catch (IOException ex) {
	           ex.printStackTrace();
		 }
	}

	/******************************************************************************/

	public void outputRules(RuleSet ruleset, int cc, int rr, int pp){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/ruleset/rules"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}else{
			fileName = nameDir + "\\ruleset\\rules"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        for(int i=0;i<ruleset.pitsRules.size();i++){
	        	pw.println("rule: " + i + " " + ruleset.pitsRules.get(i).getVecNum());
	        	for(int f=0;f<ruleset.pitsRules.get(i).getMics().size();f++){
	        		for(int g=0;g<ruleset.pitsRules.get(i).getMics().get(f).getNdim();g++){
			        	pw.print(ruleset.pitsRules.get(i).getMics().get(f).getRule(g)+" ");
			        }
	        		pw.print(ruleset.pitsRules.get(i).getMics().get(f).getConc()+" ");
	        		pw.print(ruleset.pitsRules.get(i).getMics().get(f).getCf()+" ");
	        		pw.println();
		        }
	        }

	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }
	}

	public void outputVec(RuleSet ruleset, int cc, int rr, int pp){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir+ "/vecset/vecs"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}else{
			fileName = nameDir + "\\vecset\\vecs"  +"_"+pp+"_"+rr+"_"+cc+ ".txt";
		}

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.println("#ruleNum vecNum tra tst num len");

	        for(int i=0;i<ruleset.pitsRules.size();i++){
	        	pw.println(i + " " + ruleset.pitsRules.get(i).getVecNum()
	        			+" "+ ruleset.pitsRules.get(i).getMissRate()
	        			+" "+ ruleset.pitsRules.get(i).GetTestMissRate()
	        			+" "+ ruleset.pitsRules.get(i).getRuleNum()
	        			+" "+ ruleset.pitsRules.get(i).getRuleLength());
	        }
	        pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
	    }

	}

	/******************************************************************************/

	public void writeBestLog(double tra, double tst, double num, double len, int Gen, int pon2, int repeat2, int cv2){
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/" +pon2+repeat2+cv2+ "GEN" + ".txt";
		}else{
			fileName = nameDir + "\\write\\" +pon2+repeat2+cv2+ "GEN" + ".txt";
		}
		 try {
	        FileWriter fw = new FileWriter(fileName, true);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.println(Gen +" "+tra+" "+tst+" "+num+" "+len);
	        pw.close();

	        }
		 catch (IOException ex) {
	           ex.printStackTrace();
		 }
	}

	public void setRare(Pittsburgh best){
		double tra1 = best.getMissRate();
		double tst1 = best.GetTestMissRate();
		double num1 = best.getRuleNum();
		double len1 = best.getRuleLength();
		this.tra = tra1;
		this.tst = tst1;
		this.num = num1;
		this.len = len1;
	}

	public void writeRare(Pittsburgh best){
		double tra = best.getMissRate();
		double tst = best.GetTestMissRate();
		double num = best.getRuleNum();
		double len = best.getRuleLength();
		String fileName;
		if(os == Cons.Uni){
			fileName = nameDir + "/write/Allwrite" + ".txt";
		}else{
			fileName = nameDir + "\\write\\Allwrite" + ".txt";
		}
		 try {
	        FileWriter fw = new FileWriter(fileName, true);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        pw.println(tra+" "+tst+" "+num+" "+len);

	        pw.close();

	        }
		 catch (IOException ex) {
	           ex.printStackTrace();
		 }
	}

	public void setRareAve(){
		rareAve[0] += tra;
		rareAve[1] += tst;
		rareAve[2] += num;
		rareAve[3] += len;
	}

	public void setRareAveRep(int pp, int j){

		rareAve[0] /= (double)(cv);
		rareAve[1] /= (double)(cv);
		rareAve[2] /= (double)(cv);
		rareAve[3] /= (double)(cv);

		rareAveRep[0] += rareAve[0];
		rareAveRep[1] += rareAve[1];
		rareAveRep[2] += rareAve[2];
		rareAveRep[3] += rareAve[3];

		Gmethod.write("res", nameDir,rareAve, pp, j, os);

		rareAve[0] = 0;
		rareAve[1] = 0;
		rareAve[2] = 0;
		rareAve[3] = 0;

	}

	public void setRareAveRepAll(int pp){

		rareAveRep[0] /= (double)(repeat);
		rareAveRep[1] /= (double)(repeat);
		rareAveRep[2] /= (double)(repeat);
		rareAveRep[3] /= (double)(repeat);

		rareAveRepAll[0] += rareAveRep[0];
		rareAveRepAll[1] += rareAveRep[1];
		rareAveRepAll[2] += rareAveRep[2];
		rareAveRepAll[3] += rareAveRep[3];

		Gmethod.write("res", nameDir,rareAveRep, pp, 100, os);

		rareAveRep[0] = 0;
		rareAveRep[1] = 0;
		rareAveRep[2] = 0;
		rareAveRep[3] = 0;
	}

	public void setRareAveRepAllFinal(){

			rareAveRepAll[0] /= (double)(pon);
			rareAveRepAll[1] /= (double)(pon);
			rareAveRepAll[2] /= (double)(pon);
			rareAveRepAll[3] /= (double)(pon);

		Gmethod.write("res", nameDir,rareAveRepAll, 1000, 1000, os);

	}

}
