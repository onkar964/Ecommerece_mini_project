package com.ecommerce;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class UserOperation {
//	Static Scanner sc = new Scanner(System.in);
	public static void userStart() {
		System.out.println("1. User Registration");
		System.out.println("2. User Login");
		System.out.println("3. Main menu");
		System.out.println("4. Exit");

		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your choice for user operations..");
			int ch = sc.nextInt();
			switch (ch) {
			case 1:
				UserOperation.userRegister();
				break;
			case 2:
				UserOperation.userLogin();
				break;
			case 3:
				EcomMain.startUp();
				break;
			case 4:
				System.exit(0);
			default:
				System.out.println("Enter write choice");
			}
		}
	}

	public static void userRegister() {
		Connection con = null;
		System.out.println("For register Enter user details...");

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the first name>>");
		String fname = sc.nextLine();
		System.out.println("Enter the last name>>");
		String lname = sc.nextLine();
		System.out.println("Enter the username>>");
		String username = sc.nextLine();
		System.out.println("Enter the password>>");
		String password = sc.nextLine();
		System.out.println("Enter the city>>");
		String city = sc.nextLine();
		System.out.println("Enter the mail id>>");
		String mailid = sc.nextLine();
		System.out.println("Enter the mobile number>>");
		String mobno = sc.nextLine();

		String insertUser = "INSERT INTO user (username, firstName, lastName, password, city, mailId, mobileNumber) VALUES('"
				+ username + "', '" + fname + "', '" + lname + "', '" + password + "', '" + city + "', '" + mailid
				+ "', '" + mobno + "' )";
		try {

			con = MysqlConnector.makeConnection();
			if (con != null) {

				try (Statement st = con.createStatement()) {
					st.execute(insertUser);
					System.out.println("User registered successfully..");

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

	public static void userLogin() {

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter user name:");
		String username = sc.nextLine();
		System.out.println("Enter password:");
		String password = sc.nextLine();

		String fetchLogin = "SELECT username, password FROM user WHERE username='" + username + "' AND password='"
				+ password + "'";

		Connection con = null;

		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet fl = st.executeQuery(fetchLogin);
					while (fl.next()) {
						String uname = fl.getString("username");
						String pwd = fl.getString("password");
						if ((uname.equals(username)) && (pwd.equals(password))) {
							UserOperation.userAfterLogin(username);
						} else {
							System.out.println("Enter correct username and password..");
							UserOperation.userStart();
						}
					}

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

	public static void userAfterLogin(String username) {

		System.out.println("Welcome user " + username + " Enter your choice");
		System.out.println("1. User view Product item as Sorted Order");
		System.out.println("2. Add to cart");
		System.out.println("3. View Cart");
		System.out.println("4. Purchase the item");
		System.out.println("5. Exit");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Welcome " + username + " Enter your choice..");
			int ch = sc.nextInt();
			switch (ch) {
			case 1:
				UserOperation.viewProduct();
				break;
			case 2:
				UserOperation.addToCart(username);
				break;
			case 3:
				UserOperation.viewCart(username);
				break;
			case 4:
				UserOperation.purchaseItem(username);
				break;
			case 5:
				System.exit(0);
			default:
				System.out.println("Enter write choice");
			}
		}
	}

	public static void viewProduct() {
		Connection con = null;
		String viewAllProduct = "SELECT id, name, price from product";
		try {

			con = MysqlConnector.makeConnection();
			if (con != null) {

				try (Statement st = con.createStatement()) {
					ResultSet rs = st.executeQuery(viewAllProduct);
					while (rs.next()) {
						String id = rs.getString("id");
						String name = rs.getString("name");
						double price = rs.getDouble("price");
						System.out.println(id + "\t" + name + "\t" + price);
					}
					rs.close();
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

	public static void addToCart(String username) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter product id ");
		String id = sc.nextLine();
		System.out.println("Enter quantity as integer value");
		int quantity = sc.nextInt();
		double price = 0;
		Connection con = null;
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					String getprice = "SELECT price FROM product WHERE id= '" + id + "'";

					ResultSet gp = st.executeQuery(getprice);
					while (gp.next()) {
						price = gp.getDouble("price");
					}
					gp.close();

					double cartprice = price * quantity;

					String addToCart = "INSERT INTO cart(username, productid,quantity,itemprice,cartprice) VALUES "
							+ "('" + username + "', " + id + ", " + quantity + ", " + price + ", " + cartprice + ");";

					st.execute(addToCart);
					System.out.println("Item Added to cart successfully...");
					UserOperation.userAfterLogin(username);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Database connection fail...");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlConnector.closeConnection(con);
		}

	}

	public static void viewCart(String username) {

		Connection con = null;
//		
		String viewcart = "SELECT p.name, p.price,c.quantity, c.cartprice FROM cart c LEFT JOIN product p on c.productid=p.id WHERE username='"
				+ username + "'";
		String cartprice = "SELECT SUM(c.cartprice) as Total FROM cart c WHERE username='" + username + "'";
		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet vc = st.executeQuery(viewcart);
					System.out.println("product_name   quantity  product_price Amount");
					while (vc.next()) {
						String productname = vc.getString("name");
						int quantity = vc.getInt("quantity");
						double price = vc.getDouble("price");
						double cartprce = vc.getDouble("cartprice");

						System.out.println(productname + "\t" + quantity + "\t" + price + "\t" + cartprce);
					}
					vc.close();
					System.out.print("\nTotal cart amount is ");
					ResultSet cp = st.executeQuery(cartprice);
					while (cp.next()) {
						double totalAmount = cp.getDouble("Total");
						System.out.println(totalAmount);
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

	public static void purchaseItem(String username) {

		Connection con = null;
		double totalAmount = 0;

		String cartTotal = "SELECT SUM(c.cartprice) as Total FROM cart c WHERE username='" + username + "'";

		try {
			con = MysqlConnector.makeConnection();
			if (con != null) {
				try (Statement st = con.createStatement()) {
					ResultSet ct = st.executeQuery(cartTotal);
					while (ct.next()) {
						totalAmount = ct.getDouble("Total");
					}
					String makeOrder = "INSERT INTO orders (username, total_amount) VALUES ('" + username + "'," + totalAmount+ ")";
					st.execute(makeOrder);
					System.out.println("We send your request to Admin for verification so goto main menu");
					UserOperation.userStart();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Database connection fail..");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlConnector.closeConnection(con);
		}


	}

}
