package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StockCalcLongResultDaoClass implements SystemVariableInterface {

	public static final String DATABASE_NAME = "simple_calc_result";
	private Connection con;
	private ColumnClass[] columns;
	private Statement stmt;
	private String tableName;

	public StockCalcLongResultDaoClass(String tableName) {
		this.tableName = tableName;
		columns = new ColumnClass[2];
		columns[0] = new ColumnClass("symbol", "string");
		columns[1] = new ColumnClass(tableName, "int");

		// JDBCドライバーの指定
		try {
			Class.forName("org.sqlite.JDBC");

			// データベースに接続する なければ作成される
			con = DriverManager.getConnection("jdbc:sqlite:" + WORKSPACE
					+ DATABASE_NAME + ".db");

			stmt = con.createStatement();


			
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public List<StringLongClass> findAll(){
		List<StringLongClass> entityList = new ArrayList<StringLongClass>();
		
		try {
			ResultSet rs = stmt.executeQuery("select * from " + tableName);
			while (rs.next()) {

				entityList.add(toEntity(rs) );
				
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return entityList;
		
	}
	
	public void  insert(List<StringLongClass> entityList){
		try {
			

			ResultSet rs = stmt
					.executeQuery("select count(*) from sqlite_master where type='table' and name='"
							+ tableName + "';");

			// テーブル作成
			if (rs.getInt(1) == 0) {

				String sql = "create table " + tableName + "( ";

				sql = sql + columns[0].name + " " + columns[0].type;
				for (int i = 1; i < columns.length; i++) {
					sql = sql + ", " + columns[i].name + " " + columns[i].type;
				}
				sql = sql + ")";

				stmt.executeUpdate(sql);
				
				sql = "insert into " + tableName + " values (?,?)";
				con.setAutoCommit(false);
				PreparedStatement ps = con.prepareStatement(sql);
				for(int i=0; i < entityList.size(); i++){
					StringLongClass dayData = entityList.get(i);

					ps.setString(1, dayData.str);
					ps.setLong(2, dayData.num);

					ps.executeUpdate();
					
				}
				
				con.commit();
	            ps.close();
	            con.setAutoCommit(true);
			}
			
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private StringLongClass toEntity(ResultSet rs) throws SQLException{
		StringLongClass entity = new StringLongClass();

		entity.str = rs.getString("symbol");
		entity.num = rs.getLong(tableName);
		
		return entity;
	}

}
