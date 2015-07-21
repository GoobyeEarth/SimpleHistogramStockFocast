package library;

public class StockCandleClass {
	
	public int date;
	public String symbol;
	public float opening;
	public float high;
	public float low;
	public float closing;
	public int turnover;
	
	public float getTradingVolume(){
		return closing * turnover;
	}

}
