package library;

import java.util.List;

public class CalcCorrelationCoefficientClass {
	private double[] x;
	private double[] y;
	private double x_ave = 0;
	private double y_ave = 0;
	
	private int dataNum;
	
	private double[] x_gaps;
	private double[] y_gaps;
	
	public CalcCorrelationCoefficientClass(List<Double> x, List<Double> y){
		
		
		
		if(x.size() != y.size()){
			System.out.println("data is not same number");
			if(x.size() < y.size()){
				dataNum = x.size();
			}
			else{
				dataNum = y.size();
			}
		}
		else{
			dataNum = x.size();
		}
		
		
		for(int i=0; i < dataNum; i++){
			x_ave += x.get(i);
			y_ave += y.get(i);
		}
		x_ave = x_ave/dataNum;
		y_ave = y_ave/dataNum;
		
		x_gaps = new double[dataNum];
		y_gaps = new double[dataNum];
		
		for(int i=0; i < dataNum; i++){
			x_gaps[i] = x.get(i) - x_ave;
			y_gaps[i] = y.get(i) - y_ave;
		}
	}
	
	public double correlationCoefficient(){
		return covariance()/ ( standardDeviationX() * standardDeviationY() );
	}
	
	public double covariance(){
		double sumOfParts = 0;
		
		for(int i=0; i < dataNum; i++){
			sumOfParts += x_gaps[i] * y_gaps[i];
		}
		
		return sumOfParts/dataNum;
		
	}
	
	
	
	public double standardDeviationX(){
		
		double _2dOfparts;
		double sumOf2dParts = 0;
		
		
		
		
		for(int i=0; i < dataNum; i++){
			_2dOfparts = x_gaps[i]* x_gaps[i];
			sumOf2dParts += _2dOfparts;
		}
		
		double variance = sumOf2dParts/dataNum;
		
		
		return Math.sqrt(variance);
	}
	
	public double standardDeviationY(){
		
		double _2dOfparts;
		double sumOf2dParts = 0;
		
		
		
		
		for(int i=0; i < dataNum; i++){
			_2dOfparts = y_gaps[i]* y_gaps[i];
			sumOf2dParts += _2dOfparts;
		}
		
		double variance = sumOf2dParts/dataNum;
		
		
		return Math.sqrt(variance);
	}

}
