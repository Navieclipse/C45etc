package time;

public class TimeWatch {

	public TimeWatch(){}

	double evoStart = 0;
	double evoTime = 0;


	double allStart = 0;
	double allTime = 0;


	public void evoStart(){
		evoStart = System.nanoTime();
	}

	public void evoStop(){
		double nowTime = System.nanoTime();
		evoTime += (nowTime - evoStart);
	}

	public double getEvoSec(){
		return (evoTime / 1000000000.0);
	}

	public double getEvoNano(){
		return evoTime;
	}


	//all time

	public void allStart(){
		allStart = System.nanoTime();
	}

	public void allStop(){
		double nowTime = System.nanoTime();
		allTime += (nowTime - allStart);
	}

	public double getAllSec(){
		return (allTime / 1000000000.0);
	}

	public double getAllNano(){
		return evoTime;
	}


}
