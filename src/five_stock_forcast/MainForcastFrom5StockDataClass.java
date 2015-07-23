package five_stock_forcast;

import java.util.ArrayList;
import java.util.List;

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
		List<StockCandleClass>[] candleList = new List[NUM_OF_STOCK];//srsDao.findBySymbol("1301");
		
		candleList[0] = srsDao.findBySymbol("1301");
		candleList[1] = srsDao.findBySymbol("1305");
		candleList[2] = srsDao.findBySymbol("1306");
		candleList[3] = srsDao.findBySymbol("1308");
		candleList[4] = srsDao.findBySymbol("1309");
		
		float[][][] mrData = new float[NUM_OF_STOCK][][];
		for(int i=0; i < mrData.length; i++){
			mrData[i] = toMRData(candleList[i]);
		}

		FiveStockDataClass[] learningData = new FiveStockDataClass[mrData[0].length - NUM_OF_DAY_IN_STOCK_DATA];
		System.out.println("" + mrData.length);
		for(int i=0; i < learningData.length; i++){
			learningData[i] = new FiveStockDataClass();

			for(int j=0; j < NUM_OF_STOCK; j++){
				for(int k=0; k < NUM_OF_DAY_IN_STOCK_DATA; k++){
					for(int l=0; l < NUM_OF_VARIABLES_IN_DALIY_DATA; l++){
						learningData[i].stockData[j].dailyData[k].preMR[l] = mrData[j][i + k][l];
						System.out.println(i + "," + j + "," + k);
					}

				}

			}

			learningData[i].sufClosingMR = mrData[0/*自分が統計取りたい株式*/][i+5][OneDayDataClass.CLOSING];
		}

		LearningFiveStockDataClass x = new LearningFiveStockDataClass();
		LogCsvClass log = new LogCsvClass("1");
		List<String> logColumn = new ArrayList<String>();

		String[] dn = new String[NUM_OF_VARIABLES_IN_DALIY_DATA];
		dn [0] = "open";
		dn [1] = "low";
		dn [2] = "high";
		dn [3] = "closing";
		dn [4] = "trading_volume";

		for(int i=0; i < NUM_OF_STOCK; i++){
			for(int j=0; j < NUM_OF_DAY_IN_STOCK_DATA; j++){
				for(int k=0; k < NUM_OF_VARIABLES_IN_DALIY_DATA; k++){
					logColumn.add("stock" + i + "_day" + j + "_" + dn[k]);
				}

			}

			for(int j=0; j < NUM_OF_DAY_IN_STOCK_DATA; j++){
				for(int k=0; k < NUM_OF_VARIABLES_IN_DALIY_DATA; k++){
					logColumn.add("stock" + i + "_day" + j + "_" + dn[k] + "_unit");
				}
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


		for (int i=0; i < learningData.length - 1; i++) {
			x.learn(learningData[i]);

			if(i > 30){

				FiveStockResultClass result = x.getCalcResult(learningData[i+1]);

				float profit = 0;
				if(0 < result.mean){
					profit = learningData[i+1].sufClosingMR;
				}
				else if(result.mean < 0){
					profit = -learningData[i+1].sufClosingMR;
				}

				List<String> logStrList = new ArrayList<String>();
				for(int j=0; j < NUM_OF_STOCK; j++){
					for(int k=0; k < NUM_OF_DAY_IN_STOCK_DATA; k++){
						for(int l=0; l < NUM_OF_VARIABLES_IN_DALIY_DATA; l++){
							logStrList.add("" + learningData[i].stockData[j].dailyData[k].preMR[l]);
						}
					}

					for(int k=0; k < NUM_OF_DAY_IN_STOCK_DATA; k++){
						for(int l=0; l < NUM_OF_VARIABLES_IN_DALIY_DATA; l++){
							logStrList.add("" + result.unit[j][k][l]);
						}
					}
				}

				logStrList.add("" + result.mean);
				logStrList.add("" + result.std);
				logStrList.add("" + result.influenceDist);
				logStrList.add("" + result.n);
				logStrList.add("" + result.distanceSum);
				logStrList.add("" + learningData[i+1].sufClosingMR);
				logStrList.add("" + profit);

				log.set(logStrList);

			}

			if(i % 10 == 0) System.out.println(i + "/" + learningData.length + " progressed");
		}

	}

	private static float[][] toMRData(List<StockCandleClass> candleList){
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
		return mrData;
	}



}
