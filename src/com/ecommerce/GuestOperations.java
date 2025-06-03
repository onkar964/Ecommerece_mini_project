package com.ecommerce;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class GuestOperations {
	
	public static void guestStart() {
		System.out.println("1. View product item");
		System.out.println("2. Not purchase item");
		System.out.println("3. Entering main menu");
		System.out.println("4. Exit");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your choice..");
			int ch = sc.nextInt();
			switch(ch){
				case 1:
					GuestOperations.viewProductItemAdmin();
					System.out.println("If you want to buy something please login or register for that press 1 and \npress 0 for exit");
					int opt=sc.nextInt();
					if(opt==1) {
						UserOperation.userStart();
					}else {
						System.exit(0);
					}
					break;
				case 2:
					GuestOperations.notPurchaseItem();
					break;
				case 3:
					EcomMain.startUp();
					break;
				case 4:
					System.exit(0);
				default:
					System.out.println("Enter correct choice..");
			}
		}
	}
	
	public static void viewProductItemAdmin() {
		Connection con = null;
		String viewAllProduct= "SELECT name, price from product";
		try {
			
			con= MysqlConnector.makeConnection();
			if(con!=null) {
				
				try(Statement st =con.createStatement()) {
					ResultSet rs =  st.executeQuery(viewAllProduct);
					while (rs.next()) {
						String name = rs.getString("name");
						double price = rs.getDouble("price");
						System.out.println(name +"\t"+ price);
					}
					rs.close();
				}catch (Exception e) {
					e.printStackTrace();
				} finally {
					MysqlConnector.closeConnection(con);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			MysqlConnector.closeConnection(con);
		}	
		
	}
	
	public static void notPurchaseItem() {
		System.out.println("You need to login or register...");
		System.out.println("1. User Operation");
		System.out.println("2. Main menu");
		System.out.println("3. Exit");

		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your choice..");
			int ch = sc.nextInt();
			switch(ch){
				case 1:
					UserOperation.userStart();
					break;
				case 2:
					EcomMain.startUp();
					break;
				case 3:
					System.exit(0);
				default:
					System.out.println("Enter correct choice..");
			}
		}
		
	}

}
