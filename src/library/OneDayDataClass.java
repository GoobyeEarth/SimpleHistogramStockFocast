package library;


public class OneDayDataClass implements SystemVariableInterface{
	public float[] preMR;

	public static final int OPENING = 0;
	public static final int LOW = 1;
	public static final int HIGH = 2;
	public static final int CLOSING = 3;
	public static final int TRADING_VOLUME = 4;

	public float sufClosingMR;
	public boolean inDist = true;
	
	public int distance = 0;
	public OneDayDataClass(){
		preMR = new float[NUM_OF_VARIABLES_IN_DALIY_DATA];
	}
	
	




}
