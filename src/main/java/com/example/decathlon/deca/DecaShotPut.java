package com.example.decathlon.deca;

import com.example.decathlon.common.CalcTrackAndField;
import com.example.decathlon.common.InputResult;

public class DecaShotPut {

	private int score;
	private double A = 51.39;
	private double B = 1.5;
	private double C = 1.05;
	boolean active = true;
	CalcTrackAndField calc = new CalcTrackAndField();
	InputResult inputResult = new InputResult();

	// Calculate the score based on distance and height. Measured in meters.
	public int calculateResult(double distance) {

		while (active) {

			try {
				// Acceptable values.
				if (distance < 0) {
					System.out.println("Value too low");
					distance = inputResult.enterResult();
				} else if (distance > 30) {

					System.out.println("Value too high");
					distance = inputResult.enterResult();
				} else {

					score = calc.calculateField(A, B, C, distance);
					active = false;
				}
			} catch (Exception e) {

				System.out.println("Please enter numbers");
			}
		}
		System.out.println("The result is: " + score);
		return score;
	}

}
