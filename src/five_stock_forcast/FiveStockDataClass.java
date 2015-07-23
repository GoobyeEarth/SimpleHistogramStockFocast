package five_stock_forcast;

import library.FiveDayDataClass;
import library.SystemVariableInterface;

public class FiveStockDataClass implements SystemVariableInterface{
	public FiveDayDataClass[] stockData = new FiveDayDataClass[NUM_OF_STOCK];
	public float sufClosingMR;
	public boolean inDist = true;
	public int distance = 0;

	public FiveStockDataClass (){
		for(int i=0; i < stockData.length; i++){
			stockData[i] = new FiveDayDataClass();

		}
	}

}
