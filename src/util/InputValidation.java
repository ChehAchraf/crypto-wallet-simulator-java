package util;

import java.util.Scanner;

public class InputValidation {
	private static final Scanner SCANNER = new Scanner(System.in);
	
	public static boolean isDouble(String s ) {
		try {
			Double.parseDouble(s);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
}
