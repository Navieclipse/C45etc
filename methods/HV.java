package methods;

import java.util.ArrayList;
import java.util.Comparator;

import genetics.Pittsburgh;

public class HV {

	public  double execute(int objectives, double[] refPoint, ArrayList<Pittsburgh> pitsRules ) {

		double[] referencePoint = refPoint;
		Double[][] points = new Double[pitsRules.size()][objectives];
		for (int i = 0; i < pitsRules.size(); i++) {
				points[i][0] = (double) pitsRules.get(i).getRuleNum();
				points[i][1] = pitsRules.get(i).getMissRate();
		} // for

		TheComparator comparator = new TheComparator();
		//comparator.setIndex(1);

		//劣解の個数
        int Num = filterNondominatedSet(points, points.length, objectives);
        //System.out.println(Num);
        //dump(points);

        ArrayList<Double[]> Onepoints = new ArrayList<Double[]>();
        for (int i = 0; i < Num; i++) {
        	if(points[i][0] > 0){
        		Onepoints.add( points[i] );
        	}
        } // for
        Onepoints.stream().distinct();
        Onepoints.sort(comparator);

       // dump(Onepoints);

		//ここからHV
		double h = (referencePoint[0] - Onepoints.get(0)[0]) * (referencePoint[1] - Onepoints.get(0)[1]);
		double diffDim1;
		int lastValidIndex = 0;
		for (int i = 1; i < Onepoints.size(); i++) {
			diffDim1 = (referencePoint[0] - Onepoints.get(i)[0]);
			if (diffDim1 > 0) {
				h += diffDim1 * (Onepoints.get(lastValidIndex)[1] - Onepoints.get(i)[1]);
				lastValidIndex = i;
			}
		}
		//System.out.println(h);
		return h;

	}

	public void dump( ArrayList<Double[]> onepoints ) {
		for ( int i = 0;i < onepoints.size();i++ ) {
		    for ( int j = 0; j < onepoints.get(i).length;j++ ) {
		        System.out.print( "\t" + onepoints.get(i)[j] );
		    }

		    System.out.println();
		}
	}

	public class TheComparator implements Comparator<Object> {

	    /** ソート対象のカラムの位置 */
	    private int index = 0;

	    /** ソートするためのカラム位置をセット */
	    public void setIndex( int index ) {
	        this.index = index;
	    }

	    public int compare( Object a, Object b ) {
	        Double[] strA = ( Double[] ) a;
	        Double[] strB = ( Double[] ) b;

	        return ( strA[ index ].compareTo( strB[ index ] ) );
	    }
	}

	private boolean dominates(Double point1[], Double point2[], int noObjectives) {
	    int i;
	    int betterInAnyObjective;

	    betterInAnyObjective = 0;
	    for (i = 0; i < noObjectives && point1[i] <= point2[i]; i++) {
	      if (point1[i] < point2[i]) {
	        betterInAnyObjective = 1;
	      }
	    }

	    return ((i >= noObjectives) && (betterInAnyObjective > 0));
	  }

	  private void swap(Double[][] front, int i, int j) {
		  Double[] temp;
		  temp = front[i];
		  front[i] = front[j];
		  front[j] = temp;
	  }

	  public int filterNondominatedSet(Double[][] front, int noPoints, int noObjectives) {
		 int i, j;
		 int n;
		 n = noPoints;
		 i = 0;
		 while (i < n) {
			 j = i + 1;
			 while (j < n) {
				if (dominates(front[i], front[j], noObjectives)) {
						 n--;
						 swap(front, j, n);
				} else if (dominates(front[j], front[i], noObjectives)) {
						n--;
						swap(front, i, n);
						i--;
						break;
				} else {
					j++;
				}
			 }
			 i++;
		}
		return n;
	}

}
