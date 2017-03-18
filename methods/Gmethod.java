package methods;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import genetics.Cons;
import genetics.Pittsburgh;

public class Gmethod {

	Gmethod(){}

	//******************************************************************************//
	//methods

	//データファイル名作成
	public static void makeFile(String dataName, String traFiles[][], String tstFiles[][]){

		for(int rep_i=0; rep_i<traFiles.length; rep_i++){
			for(int cv_i=0; cv_i<traFiles[0].length; cv_i++){
				 traFiles[rep_i][cv_i] = dataName + "/a" + Integer.toString(rep_i) + "_" + Integer.toString(cv_i) + "_" +dataName + "-10tra.dat";
				 tstFiles[rep_i][cv_i] = dataName + "/a" + Integer.toString(rep_i) + "_" + Integer.toString(cv_i) + "_" +dataName + "-10tst.dat";
			}
		}

	}

	//非復元抽出
	public static int [] sampringWithout(int num, int DataSize, MersenneTwisterFast rnd){

		int ans[] = new int[num];
		int i, j, same;


		int[] patternNumber2 = new int[DataSize];
		int generateNumber = num;

		if (DataSize < num){
			for (i = 0; i < DataSize; i++) {
				patternNumber2[i] = i;
			}
			generateNumber = num - DataSize;
		}

	    //非復元抽出
	    for(i=0;i<generateNumber;i++){
	    	same = 0;
	        ans[i] = rnd.nextInt(DataSize);
	        for(j=0;j<i;j++){
	        	if(ans[i] == ans[j]){
	        	   same=1;
	        	}
	        }
	        if(same==1){
	        	i--;
	        }
	    }

	    if(DataSize < num){
	    	int ii = 0;
	    	for(i=generateNumber;i<num;i++){
	    		ans[i] = patternNumber2[ii];
	    		ii++;
	    	}
	    }

	    return ans;
	}

	//非復元抽出
	public static int [] sampringWithout2(int num, int DataSize, MersenneTwisterFast rnd){

		int ans[] = new int[num];

	    //非復元抽出
	    for(int i=0;i<num;i++){
	    	boolean isSame = false;
	        ans[i] = rnd.nextInt(DataSize);
	        for(int j=0; j<i; j++){
	        	if(ans[i] == ans[j]){
	        	   isSame = true;
	        	}
	        }
	        if(isSame){
	        	i--;
	        }
	    }

	    return ans;
	}

	public static int sampringArray(int array[], MersenneTwisterFast rnd){
		return array[rnd.nextInt(array.length)];
	}

	//バイナリトーナメント
	public static int binaryT4(ArrayList<Pittsburgh> pitsRules, MersenneTwisterFast rnd, int popSize, int objectives){
		int ans = 0;
		int sele1, sele2;

		do{
			sele1 = rnd.nextInt(popSize);
			sele2 = rnd.nextInt(popSize);

			if (objectives == 1) {
				if (pitsRules.get(sele1).getFitness() < pitsRules.get(sele2).getFitness()) {
					ans = sele1;
				} else {
					ans = sele2;
				}
			} else {

				if (pitsRules.get(sele1).GetRank() > pitsRules.get(sele2).GetRank()) {
					ans = sele2;
				}
				else if (pitsRules.get(sele1).GetRank() < pitsRules.get(sele2).GetRank()) {
					ans = sele1;
				}
				else if (pitsRules.get(sele1).GetRank() == pitsRules.get(sele2).GetRank()) {
					if (pitsRules.get(sele1).GetCrowding() < pitsRules.get(sele2).GetCrowding()) {
						ans = sele2;
					} else {
						ans = sele1;
					}
				}
			}

		}while(pitsRules.get(ans).getRuleNum() == 0);

		return ans;
	}

	public static double distance(Pittsburgh a, Pittsburgh b){
		double dis = Math.abs(a.GetFitness(1) - b.GetFitness(1));
		dis += Math.abs(a.GetFitness(0) - b.GetFitness(0));
		return dis;
	}

	public static int[] binaryTRand(ArrayList<Pittsburgh> pitsRules, MersenneTwisterFast rnd, int popSize, int objectives){
		int[] ans = new int[2];
		int sele1, sele2;

		//一個目
		do{
			sele1 = rnd.nextInt(popSize);
			sele2 = rnd.nextInt(popSize);

			if (objectives == 1) {
				if (pitsRules.get(sele1).getFitness() < pitsRules.get(sele2).getFitness()) {
					ans[0] = sele1;
				} else {
					ans[0] = sele2;
				}
			} else {

				if (pitsRules.get(sele1).GetRank() > pitsRules.get(sele2).GetRank()) {
					ans[0] = sele2;
				}
				else if (pitsRules.get(sele1).GetRank() < pitsRules.get(sele2).GetRank()) {
					ans[0] = sele1;
				}
				else if (pitsRules.get(sele1).GetRank() == pitsRules.get(sele2).GetRank()) {

					if (pitsRules.get(sele1).GetCrowding() == 100000000){
						ans[0] = sele1;
					}else if (pitsRules.get(sele2).GetCrowding() == 100000000){
						ans[0] = sele2;
					}else if (pitsRules.get(sele1).GetCrowding() == 0){
						ans[0] = sele2;
					}else if (pitsRules.get(sele2).GetCrowding() == 0){
						ans[0] = sele1;
					}else if (rnd.nextBoolean()) {
						ans[0] = sele2;
					} else {
						ans[0] = sele1;
					}
				}
			}

		}while(pitsRules.get(ans[0]).getRuleNum() == 0);


		//二個目
		do{
			sele1 = rnd.nextInt(popSize);
			sele2 = rnd.nextInt(popSize);

			if (objectives == 1) {
				if (pitsRules.get(sele1).getFitness() < pitsRules.get(sele2).getFitness()) {
					ans[1] = sele1;
				} else {
					ans[1] = sele2;
				}
			} else {

				if (pitsRules.get(sele1).GetRank() > pitsRules.get(sele2).GetRank()) {
					ans[1] = sele2;
				}
				else if (pitsRules.get(sele1).GetRank() < pitsRules.get(sele2).GetRank()) {
					ans[1] = sele1;
				}
				else if (pitsRules.get(sele1).GetRank() == pitsRules.get(sele2).GetRank()) {
					if (distance(pitsRules.get(sele1), pitsRules.get(ans[0])) >
						distance(pitsRules.get(sele2), pitsRules.get(ans[0])) )
					{
						ans[1] = sele2;
					} else {
						ans[1] = sele1;
					}
				}
			}

		}while(pitsRules.get(ans[1]).getRuleNum() == 0);

		return ans;
	}

	//marge sort
	public  static void mergeSort(ArrayList<Pittsburgh> temp, ArrayList<Pittsburgh> parent, ArrayList<Pittsburgh> child){

		int parentI = 0;
		int childI = 0;

		int length = child.size() + parent.size();

		for (int k = 0; k < child.size() + length; k++) {
			if(parent.get(parentI).GetFitness(0) < child.get(childI).GetFitness(0)){
				temp.add(parent.get(parentI));
				parentI++;
			}
			else{
				temp.add(child.get(childI));
				childI++;
			}
		}

	}

	//出力
	public static void stringWrite(String file, String log){

		String fileName = file + ".txt";

		 try {
	        //出力先を作成する
	        FileWriter fw = new FileWriter(fileName, true);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	        //内容を指定する

	        	pw.println(log);

	        	//ファイルに書き出す
	            pw.close();
		 }
	     catch (IOException ex) {
	        //例外時処理
	         ex.printStackTrace();
	     }

	}

	public static void writeExp(String name ,String dir ,String st, int os){

		String fileName;

		if(os == Cons.Uni){
			fileName = dir + "/" + name + ".txt";
		}else{
			fileName = dir + "\\" + name + ".txt";
		}

		try {
	        //出力先を作成する
	        FileWriter fw = new FileWriter(fileName, false);
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	        pw.println(st);
	        pw.close();
	    }

		catch (IOException ex){
			ex.printStackTrace();
	    }

	}

	//file input
	public static void write(String name ,String dir ,double ou[] ,int pp, int j, int os){

		String fileName;

		if(os == Cons.Uni){
			fileName = dir + "/write/" + name + "_" + pp + "_" + j + ".txt";
		}else{
			fileName = dir + "\\write\\" + name + "_" + pp + "_" + j + ".txt";
		}


		 try {
	        //出力先を作成する
	        FileWriter fw = new FileWriter(fileName, false);  //※１
	        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

	        //内容を指定する
	        for(int i=0;i<ou.length;i++){
	        	pw.print(ou[i]+ " " );
	        }
	        pw.println();

	        	//ファイルに書き出す
	            pw.close();

	            //終了メッセージを画面に出力する
	            //System.out.println("出力が完了しました。");

	        } catch (IOException ex) {
	            //例外時処理
	            ex.printStackTrace();
	    }
	}

	public static String makeDir(String dataname, int methods, int os){


		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		String path = System.getProperty("user.dir");

		if(os == Cons.Uni){
			path += "/result_" +dataname + "_f" + methods + "_" +sdf.format(now.getTime());
		}else{

			path += "\\result_" +dataname + "_f" + methods + "_" +sdf.format(now.getTime());

		}

		File newdir = new File(path);
		newdir.mkdir();

		return path;

	}

	public static String makeDirName(String dataname, int methods, int os){


		String path = System.getProperty("user.dir");

		if(os == Cons.Uni){
			path += "/result_" +dataname + "_f" + methods + "_" + Cons.Seed;
		}else{
			path += "\\result_" +dataname + "_f" + methods + "_" + Cons.Seed;
		}

		return path;
	}

	public static String makeDirShort(String dataname, int methods, int os){

		String path = System.getProperty("user.dir");

		if(os == Cons.Uni){
			path += "/result_" +dataname + "_f" + methods + "_" + Cons.Seed;
		}else{
			path += "\\result_" +dataname + "_f" + methods + "_" + Cons.Seed;
		}

		File newdir = new File(path);
		newdir.mkdir();
		return path;
	}

	public static String makeDirRule(String dir, int os){

		String path;


		if(os == Cons.Uni){
			path = dir + "/ruleset";
			File newdir = new File(path);
			newdir.mkdir();

			path = dir + "/treeset";
			File newdir2 = new File(path);
			newdir2.mkdir();

			path = dir + "/solution";
			File newdir3 = new File(path);
			newdir3.mkdir();

			path = dir + "/write";
			File newdir4 = new File(path);
			newdir4.mkdir();
		}

		else{
			path = dir + "\\ruleset";
			File newdir = new File(path);
			newdir.mkdir();

			path = dir + "\\treeset";
			File newdir2 = new File(path);
			newdir2.mkdir();

			path = dir + "\\solution";
			File newdir3 = new File(path);
			newdir3.mkdir();

			path = dir + "\\write";
			File newdir4 = new File(path);
			newdir4.mkdir();
		}


		return path;

	}


	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void shuffle(List<?> list, MersenneTwisterFast rnd) {
		int SHUFFLE_THRESHOLD = 5;
		int size = list.size();
		if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
			for (int i=size; i>1; i--)
				swap(list, i-1, rnd.nextInt(i));
		} else {
			Object arr[] = list.toArray();

            // Shuffle array
			for (int i=size; i>1; i--)
                swap(arr, i-1, rnd.nextInt(i));

			ListIterator it = list.listIterator();
            for (int i=0; i<arr.length; i++) {
                it.next();
                it.set(arr[i]);
            }
        }
    }

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void swap(List<?> list, int i, int j) {
		final List l = list;
		l.set(i, l.set(j, l.get(i)));
	}

	private static void swap(Object[] arr, int i, int j) {
		Object tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}


}
