package com.ecommerce;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MysqlConnector {
	
	public static Connection makeConnection() {
		Connection con = null;
		Properties prop = new Properties();
		try(FileInputStream fis = new FileInputStream("src/db.properties")){
			prop.load(fis);
			
			String url =prop.getProperty("db.url");
			String user = prop.getProperty("db.user");
			String password = prop.getProperty("db.password");
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			con = DriverManager.getConnection(url,user, password);
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		return con;
	}

	public static void closeConnection(Connection con) {
		try {
			if(con!=null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}
