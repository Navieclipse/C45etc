package genetics;

public class Cons {

	//普通
    public static final int Seed = 20126;					//乱数シード

    //ツリー
    public static final double maxClassRate = 0.75;		//ツリー生成時の葉にする条件
    public static final double minNumPatterns = 2;		//ツリー生成時の葉にする条件
    public static final double cf = 0.25;					//ツリーのプルーニング時の条件
    public static final boolean isPrune = false;
    //ファジィ
    public static final int maxFnum = 3;					//ツリーのファジィ分割数
    public static final double cutValue = 0.25;			//カットする採用メンバシップ値

    //GA
    public static final int Len  = 5;						//L
    public static final double Dont = 0.8;					//どんとケア適応確率（合わせ用）

    public static final boolean isRandDC  = true;			//dontCare

    public static final int Fnum  = 14;						//ファジィ集合数
    public static final int MaxFnum = 5;					//条件部の分割数の最大値
    public static final int Nini = 30;						//初期ルール数
    public static final int Rmax  = 60;						//最大ルール数
    public static final int Rmin = 1;						//最小ルール数

    public static final double CrossP = 0.9;				//ピッツバーグ交叉確率
    public static final double CrossM = 0.9;				//ミシガン交叉確率
    public static final double Micope = 0.5;				//ミシガン適用確率
    public static final double MicNum = 0.2;				//ミシガン適用個数率

	public static final boolean isNewGen = false;			//ログでログを出力．

    //MOEADパラメータ
    public static final int H  = 59;						//分割数 //2:209,3:19
    public static final double alpha = 0.9;					//参照点のやつ
    public static final double theta = 5.0;					//シータ
    public static final int neiPerSwit = 1;					//近傍サイズ 0:個数指定, 1:パーセント指定
    public static final int neiPer = 10;					//近傍サイズ％
    public static final int seleN  = 21;					//選択近傍サイズ
    public static final int upN  = 1;						//更新近傍サイズ

    public static final int WS  = 1;						//weighted sum
    public static final int Tcheby = 2;						//Tchebycheff
    public static final int PBI = 3;						//PBI
    public static final int IPBI = 4;						//InvertedPBI
    public static final int SSF = 5;						//special scalarizing function.

    public static final int Way = 0;						//2目的め 0:rule, 1:length, 2:rule * length, 4:length/rule
    public static final int Normalization = 0;				//正規化 0:しない or 1:する
    public static final boolean isBias = false;				//false: NObiasVector, 1: biasVector

    public static final double idealDown = 0.0;				//１目的目のみ下に動かす．（やらない場合は０に）
    public static final boolean isWSfromNadia = false;


    //NSGA2パラメータ
    public static final int nsga2 = 0;						//NSGA2の番号
    public static final int inclination = 0;				//目的関数の回転度
    public static final boolean isCDnormalize = false;
    public static final boolean isParent = false;

    //繰り返し回数
    public static final int ShowRate  = 1000;			//表示する世代

    //重み(一目的）
    public static final int W1 = 100;
    public static final int W2  = 1;
    public static final int W3 = 1;

    public static final int Traa = 0;						//学習用
    public static final int Tess = 1;						//評価用
    //OS
    public static final int Win = 0;						//windows
    public static final int Uni = 1;						//unix

}
