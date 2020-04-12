package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Replace extends Rule {
	
	public static final boolean REGEX = false;
	
	private String find; //String to find
	private String replace; //Replacement String
	private boolean regex; //Find is a regular expression
	private ArrayList<String> log = new ArrayList<String>(); //Log of rule steps
	public final RuleType ruleType = RuleType.Replace;
	
	public Replace(String find, String replace, boolean regex) {
		this.find = find;
		this.replace = replace;
		this.regex = regex;
	}
	
	public String printRule() {
		return "Replace - find = \"" + this.find + "\" | replace = \"" + this.replace + "\" | regex = " + String.valueOf(this.regex);
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < input.size(); i++) {
			this.log.add("Applying Rule on \"" + input.get(i) + "\"");
			if (regex) {
				output.add(replaceRegex(input.get(i)));
			} else {
				output.add(replace(input.get(i)));				
			}
		}
		return output;
	}
	
	//Replace all appearances of a plain String with a plain string
	private String replace(String input) {
		int matchCount = 0;
		int start = 0;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == this.find.charAt(matchCount)) {
				if (matchCount == 0) {
					start = i;					
				}
				matchCount++;
			}
			if (matchCount == find.length() - 1) {
				this.log.add("Replaced from index " + start + " to " + (start + matchCount));
				input = input.substring(0, start) + this.replace + input.substring(start + matchCount, input.length());
				matchCount = 0;
			}
		}
		return input;
	}
	
	//Replace all appearances of a regex (custom function for logging purposes) with a replacement String
	private String replaceRegex(String input) {
		Pattern pattern = Pattern.compile(this.find);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			this.log.add("Replaced from index " + matcher.start() + " to " + matcher.end());
			input = matcher.replaceFirst(this.replace);
		}
		return input;
	}
	
}
