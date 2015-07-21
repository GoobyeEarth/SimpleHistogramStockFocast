package sqlite_to_csv;

import java.util.ArrayList;
import java.util.List;

import library.CalcCorrelationCoefficientClass;
import library.SQLiteRowStockDataDaoClass;
import library.StockCalcDoubleResultDaoClass;
import library.StockCandleClass;
import library.StringDoubleClass;
import library.SystemVariableInterface;

class CopyOfCalcAndCsvMainClass implements SystemVariableInterface{
	private static SQLiteRowStockDataDaoClass stockDao;
	
	public static void main(String[] args) {

		getCorrelation();
		
	}
	
	
	
	private static void getCorrelation(){

		stockDao = new SQLiteRowStockDataDaoClass();
		List<String> symbolList = stockDao.getSymbolList();
		List<Integer> dateList = stockDao.getDateList();
		List<StockCandleClass> nikkei = stockDao.findBySymbol(NIKKEI_AVE);
		
		List<StringDoubleClass> corrCoefDataList  = new ArrayList<StringDoubleClass>();
		
		
		
		for(int i=0; i < symbolList.size(); i++){
			List<StockCandleClass> stockData = stockDao.findBySymbol(symbolList.get(i) );
			double cc = getCorrelationCoeffFromDailyData(nikkei, stockData, dateList);
			
			corrCoefDataList.add(new StringDoubleClass(symbolList.get(i), cc) );
			
			System.out.println("symbol: " + symbolList.get(i) + " cc=" + cc + " progress:" + i  * 100 / (symbolList.size()) + "%"
					+ " i="+ i + " syimbolList.size=" + symbolList.size() );
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


