package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.config.Config;

public class Db {
  public static Connection getConnection() throws SQLException {
	Config.load();
	
    String url = Config.get("db.url");
    String user = Config.get("db.user");
    String pass = Config.get("db.pass");
    return DriverManager.getConnection(url, user, pass);
  }
  
  private static void releaseConnection(Connection con) {
	  try {
		  if (con != null) {
			  con.close();
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
  private static void releaseStatement(Statement stm) {
	  try {
		  if (stm != null) {
			  stm.close();
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
  private static void releaseResultSet(ResultSet rs) {
	  try {
		  if (rs != null) {
			  rs.close();
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
  public static void releaseConnection (Connection con, Statement stm) {
	  releaseConnection(con);
	  releaseStatement(stm);
  }
  
  public static void releaseConnection (Connection con, Statement stm, ResultSet rs) {
	  releaseConnection(con, stm);
	  releaseResultSet(rs);
  }
}
