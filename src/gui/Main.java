package gui;

public class Main {
	
	public static void main (String[] args) {
		String test = "hello my name is random";
		
		test = test.substring(0, 0) + test.substring(0, 5) + test.substring(5, test.length());
		
		System.out.println(test);
		
	}
	
}
