package com.example.decathlon.common;

public class CalcTrackAndField {

	private int result;

	// Calculates points for field discipline.
	public int calculateField(double A, double B, double C, double distance) {
		result = (int) (A * Math.pow((distance - B), C));
		return result;
	}

	// Calculates points for track discipline.
	public int calculateTrack(double A, double B, double C, double time) {
		result = (int) (A * Math.pow((B - time), C));
		return result;
	}

}
