package parser;

import java.util.ArrayList;

public class Replace extends Rule {
	
	private String find;
	private String replace;
	
	public ArrayList<String> apply(ArrayList<String> input) {
		int index;
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < input.size(); i++) {
			index = 0;
			output.add(replace(input.get(i)));
		}
		return input;
	}
	
	private String replace(String input) {
		int matchCount = 0;
		int findCount = find.length();
		int start = 0;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == find.charAt(matchCount)) {
				start = i;
				matchCount++;
			}
			if (matchCount == find.length() - 1) {
				input = input.substring(0, start);
			}
		}
		return input;
	}
	
}
