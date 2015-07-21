package library;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

public class LogCsvClass implements SystemVariableInterface{

	private String directory;
	private String fileName;

	public LogCsvClass(String name){

		this.directory = WORKSPACE + "log\\";
		Calendar calendar = Calendar.getInstance();

	    int day = calendar.get(Calendar.DATE);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int minute = calendar.get(Calendar.MINUTE);
	    int second = calendar.get(Calendar.SECOND);

	    fileName = day + "day_" + hour + "hour_" + minute + "min_" + second + "second"+ "_" + name + ".csv";
	}

	public void set(String log){
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName, true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print(log);

			pw.println();

			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}

	}

	public void set(List<String> logs){
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName, true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			String text = "";
			text = logs.get(0);
			for(int i=1; i < logs.size(); i++){
				text = text + "," + logs.get(i);
			}
			pw.print(text);

			pw.println();

			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}

	public void setAll(List<String> logs){
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName, true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			for (String log : logs) {
				pw.print(log);
				pw.println();

			}

			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}

	public void set(String[] logs){
		try {
			// 出力先を作成する
			FileWriter fw = new FileWriter(directory + fileName, true); // ※１
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			String text = "";
			text = logs[0] ;
			for(int i=1; i < logs.length; i++){
				text = text + "," + logs[i];
			}
			pw.print(text);

			pw.println();

			pw.close();

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();
		}
	}

}
