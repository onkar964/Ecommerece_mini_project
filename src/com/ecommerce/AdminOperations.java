package com.ecommerce;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;


public class AdminOperations {
	private static int count = 0;

	public static void adminStart() {
		if (AdminOperations.adminVerify()) {
			System.out.println("1. Add product item");
			System.out.println("2. Display bill amount to End User");
			System.out.println("3. Calculate Bill and process");
			System.out.println("4. Check Quantity");
			System.out.println("5. Check registered user");
			System.out.println("6. Check the particular user history");
			System.out.println("7. Go to main menu");
			System.out.println("8. Exit");
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.println("Enter your choice..");
				int ch = sc.nextInt();
				switch (ch) {
				case 1:
					AdminOperations.addProduct();
					break;
				case 2:
					AdminOperations.displayAmount();
					break;
				case 3:
					AdminOperations.calculateBill();
					break;
				case 4:
					AdminOperations.quantityCheck();
					break;
				case 5:
					AdminOperations.checkRegisterUser();
					break;
				case 6:
					AdminOperations.userHistory();
					break;
				case 7:
					EcomMain.startUp();
				case 8:
					System.exit(0);
				default:
					System.out.println("Enter correct choice..");
				}
			}

		} else {
			System.out.println("Enter Correct Password..");
			AdminOperations.adminStart();
		}

	}

	public static boolean adminVerify() {
		Scanner sc = new Scanner(System.in);

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream("src/db.properties")) {
			props.load(fis);
			if (count < 3) {
				count++;
				System.out.println("Enter your admin user name");
				String username = sc.nextLine();
				System.out.println("Enter admin password");
				String password = sc.nextLine();

				String pwd = props.getProperty("db.adminpassword");
				String uname = props.getProperty("db.username");
				if (pwd.equals(password) && uname.equals(username)) {
					return true;
				} else {
					System.out.println("Wrong password, you tried " + (count) + "times");
					AdminOperations.adminVerify();
				}

			} else {
				System.out.println("You entered password more than 3 times so exit from system");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void addProduct() {
		Connection con = null;
		Scanner sc = new Scanner(System.in);

		System.out.println("Product Id>> ");
		String id = sc.nextLine();
		System.out.println("Product Name>> ");
		String name = sc.nextLine();
		System.out.println("Product Description>>");
		String description = sc.nextLine();
		System.out.println("Quantity>> ");
		int quantity = sc.nextInt();
		System.out.println("Price>> ");
		double price = sc.nextDouble();

		String insertProduct = "INSERT INTO product (id,name, description, quantity, price) VALUES('" + id + "', '" + name
				+ "', '" + description + "', " + quantity + ", " + price + " )";
		try {

			con = MysqlConnector.makeConnection();
			if (con != null) {

				try (Statement st = con.createStatement()) {
					st.execute(insertProduct);
					System.out.println("Product Added successfully..");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlConnector.closeConnection(con);
		}

	}

	public static void displayAmount() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter username>> ");
		String username = sc.nextLine();

		Connection con = null;
		String cartprice = "SELECT SUM(c.cartprice) as Total FROM cart c WHERE username='" + username + "'";
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet cp = st.executeQuery(cartprice);
					while (cp.next()) {
						double totalAmount = cp.getDouble("Total");
						System.out.println("Your cart amount is " + totalAmount);
					}
					cp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Database connection fail..");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			MysqlConnector.closeConnection(con);
		}

	}

	public static void quantityCheck() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter productid>> ");
		String productid = sc.nextLine();

		Connection con = null;
		String productQ = "SELECT name, quantity FROM product WHERE id='" + productid + "'";
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet cp = st.executeQuery(productQ);
					while (cp.next()) {
						String pname = cp.getNString("name");
						int quantity = cp.getInt("quantity");
						System.out.println("Your " + pname + " product quantity is " + quantity);
					}
					cp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Database connection fail..");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			MysqlConnector.closeConnection(con);
		}
	}

	public static void calculateBill() {
	    Scanner sc = new Scanner(System.in);
	    System.out.println("Enter username>> ");
	    String username = sc.nextLine();

	    String fetchBill = "SELECT p.id, p.name, p.price, c.quantity, c.cartprice "
	            + "FROM cart c JOIN product p ON c.productid = p.id WHERE c.username = ?";
	    String getCartTotal = "SELECT SUM(cartprice) as Total FROM cart WHERE username = ?";
	    String updateStock = "UPDATE product SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
	    String insertHistory = "INSERT INTO user_histry (username, productname, quantity) VALUES (?, ?, ?)";
	    String deleteCart = "DELETE FROM cart WHERE username = ?";

	    try (Connection con = MysqlConnector.makeConnection()) {
	        if (con == null) {
	            System.out.println("Database connection failed.");
	            return;
	        }

	        Map<Integer, Integer> productIdToQuantity = new HashMap<>();
	        Map<Integer, String> productIdToName = new HashMap<>();

	        try (
	            PreparedStatement fetchStmt = con.prepareStatement(fetchBill);
	            PreparedStatement totalStmt = con.prepareStatement(getCartTotal);
	        ) {
	            fetchStmt.setString(1, username);
	            ResultSet rs = fetchStmt.executeQuery();
	            System.out.println("product_name   quantity  product_price   Amount");

	            while (rs.next()) {
	                int productId = rs.getInt("id");
	                String name = rs.getString("name");
	                int quantity = rs.getInt("quantity");
	                double price = rs.getDouble("price");
	                double cartPrice = rs.getDouble("cartprice");

	                System.out.println(name + "\t" + quantity + "\t" + price + "\t" + cartPrice);
	                productIdToQuantity.put(productId,productIdToQuantity.getOrDefault(productId, 0) + quantity);
	                productIdToName.put(productId, name);
	            }
	            rs.close();

	            totalStmt.setString(1, username);
	            ResultSet cp = totalStmt.executeQuery();
	            if (cp.next()) {
	                double totalAmount = cp.getDouble("Total");
	                System.out.println("\nTotal cart amount is " + totalAmount);
	            }
	            cp.close();
	        }

	        System.out.println("Enter 1 to process bill otherwise press enter");
	        int option = sc.nextInt();
	        sc.nextLine(); 

	        if (option == 1) {
	            try (PreparedStatement updateStockStmt = con.prepareStatement(updateStock)) {
	                for (Map.Entry<Integer, Integer> entry : productIdToQuantity.entrySet()) {
	                    int productId = entry.getKey();
	                    int qty = entry.getValue();

	                    updateStockStmt.setInt(1, qty);
	                    updateStockStmt.setInt(2, productId);
	                    updateStockStmt.setInt(3, qty);
	                    updateStockStmt.executeUpdate();
	                }
	                System.out.println("Product stock updated.");
	            }
	            try (PreparedStatement insertHistoryStmt = con.prepareStatement(insertHistory)) {
	                for (Map.Entry<Integer, Integer> entry : productIdToQuantity.entrySet()) {
	                    int productId = entry.getKey();
	                    int qty = entry.getValue();
	                    String productName = productIdToName.get(productId);

	                    insertHistoryStmt.setString(1, username);
	                    insertHistoryStmt.setString(2, productName);
	                    insertHistoryStmt.setInt(3, qty);
	                    insertHistoryStmt.executeUpdate();
	                }
	                System.out.println("User history updated.");
	            }
	            try (PreparedStatement deleteCartStmt = con.prepareStatement(deleteCart)) {
	                deleteCartStmt.setString(1, username);
	                deleteCartStmt.executeUpdate();
	                System.out.println("Cart cleared for user.");
	            }

	            System.out.println("Order processed successfully.");
	        } else {
	            System.out.println("Exit from bill processing...");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public static void checkRegisterUser() {
		System.out.println("lets see registered userlist with names..");
		Connection con = null;
		String userlist = "SELECT username,firstName,lastName, city, mailId,mobileNumber FROM user";
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet cp = st.executeQuery(userlist);
					System.out.println("username  firstName  lastName city  mailId        mobileNumber");
					while (cp.next()) {
						String username = cp.getNString("username");
						String firstName = cp.getNString("firstName");
						String lastName = cp.getNString("lastName");
						String city = cp.getNString("city");
						String mailId = cp.getNString("mailId");
						String mobileNumber = cp.getNString("mobileNumber");

						System.out.println(username + " " + firstName + " " + lastName + " " + city + " " + mailId + " "
								+ mobileNumber);
					}
					cp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Database connection fail..");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			MysqlConnector.closeConnection(con);
		}
	}

	public static void userHistory() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter username>> ");
		String username = sc.nextLine();
		Connection con = null;
		String user="SELECT productname,quantity From user_histry";
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet rs= st.executeQuery(user);
					System.out.println("Below os user histry of user "+username);
					while(rs.next()) {
						String productname=rs.getString("productname");
						int quantity=rs.getInt("quantity");
						System.out.println(productname+"\t"+quantity);
					}
				}
				} else {
					System.out.println("Database connection fail..");
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				MysqlConnector.closeConnection(con);
			}
	}

}
