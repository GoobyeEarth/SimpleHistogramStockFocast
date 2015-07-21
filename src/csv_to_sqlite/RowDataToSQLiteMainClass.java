package csv_to_sqlite;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import library.CsvReaderClass;
import library.SQLiteRowStockDataDaoClass;
import library.StockCandleClass;
import library.SystemVariableInterface;

public class RowDataToSQLiteMainClass  implements SystemVariableInterface{

	private static Statement stmt;
	private static ResultSet rs;
	private static SQLiteRowStockDataDaoClass stockDao;
	
	private static final String[] STOCK_ROW_DATA_FILES= {
		"201310"
		,"201311"
		,"201312"
		,"201401"
		,"201402"
		,"201403"
		,"201404"
		,"201405"
		,"201406"
		,"201407"
		,"201408"
		,"201409"
		,"201410"
		,"201411"
		,"201412"
		,"201501"
		,"201502"
		,"201503"
		,"201504"
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		stockDao = new SQLiteRowStockDataDaoClass();
		for(int i=0; i < STOCK_ROW_DATA_FILES.length; i++){
			csvToSQLite(STOCK_ROW_DATA_FILES[i]);
		}

	}
	
	private static void csvToSQLite(String fileName){
		CsvReaderClass csvReader = new CsvReaderClass(WORKSPACE + DIRECTORY_ROW_DATA);
		boolean done = logCheck(csvReader, fileName);
		if(done == true) return ;
		
		csvReader.append(LOG, fileName);
		
		List<List<String> > csvData = csvReader.read(fileName);
		String fileNameComment = " fileName:" + fileName + ".csv";
		
		System.out.println("loading file has finished" + fileNameComment);
		
		dataProcessing(csvData);
		System.out.println("DATA PROCESSING has finished");
		
		List<StockCandleClass> stockDataList = formating(csvData);
		System.out.println("formating has finished");
		
		
		stockDao.insert(stockDataList);
		
		System.out.println("inserting has finished");

		csvReader.append(LOG, "succeed");
		System.out.println(csvData.size() );
	}
	
	private static List<StockCandleClass> formating(List<List<String>> csvData){
		List<StockCandleClass> stockDataList = new ArrayList<StockCandleClass>();
		
		for(int i=0; i < csvData.size(); i++){
			
			List<String> lineData = csvData.get(i);

			StockCandleClass candleData = new StockCandleClass();
			candleData.date = Integer.parseInt(lineData.get(0));
			candleData.symbol = lineData.get(1);
			candleData.opening = Float.parseFloat(lineData.get(2));
			candleData.high = Float.parseFloat(lineData.get(3));
			candleData.low = Float.parseFloat(lineData.get(4));
			candleData.closing = Float.parseFloat(lineData.get(5));
			candleData.turnover = Integer.parseInt(lineData.get(6));
			stockDataList.add(candleData);
			
		}
		return stockDataList;
	}
	
	private static void dataProcessing(List<List<String>> csvData){
		for(int i=0; i < csvData.size(); i++){
			
			List<String> lineData = csvData.get(i);
			
			if(lineData.size() >= 1) { 
				String date = lineData.get(0);
				date = date.replace("/", "");

				lineData.set(0, date);
				csvData.set(i, lineData);
			
			}
			else{
				System.out.println("lineData is blank line:" + i);
			}
		}
	}
	
	private static boolean logCheck(CsvReaderClass csvReader, String fileName){
		List<List<String>> logData = csvReader.read(LOG);
		for(int i=0; i < logData.size(); i++){
			for(int j=0; j < logData.get(i).size(); j++){
				if(logData.get(i).get(j).equals(fileName)) {
					return true;
				}
			}
		}
		return false;
	}
	
}