package simple_closing_stock_forcast;

import java.util.ArrayList;
import java.util.List;


/**
 * 比率に変換
 *
 * @author rintaro
 *
 */
public class AbstractStockLearningClass {
	private List<DataClass> data = new ArrayList<DataClass>();

	private float unit = (float) 0.01;


	public void learn(float pastX, float presentX){
		System.out.println("data:(" + pastX +","+ presentX);
		int index = getDataPlace(pastX);
		System.out.println("data place:" + index );

		System.out.println("----------data.size()=" + data.size() + "--------------");
		data.add(index, new DataClass(pastX, presentX) );

	}

	public void printlnData(){
		for (DataClass datapart: data) {
			System.out.println("pastX:" + datapart.pastX + "\tpresentX:" + datapart.presentX);
		}
	}

	private int getEarlyDataPlace(float pastX){
		switch (data.size() ){
		case 0: return 0;
		case 1:
			if(data.get(0).pastX < pastX){
				return 1;
			}
			else{
				return 0;
			}
		default:
		}

		return 0;
	}

	private int getDataPlace(float pastX){
		switch (data.size() ){
		case 0: return 0;
		case 1:
			if(data.get(0).pastX < pastX){
				return 1;
			}
			else{
				return 0;
			}
		default:
			float first = data.get(0).pastX;
			float last = data.get(data.size()-1).pastX;



			if(pastX <= first) {
				return 0;

			}
			if(last <= pastX) {
				return data.size();
			}

			if(first < pastX && pastX < last){

				int ep = getExpectedDataPlace(pastX, first, last);

				System.out.println("ep:" + ep);
				return calcDataPlaceFromEp(ep, pastX);
			}
		}
		System.out.println("error");
		return -1;
	}



	public double getMean(float priceMovements){
		float influenceDistP = (float) 0.05;
		float influenceDistStart = priceMovements - influenceDistP;
		float influenceDistEnd = priceMovements + influenceDistP;
		float influenceDistLength = influenceDistP * (float)2 + (float)1;


		float first = data.get(0).pastX;
		float last = data.get(data.size()-1).pastX;
		int ep = getExpectedDataPlace(influenceDistStart - 1, first, last);

		int index = calcDataPlaceFromEp(ep, influenceDistStart - 1);
		System.out.println("influenceDistStart:" + data.get(index).pastX);

		if(0 < index) System.out.println("back pastX :" + data.get(index -1).pastX);
//
		for(; index < data.size() && data.get(index).pastX < influenceDistStart; index++){
			System.out.println("data.get(index).pastX < pastX;" + data.get(index).pastX + "<" + influenceDistStart
					+ " index:" + index);
		}

		float test;
		if( index < data.size() ) test = data.get(index).pastX;
		else test = data.get(index - 1).pastX;
		if(test < influenceDistStart){
			System.out.println("error");
			return 0;
		}

		int start = index;
		double mean = 0;
		double md = 0;
		for(; index < data.size() && data.get(index).pastX <= influenceDistEnd; index++){
			int mdp;
			int mdFromStart = (int) ((data.get(index).pastX - influenceDistStart) / unit + 1);
			int mdFromEnd = (int) ((influenceDistEnd - data.get(index).pastX) / unit + 1);
			if(mdFromStart < mdFromEnd) mdp = mdFromStart ;
			else mdp = mdFromEnd;


			mean += data.get(index).presentX * mdp;
			md += mdp;



			System.out.println("getMean: data.get(index).pastX:" + data.get(index).pastX
					+ "\tdata.get(index).presentX:" + data.get(index).presentX
					+ "\tmd:" + mdp);
		}

		if( index < data.size() && 0 < index ) System.out.println("last pastX:" + data.get(index-1).pastX + "<=" + influenceDistEnd);
		if(index < data.size() ) System.out.println("last pastX:" + data.get(index).pastX);
		System.out.println("md:" + md);
		mean = mean/(md);
		return mean;

	}

	public double getStD(float priceMovements){
		float influenceDistP = (float) 0.05;
		float influenceDistStart = priceMovements - influenceDistP;
		float influenceDistEnd = priceMovements + influenceDistP;
		float influenceDistLength = influenceDistP * (float)2 + (float)1;

		float first = data.get(0).pastX;
		float last = data.get(data.size()-1).pastX;
		int ep = getExpectedDataPlace(influenceDistStart - 1, first, last);

		int index = calcDataPlaceFromEp(ep, influenceDistStart - 1);
//		System.out.println("influenceDistStart:" + data.get(index).pastX);
//		if( 0 < index )System.out.println("back pastX :" + data.get(index -1).pastX);

		for(; index < data.size() && data.get(index).pastX < influenceDistStart; index++){
//			System.out.println("data.get(index).pastX < pastX;" + data.get(index).pastX + "<" + influenceDistStart
//					+ " index:" + index);
		}

		if(index < data.size() && data.get(index).pastX < influenceDistStart){
			System.out.println("error");
			return 0;
		}
		int start = index;
		double mean = 0;
		double md = 0;
		for(; index < data.size() && data.get(index).pastX <= influenceDistEnd; index++){
			int mdp;
			int mdFromStart = (int) ((data.get(index).pastX - influenceDistStart) / unit + 1);
			int mdFromEnd = (int) ((influenceDistEnd - data.get(index).pastX) / unit + 1);
			if(mdFromStart < mdFromEnd) mdp = mdFromStart + 1;
			else mdp = mdFromEnd;

			mean += data.get(index).presentX * mdp;
			md += mdp;
//			System.out.println("data.get(index).pastX" + data.get(index).pastX
//					+ "\tdata.get(index).presentX" + data.get(index).presentX
//					+ "\tmd:" + mdp);
		}

//		if(0 < index) System.out.println("last pastX:" + data.get(index-1).pastX + "<=" + influenceDistEnd);
//		if(index < data.size() ) System.out.println("last pastX:" + data.get(index).pastX);

		mean = mean/(md);



		index = start;
		double std = 0;
		for(; index < data.size() && data.get(index).pastX <= influenceDistEnd; index++){
			int mdp;
			int mdFromStart = (int) (data.get(index).pastX - influenceDistStart);
			int mdFromEnd = (int) (influenceDistEnd - data.get(index).pastX + 1);
			if(mdFromStart < mdFromEnd) mdp = mdFromStart + 1;
			else mdp = mdFromEnd;

			double gap = (double) data.get(index).presentX - mean;

			std += gap * gap * (double)mdp;
		}

		std = Math.sqrt( std / (double)md );

		return std;
	}


	private int calcDataPlaceFromEp(int ep, float pastX){

		switch (data.size() ){
		case 0: return 0;
		case 1:
			if(data.get(0).pastX < pastX){
				return 1;
			}
			else{
				return 0;
			}
		default:

		}
		int root = (int) Math.sqrt(data.size()) / 10;
		if(10 < root){
			while(true){
				boolean rootCalcAct = root + 2 < ep && ep < data.size() - root - 1;
				if( rootCalcAct == false) break;
				if(data.get(ep - root).pastX <= pastX && pastX <= data.get(ep).pastX) break;
				else if(pastX <= data.get(ep - root).pastX) ep -= root;
				else if(data.get(ep).pastX <= pastX) ep += root;

				if( ep < 0) {
					ep = 1;
					break;
				}
				if(data.size() - 1 < ep) {
					ep = data.size() - 1;
					break;
				}
			}
		}

		if(ep == 0 ){
			if(pastX <= data.get(ep).pastX){
				return 0;
			}
			else{
				ep = 1;
			}
		}


		if( data.size() <= ep){

			if(data.get(ep).pastX <= pastX){
				return data.size();
			}
			else{
				ep = data.size()- 1;
			}

		}


		while(true){

			if(data.get(ep - 1).pastX <= pastX && pastX <= data.get(ep).pastX) return ep;
			else if(pastX <= data.get(ep - 1).pastX) ep--;
			else if(data.get(ep).pastX <= pastX) ep ++;
			if(ep < 1) return 0;
			else if(data.size() <= ep) return data.size() - 1;
		}

	}

	private int getExpectedDataPlace(float pastX, float first, float last) {
		float d = last - first;
		float h = (float) (data.size() * 2) / d;

		float mid =  (first + last) / (float)2;

		int ep;
		if (4 < data.size()) {
			if (pastX < mid)
				ep = (int) (data.size() * (pastX - first) / d);
			else
				ep = (int) (data.size() - (data.size() * (pastX - last) / d));

			if (ep < 1)
				ep = 1;
			if (data.size() <= ep)
				ep = data.size() - 2;
		} else {
			ep = 1;
		}

		return ep;
	}

	public void printlnDataTruth() {
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i).pastX < data.get(i - 1).pastX) {
				System.out.println("data imput error " + i + " to " + (i - 1));
			}
		}
	}

	private class DataClass {
		public float pastX;
		public float presentX;

		public DataClass(float pastX, float presentX) {
			this.pastX = pastX;
			this.presentX = presentX;
		}

	}

}
