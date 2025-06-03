package com.ecommerce;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableData {

	public static void createTable(Connection con){
		String userTable="CREATE TABLE user (username VARCHAR(10) PRIMARY KEY, "
				+ "firstName VARCHAR(10), "
				+ "lastName VARCHAR(10), "
				+ "password VARCHAR(20), "
				+ "city VARCHAR(10), "
				+ "mailId VARCHAR(20) UNIQUE KEY, "
				+ "mobileNumber VARCHAR(10) UNIQUE KEY)";
		
		String productTable="CREATE TABLE product (id VARCHAR(10) PRIMARY KEY, "
				+ "description VARCHAR(40),  "
				+ "name VARCHAR(20),  "
				+ "price DECIMAL(10,2),  "
				+ "quantity INT)";
		
		String cartTable ="CREATE TABLE cart (cartid int PRIMARY KEY AUTO_INCREMENT, "
				+ "username VARCHAR(10), "
				+ "productid VARCHAR(10), "
				+ "quantity int, "
				+ "itemprice Decimal(10,2), "
				+ "cartprice Decimal(10,2), "
				+ "FOREIGN KEY (username) REFERENCES user(username), "
				+ "FOREIGN KEY (productid) REFERENCES product(id))";
		
		String accountTable = "CREATE TABLE account (id int PRIMARY KEY AUTO_INCREMENT, "
				+ "username varchar(10),"
				+ "billing_amount Decimal(10,3), "
				+ "FOREIGN KEY (username) REFERENCES user(username))";
		
		String orderTable="CREATE TABLE orders (id int PRIMARY KEY AUTO_INCREMENT, username VARCHAR(10), total_amount Decimal(10,2), "
				+ "FOREIGN KEY(username) REFERENCES user(username))";
		
		String orderHistry="CREATE TABLE user_histry(id int PRIMARY KEY AUTO_INCREMENT, "
				+ "username VARCHAR(10), "
				+ "productname VARCHAR(20), "
				+ "quantity int, "
				+ "FOREIGN KEY (username) REFERENCES user(username) )";
		
		try(Statement st = con.createStatement()){			
			st.execute(userTable);
			st.execute(productTable);
			st.execute(cartTable);
			st.execute(accountTable);
			st.execute(orderTable);
			st.execute(orderHistry);
			System.out.println("Tables created successfully.....");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void insertData(Connection con) {
		String productData = "INSERT INTO `product` (id, name, description, price, quantity) VALUES\n" 
				+"( 1 , 'Yoga Mat' , 'Non-slip exercise mat' , '29.99' , '150' ),\n"
				+"( 2 , 'Dumbbells Set' , 'Adjustable weight set' , '199.99' , '40' ),\n"
				+"( 3 , 'Treadmill' , 'Folding running machine' , '799.99' , '20' ),\n"
				+"( 4 , 'Jump Rope' , 'Speed training rope' , '19.99' , '200' ),\n"
				+"( 5 , 'Resistance Bands' , '5-piece band set' , '24.99' , '180' ),\n"
				+"( 6 , 'Exercise Bike' , 'Indoor cycling bike' , '399.99' , '25' ),\n"
				+"( 7 , 'Gym Bag' , 'Sports duffel bag' , '49.99' , '90' ),\n"
				+"( 8 , 'Water Bottle' , 'Insulated sports bottle' , '24.99' , '160' ),\n"
				+"( 9 , 'Fitness Tracker' , 'Activity tracking band' , '89.99' , '75' ),\n"
				+"( 10 , 'Workout Bench' , 'Adjustable weight bench' , '159.99' , '35' );";
		try(Statement st = con.createStatement()) {
			st.execute(productData);
			System.out.println("Data inserted successfully....");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadData() {
		Connection con =null;
		try {
			con= MysqlConnector.makeConnection();
			if(con!=null) {
				System.out.println("Connection to database successful..");
				createTable(con);
				insertData(con);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			MysqlConnector.closeConnection(con);
		}
	}

	public static void main(String[] args) {
		TableData.loadData();
	}

}
