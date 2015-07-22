package five_stock_forcast;

import java.util.ArrayList;
import java.util.List;

import library.FiveDayDataClass;
import library.LogCsvClass;
import library.OneDayDataClass;
import library.SQLiteRowStockDataDaoClass;
import library.StockCandleClass;
import library.SystemVariableInterface;

public class MainForcastFrom5StockDataClass implements
		SystemVariableInterface {

	public static void main(String[] args) {
		test();

	}

	private static void test(){
		SQLiteRowStockDataDaoClass srsDao = new SQLiteRowStockDataDaoClass();
		List<StockCandleClass> candleList = srsDao.findBySymbol("1301");

		float[][] mrData = new float[candleList.size() -1][NUM_OF_VARIABLES_IN_DALIY_DATA];



		for (int i = 0; i < mrData.length; i++) {
			mrData[i][OneDayDataClass.OPENING] = (float) 100
					* (candleList.get(i + 1).opening - candleList.get(i).closing)
					/ candleList.get(i).closing;

			mrData[i][OneDayDataClass.LOW] = (float) 100
					* (candleList.get(i + 1).low - candleList.get(i).closing)
					/ candleList.get(i).closing;

			mrData[i][OneDayDataClass.HIGH] = (float) 100
					* (candleList.get(i + 1).high - candleList.get(i).closing)
					/ candleList.get(i).closing;

			mrData[i][OneDayDataClass.CLOSING] = (float) 100
					* (candleList.get(i + 1).closing - candleList.get(i).closing)
					/ candleList.get(i).closing;

			mrData[i][OneDayDataClass.TRADING_VOLUME] = (float) 100
					* (candleList.get(i + 1).getTradingVolume() - candleList.get(i).getTradingVolume())
					/ candleList.get(i).getTradingVolume();



		}

		FiveDayDataClass[] fiveDayData = new FiveDayDataClass[mrData.length - NUM_OF_DAY_IN_STOCK_DATA];
		for(int i=0; i < fiveDayData.length; i++){
			fiveDayData[i] = new FiveDayDataClass();

			for(int j=0; j < NUM_OF_DAY_IN_STOCK_DATA; j++){
				for(int k=0; k < NUM_OF_VARIABLES_IN_DALIY_DATA; k++){
					fiveDayData[i].dailyData[j].preMR[k] = mrData[i + j][k];
				}
			}

			fiveDayData[i].sufClosingMR = mrData[i+5][OneDayDataClass.CLOSING];
		}

		LearningFiveDayDataClass x = new LearningFiveDayDataClass();
		LogCsvClass log = new LogCsvClass("1");
		List<String> logColumn = new ArrayList<String>();

		String[] dn = new String[5];
		dn [0] = "open";
		dn [1] = "low";
		dn [2] = "high";
		dn [3] = "closing";
		dn [4] = "trading_volume";

		for(int i=0; i < NUM_OF_DAY_IN_STOCK_DATA; i++){
			for(int j=0; j < 5; j++){
					logColumn.add("day" + i + "_" + dn[j]);
			}

			for(int j=0; j < 5; j++){
				logColumn.add("day" + i + "_" + dn[j] + "_unit");
			}
		}
		logColumn.add("average");
		logColumn.add("std");
		logColumn.add("influenceDist");
		logColumn.add("n");
		logColumn.add("distanceSum");
		logColumn.add("resultClosing");
		logColumn.add("profit");


		log.set(logColumn);


		for (int i=0; i < fiveDayData.length - 1; i++) {
			x.learn(fiveDayData[i]);

			if(i > 30){

				FiveStockResultClass result = x.getCalcResult(fiveDayData[i+1]);

				float profit = 0;
				if(0 < result.mean){
					profit = fiveDayData[i+1].sufClosingMR;
				}
				else if(result.mean < 0){
					profit = -fiveDayData[i+1].sufClosingMR;
				}

				List<String> logStrList = new ArrayList<String>();
				for(int j=0; j < NUM_OF_DAY_IN_STOCK_DATA; j++){
					for(int k=0; k < 5; k++){
							logStrList.add("" + fiveDayData[i].dailyData[j].preMR[k]);
					}

					for(int k=0; k < 5; k++){
						logStrList.add("" + result.unit[j][k]);
					}
				}

				logStrList.add("" + result.mean);
				logStrList.add("" + result.std);
				logStrList.add("" + result.influenceDist);
				logStrList.add("" + result.n);
				logStrList.add("" + result.distanceSum);
				logStrList.add("" + fiveDayData[i+1].sufClosingMR);
				logStrList.add("" + profit);

				log.set(logStrList);

			}

			if(i % 100 == 0) System.out.println(i + "/" + fiveDayData.length + " progressed");
		}

	}


}
