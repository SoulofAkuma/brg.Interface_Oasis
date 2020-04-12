package parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class Discard extends Rule {
	
	public static final boolean INVERTED = false;
	public static final boolean REGEX = false;
	public static final char FLAGS[] = new char[0];
	
	private String find; //String to find in the input
	private boolean inverted; //If inverted the rule will discard all strings which match the condition
	private boolean regex; 
	private String flags[]; //Flags for the rule s: remove all subsequent elements, p: remove all preceding elements, s: shift discarded elements to the end of the result list instead of deleting them, u: shift all elements to the beginning of the result list instead of deleting them
	private ArrayList<String> log = new ArrayList<String>(); //Log for the rule
	public final RuleType ruleType = RuleType.Discard;
	
	public Discard(String find, boolean inverted, boolean regex, String flags[]) {
		this.find = find;
		this.inverted = inverted;
		this.regex = regex;
		this.flags = flags;
	}
	
	public String printRule() {
		return "Discard - find = \"" + this.find + "\" | inverted = " + String.valueOf(this.inverted) + " | regex = " + String.valueOf(this.regex) + " | flags = " + String.join(",", this.flags);
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<String> discarded = new ArrayList<String>();
		for (int i = 0; i < input.size(); i++) {
			this.log.add("Applying Rule on \"" + input.get(i) + "\"");
			boolean match = (regex) ? hasRegexMatch(input.get(i)) : input.get(i).contains(this.find);
			if (match) {
				for (int ii = 0; ii < this.flags.length; ii++) {
					switch (this.flags[ii]) {
						case "s":
							for (int iii = 0; iii < output.size(); iii++) {
								discarded.add(output.get(iii));
							}
							output = new ArrayList<String>();
					}
				}
			}
			if (!inverted && match) {
				output.add(input.get(i));
			} else if (inverted && match) {
				output.add(input.get(i));
			}
		}
		return output;
	}
	
	public boolean hasRegexMatch(String input) {
		Pattern pattern = Pattern.compile(this.find);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}
}
+