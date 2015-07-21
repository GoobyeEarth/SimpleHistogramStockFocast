package simple_closing_stock_forcast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import library.CsvReaderClass;
import library.SQLiteRowStockDataDaoClass;
import library.StockCandleClass;
import library.SystemVariableInterface;

public class MainSimpleClosingStockForcastClass implements SystemVariableInterface {

	/*
	 * 特定の株価データを取得
	 * 
	 * 分析方法は どうやってキャストするか？？
	 */
	public static void main(String[] arg) {

		test();
//		test2();
	}
	
	private static void test2(){
		SQLiteRowStockDataDaoClass stockDao = new SQLiteRowStockDataDaoClass();
		List<StockCandleClass> stockCandle = stockDao.findBySymbol("1301");
		System.out.println("list.size:" + stockCandle.size() );
		float movementRate = 0;
		for(int i=0; i < stockCandle.size() - 1; i++){
			float mrp = (stockCandle.get(i + 1).closing - stockCandle.get(i).closing) / stockCandle.get(i).closing;
			if(mrp < 0) movementRate += - mrp;
			else movementRate += mrp;
			System.out.println("mrp" + mrp);
			System.out.println("movement" + movementRate);
			
		}
		movementRate = movementRate / (stockCandle.size() - 1);
		
		System.out.println("" + movementRate);
	}
	
	private void setRandomStockPrice(){
		int[] stockClosingPrice = new int[100];
		for (int k = 0; k < stockClosingPrice.length; k++) {
			Random r = new Random();
			stockClosingPrice[k] = 90 + r.nextInt(10);
		}
	}

	private static void test() {
		
		SQLiteRowStockDataDaoClass stockDao = new SQLiteRowStockDataDaoClass();
		List<StockCandleClass> stockCandle = stockDao.findBySymbol("1301");
		System.out.println("list.size:" + stockCandle.size() );
		
		
		float[] priceMovements = new float[stockCandle.size() - 1];
	

		
		for (int l = 0; l < priceMovements.length; l++) {
			priceMovements[l] = (float) 100 * (stockCandle.get(l + 1).closing - stockCandle.get(l).closing) / stockCandle.get(l).closing;
			
		}
		List<Float> tradeLog = new ArrayList<Float>();
		List<Integer> tlIndex = new ArrayList<Integer>();
		AbstractStockLearningClass x = new AbstractStockLearningClass();
		for (int i = 0; i < priceMovements.length - 2; i++) {

			x.learn(priceMovements[i], priceMovements[i + 1]);
			
			if (3 < i) {
				double mean = x.getMean(priceMovements[i + 1]);
				double std = x.getStD(priceMovements[i + 1]);
				System.out.println("mean;" + mean +  "std:" + std + " pastX;" + priceMovements[i] + " presentX" + priceMovements[i + 1]);
				if(std /2 < mean) {
					tradeLog.add(priceMovements[i + 2]);
					tlIndex.add(i);
				}
				if(mean <  - std /2) {
					tradeLog.add(-priceMovements[i + 2]);
					tlIndex.add(i);
				}
				
			}
			
		}
		
		int mean = 0;
		for (Float integer : tradeLog) {
			System.out.println("trdeLog:" + integer);
			mean += integer;
			
		}
		
		CsvReaderClass csv = new CsvReaderClass(WORKSPACE);
		List<List<String> > tradeLogSt = new ArrayList<List<String> >();
		
		for (int i=0; i<tradeLog.size(); i++) {
			List<String> tlp= new ArrayList<String>();
			tlp.add("" + tradeLog.get(i) );
			tlp.add("" + tlIndex.get(i) );
			tradeLogSt.add(tlp);
		}
		x.printlnData();
		csv.write("simpleStockForcast", tradeLogSt);
		
		
	}

	

}
