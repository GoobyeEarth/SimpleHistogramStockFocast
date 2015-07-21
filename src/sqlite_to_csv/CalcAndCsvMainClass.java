package sqlite_to_csv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import library.CalcCorrelationCoefficientClass;
import library.SQLiteRowStockDataDaoClass;
import library.SimpleThreadClass;
import library.StockCalcDoubleResultDaoClass;
import library.StockCandleClass;
import library.StringDoubleClass;
import library.SystemVariableInterface;

class CalcAndCsvMainClass implements SystemVariableInterface{
	private static SQLiteRowStockDataDaoClass stockDao;
	
	public static void main(String[] args) {

		getCorrelation();
		
	}
	
	
	
	private static void getCorrelation(){

		stockDao = new SQLiteRowStockDataDaoClass();
		List<String> symbolList = stockDao.getSymbolList();
		List<Integer> dateList = stockDao.getDateList();
		List<StockCandleClass> nikkei = stockDao.findBySymbol(NIKKEI_AVE);
		
		
		SimpleThreadClass thread = new SimpleThreadClass(3);
		
		
		double[] ccArray = new double[symbolList.size()];
		for(int i=0; i < symbolList.size(); i++){
			
			final int index = i;
			thread.addProcess(new Callable<Void>() {
				
				@Override
				public Void call(){
					// TODO 自動生成されたメソッド・スタブ
					
					SQLiteRowStockDataDaoClass stockDaoRun = new SQLiteRowStockDataDaoClass();
					List<StockCandleClass> stockData = stockDaoRun.findBySymbol(symbolList.get(index) );
					ccArray[index] = getCorrelationCoeffFromDailyData(nikkei, stockData, dateList);
					
					System.out.println("symbol: " + symbolList.get(index) + " ccArray[index] =" + ccArray[index] + " progress:" + index  * 100 / (symbolList.size()) + "%"
							+ " i="+ index + " syimbolList.size=" + symbolList.size() );
					return null;
				}
			});
			
		}
		
		thread.threadExecute();
		
		List<StringDoubleClass> corrCoefDataList  = new ArrayList<StringDoubleClass>();
		
		for(int i=0; i < symbolList.size(); i++){
			corrCoefDataList.add(new StringDoubleClass(symbolList.get(i), ccArray[i]) );
		}
		
		
		
		StockCalcDoubleResultDaoClass corrCoefResultDao = new StockCalcDoubleResultDaoClass(CORRELATION_COEFFICIENT);
		corrCoefResultDao.insert(corrCoefDataList);
	}

	
	private static double getCorrelationCoeffFromDailyData(List<StockCandleClass> stockXlist, 
			List<StockCandleClass> stockYlist, 
			List<Integer> dateList){
		
		
		
		List<Double> dataListX = new ArrayList<Double>();
		List<Double> dataListY = new ArrayList<Double>();
		
		int stockXindex = 0;
		int stockYindex = 0;
		
		
		for(int i=0; i < dateList.size(); i++){
			
			if(stockXlist.size() == stockXindex || stockYlist.size() == stockYindex) break;
			
			if(dateList.get(i) == stockXlist.get(stockXindex).date && dateList.get(i) == stockYlist.get(stockYindex).date){
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
		
		
		CalcCorrelationCoefficientClass ccc = new CalcCorrelationCoefficientClass(dataListX, dataListY);
		return ccc.correlationCoefficient() ;
		
	}
	
}


