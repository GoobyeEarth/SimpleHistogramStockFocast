package five_stock_forcast;

import java.util.ArrayList;
import java.util.List;

import library.FiveDayDataClass;
import library.SystemVariableInterface;

public class LearningFiveDayDataClass implements SystemVariableInterface {

	private List<FiveDayDataClass>[][] pointer = new List[NUM_OF_DAY_IN_STOCK_DATA][NUM_OF_VARIABLES_IN_DALIY_DATA];

	private double[][] unit = new double[NUM_OF_DAY_IN_STOCK_DATA][NUM_OF_VARIABLES_IN_DALIY_DATA];

	private static final int DATA_NUM = 20;

	public LearningFiveDayDataClass() {
		for (int i = 0; i < pointer.length; i++) {
			for(int j=0; j < pointer[i].length; j++){
				pointer[i][j] = new ArrayList<FiveDayDataClass>();
			}

		}
	}

	public void learn(FiveDayDataClass stockData) {

		for (int i = 0; i < pointer.length; i++) {
			for(int j=0; j < pointer[i].length; j++){
				DataPlacerClass placer = new DataPlacerClass(pointer[i][j], new int[]{i,j});
				int index = placer.getDataPlace(stockData);
				pointer[i][j].add(index, stockData);
			}

		}

	}

	public FiveStockResultClass getCalcResult(FiveDayDataClass priceMovements) {
		DataPlacerClass dataResetter = new DataPlacerClass(pointer[0][0], new int[]{0,0});
		dataResetter.resetDist();

		for (int i = 0; i < pointer.length; i++) {
			for(int j=0; j < pointer[i].length; j++){
				DataPlacerClass placer = new DataPlacerClass(pointer[i][j], new int[]{i,j});
				placer.setDistance(priceMovements.dailyData[i].preMR[j]);
			}


		}

		int influenceDist = getinfluenceDist();

		double wSum = 0;
		FiveStockResultClass result = new FiveStockResultClass();

		for (int i = 0; i < pointer[0][0].size(); i++) {

			if (influenceDist > pointer[0][0].get(i).dailyData[0].distance) {
				int weight = influenceDist - pointer[0][0].get(i).dailyData[0].distance;
				wSum += pointer[0][0].get(i).sufClosingMR * weight;
				result.distanceSum += weight;
				result.n++;

			}
		}
		result.mean = wSum / result.distanceSum;
		result.influenceDist = influenceDist;

		double gapD2Sum = 0;

		for (int i = 0; i < pointer[0][0].size(); i++) {
			if (influenceDist > pointer[0][0].get(i).dailyData[0].distance) {
				final int weight = influenceDist - pointer[0][0].get(i).dailyData[0].distance;
				final double gap = pointer[0][0].get(i).sufClosingMR - result.mean;
				gapD2Sum += gap * gap * weight;

			}

			result.std = Math.sqrt(gapD2Sum / (double) result.distanceSum);
			result.unit = unit;
		}

		return result;
	}

	public int getinfluenceDist() {


		int influenceDist = 3;

		int sum = 0;
		while (sum < DATA_NUM) {
			influenceDist += 1;
			int n = 0;
			for (int i = 0; i < pointer[0][0].size(); i++) {
				if (influenceDist > pointer[0][0].get(i).dailyData[0].distance)
					n++;
			}

			sum = n;
		}

		return influenceDist;
	}

	private class DataPlacerClass {
		private int[] MRindex;
		private List<FiveDayDataClass> pointer;

		public DataPlacerClass(List<FiveDayDataClass> pointer, int[] MRindex) {
			this.pointer = pointer;
			this.MRindex = MRindex;

		}

		private double getStdWhole(int[] MRindex) {
			double sum = 0;
			for (int i = 0; i < pointer.size(); i++) {
				sum += pointer.get(i).dailyData[MRindex[0]].preMR[MRindex[1]];
			}
			double average = sum / pointer.size();

			double gapSum = 0;
			for (int i = 0; i < pointer.size(); i++) {
				gapSum += (pointer.get(i).dailyData[MRindex[0]].preMR[MRindex[1]] - average)
						* (pointer.get(i).dailyData[MRindex[0]].preMR[MRindex[1]] - average);
			}

			return Math.sqrt(gapSum) / pointer.size();
		}

		public void setDistance(float priceMovements) {
			unit[MRindex[0]][MRindex[1]] = getStdWhole(MRindex) / 5;
			for (int i = 0; i < pointer.size(); i++) {

				pointer.get(i).dailyData[0].distance += (int) (Math.abs(priceMovements
						- pointer.get(i).dailyData[MRindex[0]].preMR[MRindex[1]]) / unit[MRindex[0]][MRindex[1]]);

			}

		}

		public void resetDist() {
			for (int i = 0; i < pointer.size(); i++) {
				pointer.get(i).dailyData[0].inDist = true;
				pointer.get(i).dailyData[0].distance = 0;
			}

		}

		public int getDataPlace(FiveDayDataClass insertingData) {
			switch (pointer.size()) {
			case 0:
				return 0;
			case 1:
				if (pointer.get(0).dailyData[MRindex[0]].preMR[MRindex[1]] < insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]) {
					return 1;
				} else {
					return 0;
				}
			default:
				float first = pointer.get(0).dailyData[MRindex[0]].preMR[MRindex[1]];
				float last = pointer.get(pointer.size() - 1).dailyData[MRindex[0]].preMR[MRindex[1]];

				if (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= first) {
					return 0;

				}
				if (last <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]) {
					return pointer.size();
				}

				if (first < insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]
						&& insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] < last) {

					int ep = getExpectedDataPlace(insertingData, first, last);

					return calcDataPlaceFromEp(ep, insertingData);
				}
			}
			System.out.println("error");

			return 1;
		}

		public int getExpectedDataPlace(FiveDayDataClass insertingData,
				float first, float last) {
			float d = last - first;
			float h = (float) (pointer.size() * 2) / d;

			float mid = (first + last) / (float) 2;

			int ep;
			if (4 < pointer.size()) {
				if (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] < mid)
					ep = (int) (pointer.size()
							* (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] - first) / d);
				else
					ep = (int) (pointer.size() - (pointer.size()
							* (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] - last) / d));

				if (ep < 1)
					ep = 1;
				if (pointer.size() <= ep)
					ep = pointer.size() - 2;
			} else {
				ep = 1;
			}

			return ep;
		}

		public int calcDataPlaceFromEp(int ep,
				FiveDayDataClass insertingData) {
			switch (pointer.size()) {
			case 0:
				return 0;
			case 1:
				if (pointer.get(0).dailyData[MRindex[0]].preMR[MRindex[1]] < insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]) {
					return 1;
				} else {
					return 0;
				}
			default:

			}
			int root = (int) Math.sqrt(pointer.size()) / 10;
			if (10 < root) {
				while (true) {
					boolean rootCalcAct = root + 2 < ep
							&& ep < pointer.size() - root - 1;
					if (rootCalcAct == false)
						break;
					if (pointer.get(ep - root).dailyData[MRindex[0]].preMR[MRindex[1]] <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]
							&& insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]])
						break;
					else if (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= pointer.get(ep
							- root).dailyData[MRindex[0]].preMR[MRindex[1]])
						ep -= root;
					else if (pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]] <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]])
						ep += root;

					if (ep < 0) {
						ep = 1;
						break;
					}
					if (pointer.size() - 1 < ep) {
						ep = pointer.size() - 1;
						break;
					}

				}

			}

			if (ep == 0) {
				if (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]]) {
					return 0;
				} else {
					ep = 1;
				}
			}

			if (pointer.size() <= ep) {

				if (pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]] <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]) {
					return pointer.size();
				} else {
					ep = pointer.size() - 1;
				}

			}

			while (true) {

				if (pointer.get(ep - 1).dailyData[MRindex[0]].preMR[MRindex[1]] <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]]
						&& insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]])
					return ep;
				else if (insertingData.dailyData[MRindex[0]].preMR[MRindex[1]] <= pointer.get(ep - 1).dailyData[MRindex[0]].preMR[MRindex[1]])
					ep--;
				else if (pointer.get(ep).dailyData[MRindex[0]].preMR[MRindex[1]] <= insertingData.dailyData[MRindex[0]].preMR[MRindex[1]])
					ep++;
				if (ep < 1)
					return 0;
				else if (pointer.size() <= ep)
					return pointer.size() - 1;
			}

		}
	}

}
