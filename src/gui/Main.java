package gui;

public class Main {
	
	public static void main (String[] args) {
		String test = "hello my name is hello randhelloom hello";
		
		System.out.println(replace(test));
		
	}
	
	private static String replace(String input) {
		int matchCount = 0;
		String find = "hello";
		String replace = "hullo";
		int findCount = find.length();
		int start = 0;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == find.charAt(matchCount) && matchCount == 0) {
				start = i;
				matchCount++;
			} else if (input.charAt(i) == find.charAt(matchCount)) {
				matchCount++;
			}
			if (matchCount == find.length() - 1) {
				input = input.substring(0, start) + replace + input.substring(start + 1 + matchCount, input.length());
				matchCount = 0;
			}
		}
		return input;
	}
	
}
