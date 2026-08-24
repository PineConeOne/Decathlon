package com.example.decathlon.common;

import java.util.Scanner;

public class InputName {
	String compName = "";

	
	//Add competitor
	public String addCompetitor() {

		boolean active = true;
		String compName = "";
		Scanner sc = new Scanner(System.in);
		
		while (active) {
			System.out.println("Please enter the competitor's name:");
			compName = sc.nextLine();
			if (!compName.matches(".*[a-zA-Z]")) {
				System.out.println("Only use letters when putting in competitors name.");

			} else {
				active = false;
			}

		} 
		return compName;
	} 
}
