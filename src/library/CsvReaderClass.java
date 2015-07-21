package library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReaderClass {
	private String directory;

	public CsvReaderClass(String directory) {
		this.directory = directory;
	}

	public List<List<String>> read(String fileName) {

		List<List<String>> csvData = new ArrayList<List<String>>();

		try {
			// ファイルを読み込む
			FileReader fr = new FileReader(directory + fileName + ".csv");
			BufferedReader br = new BufferedReader(fr);

			// 読み込んだファイルを１行ずつ処理する
			String line;

			while ((line = br.readLine()) != null) {

				String[] sps1 = line.split(",");

				if (sps1 != null) {

					csvData.add(Arrays.asList(sps1));

				}
			}

			br.close();

		} catch (IOException ex) {
			// 例外発生時処理
			ex.printStackTrace();
		}

		return csvData;
	}

	public void write(String fileName, List<List<String>> tableData) {
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName + ".csv", false); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			for (int i = 0; i < tableData.size(); i++) {
				for (int j = 0; j < tableData.get(i).size(); j++) {

					if (j != 0) pw.print(",");
					pw.print(tableData.get(i).get(j));
				}

				pw.println();

			}
			pw.close();

			System.out.println("出力が完了しました。");

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}
	
	public void append(String fileName, List<String> lineData) {
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName + ".csv", true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			for (int i = 0; i < lineData.size(); i++) {

				if (i != 0)
					pw.print(",");
				pw.print(lineData.get(i));
			}
			pw.println();
			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}
	
	
	
	public void append(String fileName, String data){
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName + ".csv", true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print(data);

			pw.println();

			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}
}
