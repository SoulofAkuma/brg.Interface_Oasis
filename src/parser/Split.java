package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Split implements Rule {
	
	//Default values
	public static final boolean REGEX = false;
	public static final int N = 1;
	
	private String findValue; //String to find in the input
	private String find;
	private int n; //Splits only at every nth appearance
	private boolean regex;
	private boolean useHeader;
	private ArrayList<String> log = new ArrayList<String>();
	
	public Split() {}
	
	public Split(String find, int n, boolean regex, boolean useHeader) {
		this.find = find;
		this.n = n;
		this.regex = regex;
		this.useHeader = useHeader;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Integer n = ParserHandler.returnIntIfExists(constructorArgs, "n");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		Boolean useHeader = ParserHandler.returnBooleanIfExists(constructorArgs, "useHeader");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else if (n == null) {
			ParserHandler.reportGenRuleError("n", this.getClass().getName(), id);
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName(), id);
			return null;
		} else if (useHeader == null) {
			ParserHandler.reportGenRuleError("useHeader", this.getClass().getName(), id);
			return null;
		} else {
			return new Split(find, n, regex, useHeader);
		}
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader) {
		if (this.useHeader) {
			if (parsedHeader.containsKey(this.find)) {
				this.findValue = parsedHeader.get(this.find);
			} else {
				this.log.add("Value \"" + this.find +"\" not found in header");
				return input;
			}
		} else {
			this.findValue = this.find;
		}
		ArrayList<String> output = new ArrayList<String>();
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element.substring(0,  10) + "...\"");
			if (this.regex) {
				output.addAll(regexSplit(element));
			} else {
				output.addAll(split(element));
			}
		}
		return output;
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	private ArrayList<String> split(String input) {
		ArrayList<String> output = new ArrayList<String>();
		int previousEnd = 0;
		int appearanceCount = 0;
		int matchCount = 0;
		int start = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == this.findValue.charAt(matchCount)) {
				if (matchCount == 0) {
					start = i;					
				}
				matchCount++;
			}
			if (matchCount == findValue.length()) {
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
					previousEnd = start + this.findValue.length();
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
		Pattern pattern = Pattern.compile(this.findValue);
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

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.findValue);
		rule.put("n", String.valueOf(this.n));
		rule.put("regex", String.valueOf(this.regex));
		return rule;
	}

}
