package library;

import java.util.ArrayList;
import java.util.List;

public class StockCorrelationCoefficientClass {

	
	public StockCorrelationCoefficientClass(
			List<StockCandleClass> stockXlist, 
			List<StockCandleClass> stockYlist, 
			List<Integer> dateList){
		List<Double> dataListX = new ArrayList<Double>();
		List<Double> dataListY = new ArrayList<Double>();
		
		int stockXindex = 0;
		int stockYindex = 0;
		
		for(int i=0; i < dateList.size(); i++){
			
			if(stockXlist.size() == stockXindex || stockYlist.size() == stockYindex) break;
			
			System.out.println(
			"date:" + dateList.get(i)
			+ " dateX:" + stockXlist.get(stockXindex).date
			+ "dateY:" + stockXlist.get(stockXindex).date);
			if(dateList.get(i) == stockXlist.get(stockXindex).date && dateList.get(i) == stockXlist.get(stockXindex).date){
				dataListX.add((double) stockXlist.get(stockXindex).closing);
				dataListY.add((double) stockYlist.get(stockYindex).closing);
			}
			
			if(stockXlist.get(stockXindex).date <= dateList.get(i) ){
				stockXindex++;
			}
			if(stockYlist.get(stockYindex).date <= dateList.get(i) ){
				stockYindex++;
			}
			
			
			
			
			
			
			
			
		}
	}

}
