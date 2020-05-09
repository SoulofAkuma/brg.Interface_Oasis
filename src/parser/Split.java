package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Split implements Rule {
	
	//Default values
	public static final boolean REGEX = false;
	public static final int N = 1;
	
	private String find; //String to find in the input
	private int n; //Splits only at every nth appearance
	private boolean regex;
	private ArrayList<String> log = new ArrayList<String>();
	
	public Split(String find, int n, boolean regex) {
		this.find = find;
		this.n = n;
		this.regex = regex;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Integer n = ParserHandler.returnIntIfExists(constructorArgs, "n");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName());
			return null;
		} else if (n == null) {
			ParserHandler.reportGenRuleError("n", this.getClass().getName());
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName());
			return null;
		} else {
			return new Split(find, n, regex);
		}
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element + "\"");
			if (this.regex) {
				output.addAll(regexSplit(element));
			} else {
				output.addAll(split(element));
			}
		}
		return output;
	}

	@Override
	public String printElement() {
		return "Split - find = \"" + this.find + "\" | n = " + this.n + " | regex = " + String.valueOf(this.regex); 
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	@Override
	public ArrayList<String> endProcedure(ArrayList<String> input) {
		return input;
	}
	
	private ArrayList<String> split(String input) {
		ArrayList<String> output = new ArrayList<String>();
		int previousEnd = 0;
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
			if (matchCount == find.length()) {
				appearanceCount++;
				matchCount = 0;
				this.log.add("Found match number " + appearanceCount + " at index " + start);
				if (appearanceCount == this.n) {
					if (previousEnd == start) {
						this.log.add("Did not add to output - not existing");
					} else {
						output.add(input.substring(previousEnd, start));
						this.log.add("Adding \"" + input.subSequence(previousEnd, start) + "\"");
					}
					previousEnd = start + this.find.length();
					appearanceCount = 0;
				}
			}
		}
		if (previousEnd >= input.length()) {
			this.log.add("Did not add leftover - not existing");
		} else {
			output.add(input.substring(previousEnd, input.length()));
			this.log.add("Adding leftover \"" + input.substring(previousEnd, input.length()) + "\"");
		}
		return output;
	}
	
	private ArrayList<String> regexSplit(String input) {
		ArrayList<String> output = new ArrayList<String>();
		Pattern pattern = Pattern.compile(this.find);
		Matcher matcher = pattern.matcher(input);
		int previousEnd = 0;
		int appearanceCount = 0;
		while (matcher.find()) {
			appearanceCount++;
			this.log.add("Found match number " + appearanceCount + " at index " + matcher.start());
			if (appearanceCount == this.n) {
				appearanceCount = 0;
				if (previousEnd == matcher.start()) {
					this.log.add("Did not add to output - split off string is empty or blank");
				} else {
					output.add(input.substring(previousEnd, matcher.start()));
				}
				previousEnd = matcher.end();
			}
		}
		if (previousEnd >= matcher.start()) {
			this.log.add("Did not add leftover - is empty or blank");
		} else {
			output.add(input.substring(previousEnd, input.length()));
			this.log.add("Adding leftover \"" + input.substring(previousEnd, input.length()) + "\"");
		}
		return output;
	}

}
