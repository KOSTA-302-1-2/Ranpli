package app.db;

import app.config.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
  public static Connection getConnection() throws SQLException {
    String url = Config.get("db.url");
    String user = Config.get("db.user");
    String pass = Config.get("db.pass");
    return DriverManager.getConnection(url, user, pass);
  }
}
