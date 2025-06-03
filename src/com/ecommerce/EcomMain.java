package com.ecommerce;

import java.util.Scanner;

public class EcomMain {
	
	public static void startUp() {
		System.out.println("Welcome to E-Commerce based application");
		System.out.println("1. User Operation");
		System.out.println("2. Admin Operation");
		System.out.println("3. Guest Operation");
		System.out.println("4. Exit");
		
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your choice..");
			int ch = sc.nextInt();
			switch(ch){
				case 1:
					UserOperation.userStart();
					break;
				case 2:
					AdminOperations.adminStart();
					break;
				case 3:
					GuestOperations.guestStart();
				case 4:
					System.exit(0);
				default:
					System.out.println("Enter write choice");
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EcomMain.startUp();

	}

}
