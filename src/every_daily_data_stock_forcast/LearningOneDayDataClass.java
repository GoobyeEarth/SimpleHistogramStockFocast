package every_daily_data_stock_forcast;

import java.util.ArrayList;
import java.util.List;

import library.OneDayDataClass;
import library.SystemVariableInterface;

public class LearningOneDayDataClass implements SystemVariableInterface {

	private List<OneDayDataClass>[] pointer = new List[NUM_OF_VARIABLES_IN_DALIY_DATA];
	private double[] unit = new double[NUM_OF_VARIABLES_IN_DALIY_DATA];
	private static final int DATA_NUM = 20;

	public LearningOneDayDataClass() {
		for (int i = 0; i < pointer.length; i++) {
			pointer[i] = new ArrayList<OneDayDataClass>();
		}
	}

	public void learn(OneDayDataClass stockData) {

		for (int i = 0; i < pointer.length; i++) {
			DataPlacerClass placer = new DataPlacerClass(pointer[i], i);
			int index = placer.getDataPlace(stockData);
			pointer[i].add(index, stockData);
		}

	}

	public OneDayResultClass getCalcResult(float[] priceMovements) {

		DataPlacerClass dataResetter = new DataPlacerClass(pointer[0], 0);
		dataResetter.resetDist();

		for (int i = 0; i < pointer.length; i++) {

			DataPlacerClass placer = new DataPlacerClass(pointer[i], i);
			placer.setDistance(priceMovements[i]);
		}

		int influenceDist = getinfluenceDist(priceMovements);

		double wSum = 0;
		OneDayResultClass result = new OneDayResultClass();

		for (int i = 0; i < pointer[0].size(); i++) {

			if (influenceDist > pointer[0].get(i).distance) {
				int weight = influenceDist - pointer[0].get(i).distance;
				wSum += pointer[0].get(i).sufClosingMR * weight;
				result.distanceSum += weight;
				result.n++;

			}
		}
		result.mean = wSum / result.distanceSum;
		result.influenceDist = influenceDist;

		double gapD2Sum = 0;

		for (int i = 0; i < pointer[0].size(); i++) {
			if (influenceDist > pointer[0].get(i).distance) {
				final int weight = influenceDist - pointer[0].get(i).distance;
				final double gap = pointer[0].get(i).sufClosingMR - result.mean;
				gapD2Sum += gap * gap * weight;

			}

			result.std = Math.sqrt(gapD2Sum / (double) result.distanceSum);
			result.unit = unit;
		}

		return result;
	}

	public int getinfluenceDist(float[] priceMovements) {

		int influenceDist = 3;

		int sum = 0;
		while (sum < DATA_NUM) {
			influenceDist += 1;
			int n = 0;
			for (int i = 0; i < pointer[0].size(); i++) {
				if (influenceDist > pointer[0].get(i).distance)
					n++;
			}

			sum = n;
		}

		return influenceDist;
	}

	private class DataPlacerClass {
		private int MRindex;
		private List<OneDayDataClass> pointer;

		public DataPlacerClass(List<OneDayDataClass> pointer, int MRindex) {
			this.pointer = pointer;
			this.MRindex = MRindex;

		}

		private double getStdWhole(int MRindex) {
			double sum = 0;
			for (int i = 0; i < pointer.size(); i++) {
				sum += pointer.get(i).preMR[MRindex];
			}
			double average = sum / pointer.size();

			double gapSum = 0;
			for (int i = 0; i < pointer.size(); i++) {
				gapSum += (pointer.get(i).preMR[MRindex] - average)
						* (pointer.get(i).preMR[MRindex] - average);
			}

			return Math.sqrt(gapSum) / pointer.size();
		}

		public void setDistance(float priceMovements) {
			unit[MRindex] = getStdWhole(MRindex) / 5;
			for (int i = 0; i < pointer.size(); i++) {

				pointer.get(i).distance += (int) (Math.abs(priceMovements
						- pointer.get(i).preMR[MRindex]) / unit[MRindex]);

			}

		}

		public void resetDist() {
			for (int i = 0; i < pointer.size(); i++) {
				pointer.get(i).inDist = true;
				pointer.get(i).distance = 0;
			}

		}

		public int getDataPlace(OneDayDataClass insertingData) {
			switch (pointer.size()) {
			case 0:
				return 0;
			case 1:
				if (pointer.get(0).preMR[MRindex] < insertingData.preMR[MRindex]) {
					return 1;
				} else {
					return 0;
				}
			default:
				float first = pointer.get(0).preMR[MRindex];
				float last = pointer.get(pointer.size() - 1).preMR[MRindex];

				if (insertingData.preMR[MRindex] <= first) {
					return 0;

				}
				if (last <= insertingData.preMR[MRindex]) {
					return pointer.size();
				}

				if (first < insertingData.preMR[MRindex]
						&& insertingData.preMR[MRindex] < last) {

					int ep = getExpectedDataPlace(insertingData, first, last);

					return calcDataPlaceFromEp(ep, insertingData);
				}
			}
			System.out.println("error");

			return 1;
		}

		public int getExpectedDataPlace(OneDayDataClass insertingData,
				float first, float last) {
			float d = last - first;
			float h = (float) (pointer.size() * 2) / d;

			float mid = (first + last) / (float) 2;

			int ep;
			if (4 < pointer.size()) {
				if (insertingData.preMR[MRindex] < mid)
					ep = (int) (pointer.size()
							* (insertingData.preMR[MRindex] - first) / d);
				else
					ep = (int) (pointer.size() - (pointer.size()
							* (insertingData.preMR[MRindex] - last) / d));

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
				OneDayDataClass insertingData) {
			switch (pointer.size()) {
			case 0:
				return 0;
			case 1:
				if (pointer.get(0).preMR[MRindex] < insertingData.preMR[MRindex]) {
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
					if (pointer.get(ep - root).preMR[MRindex] <= insertingData.preMR[MRindex]
							&& insertingData.preMR[MRindex] <= pointer.get(ep).preMR[MRindex])
						break;
					else if (insertingData.preMR[MRindex] <= pointer.get(ep
							- root).preMR[MRindex])
						ep -= root;
					else if (pointer.get(ep).preMR[MRindex] <= insertingData.preMR[MRindex])
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
				if (insertingData.preMR[MRindex] <= pointer.get(ep).preMR[MRindex]) {
					return 0;
				} else {
					ep = 1;
				}
			}

			if (pointer.size() <= ep) {

				if (pointer.get(ep).preMR[MRindex] <= insertingData.preMR[MRindex]) {
					return pointer.size();
				} else {
					ep = pointer.size() - 1;
				}

			}

			while (true) {

				if (pointer.get(ep - 1).preMR[MRindex] <= insertingData.preMR[MRindex]
						&& insertingData.preMR[MRindex] <= pointer.get(ep).preMR[MRindex])
					return ep;
				else if (insertingData.preMR[MRindex] <= pointer.get(ep - 1).preMR[MRindex])
					ep--;
				else if (pointer.get(ep).preMR[MRindex] <= insertingData.preMR[MRindex])
					ep++;
				if (ep < 1)
					return 0;
				else if (pointer.size() <= ep)
					return pointer.size() - 1;
			}

		}
	}

}
