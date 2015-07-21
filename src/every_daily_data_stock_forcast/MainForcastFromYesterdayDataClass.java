package every_daily_data_stock_forcast;

import java.util.List;

import library.LogCsvClass;
import library.OneDayDataClass;
import library.SQLiteRowStockDataDaoClass;
import library.StockCandleClass;
import library.SystemVariableInterface;

public class MainForcastFromYesterdayDataClass implements
		SystemVariableInterface {
	
	/**
	 * 出来高をデータ分析に入れる。
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		test();
		
	}

	private static void test(){
		SQLiteRowStockDataDaoClass srsDao = new SQLiteRowStockDataDaoClass();
		List<StockCandleClass> candleList = srsDao.findBySymbol("1301");

		OneDayDataClass[] stockMoving = new OneDayDataClass[candleList.size() - 2];
		
		for (int i = 0; i < stockMoving.length; i++) {
			stockMoving[i] = new OneDayDataClass();
			stockMoving[i].preMR[OneDayDataClass.OPENING]= (float) 100
					* (candleList.get(i + 1).opening - candleList.get(i).closing)
					/ candleList.get(i).closing;
			
			stockMoving[i].preMR[OneDayDataClass.LOW] = (float) 100
					* (candleList.get(i + 1).low - candleList.get(i).closing)
					/ candleList.get(i).closing;
			
			stockMoving[i].preMR[OneDayDataClass.HIGH] = (float) 100
					* (candleList.get(i + 1).high - candleList.get(i).closing)
					/ candleList.get(i).closing;
			
			stockMoving[i].preMR[OneDayDataClass.CLOSING] = (float) 100
					* (candleList.get(i + 1).closing - candleList.get(i).closing)
					/ candleList.get(i).closing;
			
			stockMoving[i].preMR[OneDayDataClass.TRADING_VOLUME] = (float) 100
					* (candleList.get(i + 1).getTradingVolume() - candleList.get(i).getTradingVolume())
					/ candleList.get(i).getTradingVolume();
			
			
			stockMoving[i].sufClosingMR = (float) 100
					* (candleList.get(i + 2).closing - candleList.get(i + 1).closing)
					/ candleList.get(i + 1).closing;
			
		}
		
		
		LearningOneDayDataClass x = new LearningOneDayDataClass();
		LogCsvClass log = new LogCsvClass("1");
		log.set(new String[]{
				"open", 
				"low", 
				"high", 
				"closing",
				"trading_volume",
				"resultClosing",
				"average",
				"std",
				"influenceDist",
				"n",
				"distanceSum",
				"unit[0]",
				"unit[1]",
				"unit[2]",
				"unit[3]",
				
				"profit"});
		
		
		for (int i=0; i < stockMoving.length - 1; i++) {
			x.learn(stockMoving[i]);
			
			if(i > 30){
				
				OneDayResultClass result = x.getCalcResult(stockMoving[i+1].preMR);
				
				float profit = 0;
				final float test = 0;
				if(0 < result.mean){
					profit = stockMoving[i+1].sufClosingMR;
				}
				else if(result.mean < 0){
					profit = -stockMoving[i+1].sufClosingMR;
				}
				
				log.set(new String[] { "" + stockMoving[i].preMR[0],
						"" + stockMoving[i].preMR[1],
						"" + stockMoving[i].preMR[2],
						"" + stockMoving[i].preMR[3],
						"" + stockMoving[i].preMR[4],
						"" + stockMoving[i].sufClosingMR, 
						"" + result.mean,
						"" + result.std, 
						"" + result.influenceDist,
						"" + result.n, 
						"" + result.distanceSum, 
						"" + result.unit[0],
						"" + result.unit[1],
						"" + result.unit[2],
						"" + result.unit[3],
						
						"" + profit });
				
				
			}
			
			if(i % 100 == 0) System.out.println(i + "/" + stockMoving.length + " progressed");
		}

	}
	
	
}
