package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Cut extends Rule {
	
	public static final int N = 1;
	public static final boolean KEEP = false;
	public static final boolean REGEX = false;
	
	
	private String find; //String to find
	private int n; //The String is cut at the nth appearance
	private boolean keep; //Keeps the appearance of find
	private boolean regex; //Find is a regular expression
	private ArrayList<String> log = new ArrayList<String>(); //Log of rule steps
	public final RuleType ruleType = RuleType.Cut;
	
	public Cut(String find, int n, boolean keep, boolean regex) throws Exception {
		this.find = find;
		if (n < 1) {
			throw new Exception("N must be at least 1");
		}
		this.n = n;
		this.keep = keep;
		this.regex = regex;
	}
	
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < input.size(); i++) {
			this.log.add("Applying Rule on \"" + input.get(i) + "\"");
			if (regex) {
				output.add(regexCut(input.get(i)));			
			} else {
				output.add(cut(input.get(i)));
			}
		}
		return output;
	}
	
	public String printRule() {
		return "Cut - find = \"" + this.find + "\" | n = " + this.n + " | keep = " + String.valueOf(this.keep) + " | regex = " + String.valueOf(this.regex); 
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	//Cut the String at the nth appearance of a plain String
	private String cut(String input) {
		int appearanceCount = 0;
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
				appearanceCount++;
				this.log.add("Match number " + appearanceCount + " found at " + start);
				if (appearanceCount == this.n) {
					if (this.keep) {
						input = input.substring(0, i);
						this.log.add("String cut at index" + i);
					} else {
						input = input.substring(0, start);
						this.log.add("String cut at index" + start);
					}
					break;
				}
			}
		}
		return input;
	}
	
	//Cut the String at the nth regex match
	private String regexCut(String input) {
		
		Pattern pattern = Pattern.compile(this.find);
		Matcher matcher = pattern.matcher(input);
		int appearanceCount = 0;
		while (matcher.find()) {
			appearanceCount++;
			this.log.add("Match number " + appearanceCount + " found at " + matcher.start());
			if (appearanceCount == this.n) {
				if (this.keep) {
					input = input.substring(0, matcher.start());
					this.log.add("String cut at index" + matcher.start());
				} else {
					input = input.substring(0, matcher.end());
					this.log.add("String cut at index" + matcher.end());
				}
				break;
			}
		}
		return input;
	}
}
