package five_day_stock_forcast;

import library.OneDayDataClass;
import library.SystemVariableInterface;

public class FiveDayDataClass implements SystemVariableInterface{
	public OneDayDataClass[] dailyData = new OneDayDataClass[NUM_OF_DAY_IN_STOCK_DATA];
	public float sufClosingMR;
	
	public static final int DAY1 = 1;
	public static final int DAY2 = 2;
	public static final int DAY3 = 3;
	public static final int DAY4 = 4;
	public static final int DAY5 = 5;

	public FiveDayDataClass(){
		for(int i=0; i < dailyData.length; i++){
			dailyData[i] = new OneDayDataClass();
		}
	}

}