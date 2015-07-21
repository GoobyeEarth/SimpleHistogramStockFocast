package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteRowStockDataDaoClass implements SystemVariableInterface{
	private Statement stmt;
	private ResultSet rs;
	private Connection con;

	private static final String DATABASE_NAME = "stockrowdata";

	private static final String TABLE_NAME = "daily";

	private static final ColumnClass[] COLUMNS = {
			new ColumnClass("date", "integer"),
			new ColumnClass("symbol", "text"),
			new ColumnClass("opening", "real"),
			new ColumnClass("high", "real"),
			new ColumnClass("low", "real"),
			new ColumnClass("closing", "real"),
			new ColumnClass("turnover", "integer"),
			};

	public SQLiteRowStockDataDaoClass() {

		// JDBCドライバーの指定
		try {
			Class.forName("org.sqlite.JDBC");

			// データベースに接続する なければ作成される
			con = DriverManager.getConnection("jdbc:sqlite:" + WORKSPACE
					+ DATABASE_NAME + ".db");

			stmt = con.createStatement();

			rs = stmt.executeQuery("select count(*) from sqlite_master where type='table' and name='"
							+ TABLE_NAME + "';");

			// テーブル作成
			if (rs.getInt(1) == 0) {

				String sql = "create table " + TABLE_NAME + "( ";

				sql = sql + COLUMNS[0].name + " " + COLUMNS[0].type;
				for (int i = 1; i < COLUMNS.length; i++) {
					sql = sql + ", " + COLUMNS[i].name + " " + COLUMNS[i].type;
				}
				sql = sql + ")";

				stmt.executeUpdate(sql);

			}
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public boolean insert(StockCandleClass entity) {
		try {

			String sql = "insert into " + TABLE_NAME + " values ( "
					+ entity.date
					+ ", " + "'" + entity.symbol + "'"
					+ ", " + entity.opening
					+ ", " + entity.high
					+ ", " + entity.low
					+ ", " + entity.closing
					+ ", " + entity.turnover
					 + " )";

			return stmt.execute(sql);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}

	}

	public void insert(List<StockCandleClass> entityList){
		try {
			String sql = "insert into " + TABLE_NAME + " values (?,?,?,?,?,?,?)";
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(sql);
			for(int i=0; i < entityList.size(); i++){
				StockCandleClass dayData = entityList.get(i);

				ps.setInt(1, dayData.date);
				ps.setString(2, dayData.symbol);
				ps.setFloat(3, dayData.opening);
				ps.setFloat(4, dayData.high);
				ps.setFloat(5, dayData.low);
				ps.setFloat(6, dayData.closing);
				ps.setInt(7, dayData.turnover);

				ps.executeUpdate();

			}

			con.commit();

            ps.close();
            con.setAutoCommit(true);



		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


	}


	public List<StockCandleClass> findAll() {
		List<StockCandleClass> entityList = new ArrayList<StockCandleClass>();
		 PreparedStatement ps;
		// 結果を表示する
		try {
			rs = stmt.executeQuery("select * from " + TABLE_NAME);
			while (rs.next()) {

				entityList.add(toEntity(rs) );

			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック

			System.out.println("error");
			e.printStackTrace();
		}

		return entityList;
	}

	private StockCandleClass toEntity(ResultSet rs) throws SQLException {

		StockCandleClass entity = new StockCandleClass();

		entity.date = rs.getInt(COLUMNS[0].name);
		entity.symbol = rs.getString(COLUMNS[1].name);
		entity.opening = rs.getFloat(COLUMNS[2].name);
		entity.high = rs.getFloat(COLUMNS[3].name);
		entity.low = rs.getFloat(COLUMNS[4].name);
		entity.closing = rs.getFloat(COLUMNS[5].name);
		entity.turnover = rs.getInt(COLUMNS[6].name);



		return entity;

	}

	/**
	 * 編集をしなおしてください
	 * @param selection
	 * @return
	 */
	public List<StockCandleClass> findBySymbol(String symbol) {
		List<StockCandleClass> entityList = new ArrayList<StockCandleClass>();

		// 結果を表示する
		try {

			rs.setFetchSize(10000);

			rs = stmt.executeQuery("select * from " + TABLE_NAME
					+ " where symbol='" + symbol + "'"
					+ " order by date asc");

			while (rs.next()) {
				entityList.add(toEntity(rs) );

			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return entityList;
	}



	public List<String> getSymbolList(){
		List<String> symbolList = new ArrayList<String>();
		try{
			rs = stmt.executeQuery("select distinct symbol from " + TABLE_NAME);
			while(rs.next()){
				symbolList.add(rs.getString("symbol") );
			}
		}catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return symbolList;
	}

	public List<Integer> getDateList(){
		List<Integer> symbolList = new ArrayList<Integer>();
		try{
			rs = stmt.executeQuery("select distinct date from " + TABLE_NAME
					+ " order by date asc");
			while(rs.next() ){
				symbolList.add(rs.getInt("date") );
			}
		}catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return symbolList;

	}

}
