package nsga2;

import static java.util.Comparator.*;

import java.util.ArrayList;

import methods.MersenneTwisterFast;
import navier.Cons;
import navier.Pittsburgh;
import navier.RuleSet;

public class Nsga2 {

	public Nsga2(int objectives, MersenneTwisterFast rand){
		this.objectives = objectives;
		this.rnd = rand;
	}

	//メンバ
	int objectives;

	MersenneTwisterFast rnd;

	ArrayList<Pittsburgh> rankedList = new ArrayList<Pittsburgh>();

	//メソッド

	public void GenChange(RuleSet ruleset) {

		ruleset.allPitsRules.clear();
		int lengthSize =ruleset.pitsRules.size();
		ruleset.allPitsRules.addAll(ruleset.pitsRules);
		ruleset.allPitsRules.addAll(ruleset.newPitsRules);
		ruleset.pitsRules.clear();
		ruleset.newPitsRules.clear();

		//ランクとCD計算
		DisideRank(ruleset.allPitsRules);

		//ランクとCDでソート
		ruleset.allPitsRules.sort(comparing(Pittsburgh::GetRank).reversed() //打ち消しのリバース
									.thenComparing(Pittsburgh::GetCrowding).reversed());

		//次世代に個体を格納
		for (int i = 0; i < lengthSize; i++) {
			ruleset.pitsRules.add(ruleset.allPitsRules.get(i));
		}

	}

	public void DisideRank(ArrayList<Pittsburgh> rules) {

		rankedList.clear();
		int[] n_i = new int[rules.size()];
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] S_i = new ArrayList[rules.size()];
		ArrayList<Integer> F_i = new ArrayList<Integer>();

		for (int p = 0; p < rules.size(); p++) {
			n_i[p] = 0;
			S_i[p] = new ArrayList<Integer>();

			for (int q = 0; q < rules.size(); q++) {
				if (p != q) {
					if (dominate(p, q, rules)) {
						S_i[p].add(q);
					} else if (dominate(q, p, rules)) {
						n_i[p]++;
					}
				}
			}
			if (n_i[p] == 0) {
				rules.get(p).SetRank(0);
				rankedList.add(rules.get(p));
				F_i.add(p);
			}
		}

		int size = rules.size();
		double FirstMax = 0;
		double FirstMin = 100;
		boolean isNormalize = Cons.isCDnormalize;
		ArrayList<Double> firstObj = new ArrayList<Double>();
		ArrayList<Double> nowFirst = new ArrayList<Double>();
		if(isNormalize){
			for(int i=0; i<size; i++){
				nowFirst.add(rules.get(i).GetFitness(0));
				if(FirstMax < nowFirst.get(i)){
					FirstMax = nowFirst.get(i);
				}
				else if(FirstMin > nowFirst.get(i)){
					FirstMin = nowFirst.get(i);
				}
			}
			for(int i=0; i<size; i++){
				double afterFirst = ((nowFirst.get(i)-FirstMin)/(FirstMax-FirstMin)) * 99;
				double log10First = Math.log10(afterFirst + 1);
				firstObj.add(log10First);
			}

		}else{
			for(int i=0; i<size; i++){
				firstObj.add(rules.get(i).GetFitness(0));
			}
		}

		for(int i=0; i<size; i++){
			rules.get(i).setFirstObj(firstObj.get(i));
		}

		int i = 0;
		CalcDistance(rankedList);
		rankedList.clear();
		ArrayList<Integer> Q = new ArrayList<Integer>();
		while (F_i.size() != 0) {
			for (int p = 0; p < F_i.size(); p++) {
				for (int q = 0; q < S_i[F_i.get(p)].size(); q++) {
					n_i[S_i[F_i.get(p)].get(q)] -= 1;
					if (n_i[S_i[F_i.get(p)].get(q)] == 0) {

						rules.get( S_i[F_i.get(p)].get(q) ).SetRank(i + 1);
						Q.add(S_i[F_i.get(p)].get(q));
						rankedList.add(rules.get( S_i[F_i.get(p)].get(q) ));

					}
				}
			}
			if (rankedList.size() != 0)
				CalcDistance(rankedList);
			rankedList.clear();
			i++;
			F_i.clear();
			for (int k = 0; k < Q.size(); k++) {
				F_i.add(Q.get(k));
			}
			Q.clear();
		}
	}

	boolean dominate(int p, int q, ArrayList<Pittsburgh> rules) {
		// minimize, fitness[0] is maximize
		// if p dominate q then true else false
		boolean ans = false;
		int i = 1;
		for (int o = 0; o < objectives; o++) {
			if (i * rules.get(p).GetFitness(o) > i * rules.get(q).GetFitness(o)) {
				ans = false;
				break;
			}
			else if (i * rules.get(p).GetFitness(o) < i * rules.get(q).GetFitness(o)) {
				ans = true;
			}
		}
		return ans;
	}

	void CalcDistance(ArrayList<Pittsburgh> list) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			list.get(i).SetCrowding(0);
		}

		for (int o = 0; o < objectives; o++) {

			for (int i = 1; i < size; i++) {
				for (int j = i; j >= 1
						&& list.get(j - 1).getFirstObj(o) > list.get(j)
								.getFirstObj(o); j--) {
					Pittsburgh tmp = list.get(j);
					list.set(j, list.get(j - 1));
					list.set(j - 1, tmp);
				}
			}

			list.get(0).SetCrowding(Double.POSITIVE_INFINITY);
			//２目的のときにあると，Infinity増える
			//list.get(size - 1).SetCrowding(Double.POSITIVE_INFINITY);

			double min = list.get(0).getFirstObj(o);
			double max = list.get(size - 1).getFirstObj(o);
			double maxmin = max - min;
			if (maxmin == 0) {
				for (int i = 1; i < size - 1; i++) {
					double distance = 0;
					list.get(i).SetCrowding(
							list.get(i).GetCrowding() + distance);
				}
			} else {
				for (int i = 1; i < size - 1; i++) {
					double distance = (Math.abs(list.get(i + 1).getFirstObj(o)
							- list.get(i - 1).getFirstObj(o)) / maxmin);
					list.get(i).SetCrowding(
							list.get(i).GetCrowding() + distance);
				}
			}
		}

	}

}
