package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Cut implements ParserInterface {
	
	//Default values
	public static final int N = 1;
	public static final boolean KEEP = false;
	public static final boolean REGEX = false;
	public static final boolean REEVAL = false;
	
	
	private String find; //String to find
	private int n; //The String is cut at the nth appearance
	private boolean keep; //Keeps the appearance of find
	private boolean regex; //Find is a regular expression
	private boolean reEval; //If true the cut of string will be added to the input list
	private ArrayList<String> log = new ArrayList<String>(); //Log of rule steps
	
	public Cut(String find, int n, boolean keep, boolean regex, boolean reEval) {
		this.find = find;
		this.n = (n < 1) ? 1 : n;
		this.keep = keep;
		this.regex = regex;
		this.reEval = reEval;
	}
	
	
	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < input.size(); i++) {
			String element = input.get(i);
			this.log.add("Applying Rule on \"" + element + "\"");
			String[] toAdd = (regex) ? regexCut(element) : cut(element);
			output.add(toAdd[0]);
			this.log.add("Application resulted in \"" + output.get(output.size() - 1) + "\"");
			if (this.reEval) {
				if (toAdd[1] == null) {
					this.log.add("No leftover to add");
				} else {
					this.log.add("Adding leftover \"" + toAdd[1] + "\"");
					input.add(toAdd[1]);
				}
			}
		}
		return output;
	}
	
	@Override
	public String printElement() {
		return "Cut - find = \"" + this.find + "\" | n = " + this.n + " | keep = " + String.valueOf(this.keep) + " | regex = " + String.valueOf(this.regex); 
	}
	
	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	@Override
	public ArrayList<String> endProcedure(ArrayList<String> input) {
		return input;
	}
	
	//Cut the String at the nth appearance of a plain String
	private String[] cut(String input) {
		int appearanceCount = 0;
		int matchCount = 0;
		int start = 0;
		String extra = null;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == this.find.charAt(matchCount)) {
				if (matchCount == 0) {
					start = i;					
				}
				matchCount++;
			}
			if (matchCount == find.length()) {
				appearanceCount++;
				this.log.add("Match number " + appearanceCount + " found at " + start);
				matchCount = 0;
				if (appearanceCount == this.n) {
					if (this.keep) {
						if (i != input.length()) {
							extra = input.substring(i, input.length());							
						}
						input = input.substring(0, i + 1);
						this.log.add("String cut at index " + i);
					} else {
						if (i != input.length()) {
							extra = input.substring(start, input.length());
						}
						input = input.substring(0, start);
						this.log.add("String cut at index " + start);
					}
					break;
				}
			}
		}
		return new String[] {input, extra};
	}
	
	//Cut the String at the nth regex match
	private String[] regexCut(String input) {
		
		Pattern pattern = Pattern.compile(this.find);
		Matcher matcher = pattern.matcher(input);
		int appearanceCount = 0;
		String extra = null;
		
		while (matcher.find()) {
			appearanceCount++;
			this.log.add("Match number " + appearanceCount + " found at " + matcher.start());
			if (appearanceCount == this.n) {
				if (this.keep) {
					extra = input.substring(matcher.start(), input.length());
					input = input.substring(0, matcher.start());
					this.log.add("String cut at index " + matcher.start());
				} else {
					extra = input.substring(matcher.end(), input.length());
					input = input.substring(0, matcher.end());
					this.log.add("String cut at index " + matcher.end());
				}
				break;
			}
		}
		return new String[] {input, extra};
	}
}
