package turnover_price;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import library.SQLiteRowStockDataDaoClass;
import library.SimpleThreadClass;
import library.StockCalcDoubleResultDaoClass;
import library.StockCalcLongResultDaoClass;
import library.StockCandleClass;
import library.StringLongClass;
import library.SystemVariableInterface;

public class MainTurnOverPriceClass implements SystemVariableInterface{

	/*
	 * averageターンオーバー*数を取りたい
	 *
	 * 手順１　symbol毎のデータを取る。
	 * 手順２　出来高が無いものを排除
	 * 手順３　平均の計算。
	 * 手順４　データをデータベースに放り込む
	 *
	 *
	 */
	public static void main(String[] args) {
		
		StockCalcDoubleResultDaoClass corrCoef = new StockCalcDoubleResultDaoClass(CORRELATION_COEFFICIENT);
		List<String> symbolList = corrCoef.getSymbolList();
		
		
		
		StringLongClass[] dataArray = new StringLongClass[symbolList.size() ];
		
		SimpleThreadClass thread = new SimpleThreadClass(4);
		
		for(int i=0; i < symbolList.size(); i++){
			final int index = i;
			thread.addProcess(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					SQLiteRowStockDataDaoClass stockDao = new SQLiteRowStockDataDaoClass();
					List<StockCandleClass> stockData = stockDao.findBySymbol(symbolList.get(index));
					long toP = calcTurnOverPrice(stockData);
					dataArray[index] = new StringLongClass(symbolList.get(index),  toP);
					System.out.println(symbolList.get(index) + "\t turnoverPrice:" + toP);
					
					return null;
				}
			});
			
			
		}
		thread.threadExecute();
		List<StringLongClass> dataList = Arrays.asList(dataArray);
		
		StockCalcLongResultDaoClass turnoverDao = new StockCalcLongResultDaoClass(TURNOVER_PRICE);
		turnoverDao.insert(dataList);
		
	}

	private static long calcTurnOverPrice(List<StockCandleClass> stockData) {
		long turnoverPrice =0;
		for(int i=0; i < stockData.size(); i++){
			turnoverPrice += (long)stockData.get(i).turnover * (long)stockData.get(i).closing ;
		}
		
		return turnoverPrice / (long)stockData.size() ;
		
		
	}

}
