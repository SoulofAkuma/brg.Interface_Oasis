package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Cut implements Rule {
	
	//Default values
	public static final int N = 1;
	public static final boolean KEEP = false;
	public static final boolean REGEX = false;
	public static final boolean REEVAL = false;
	
	
	private String findValue; //String to find
	private String find;
	private int n; //The String is cut at the nth appearance
	private boolean keep; //Keeps the appearance of find
	private boolean regex; //Find is a regular expression
	private boolean reEval; //If true the cut of string will be added to the input list
	private boolean useHeader; //Get find Value from the header 
	private List<String> log = Collections.synchronizedList(new ArrayList<String>()); //Log of rule steps
	
	public Cut() {}
	
	public Cut(String find, int n, boolean keep, boolean regex, boolean reEval, boolean useHeader) {
		this.find = find;
		this.n = (n < 1) ? 1 : n;
		this.keep = keep;
		this.regex = regex;
		this.reEval = reEval;
		this.useHeader = useHeader;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Integer n = ParserHandler.returnIntIfExists(constructorArgs, "n");
		Boolean keep = ParserHandler.returnBooleanIfExists(constructorArgs, "keep");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		Boolean reEval = ParserHandler.returnBooleanIfExists(constructorArgs, "reEval");
		Boolean useHeader = ParserHandler.returnBooleanIfExists(constructorArgs, "useHeader");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else if (n == null) {
			ParserHandler.reportGenRuleError("n", this.getClass().getName(), id);
			return null;
		} else if (keep == null ) {
			ParserHandler.reportGenRuleError("keep", this.getClass().getName(), id);
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName(), id);
			return null;
		} else if (reEval == null) {
			ParserHandler.reportGenRuleError("reEval", this.getClass().getName(), id);
			return null;
		} else if (useHeader == null) {
			ParserHandler.reportGenRuleError("useHeader", this.getClass().getName(), id);
			return null;
		} else {
			return new Cut(find, n, keep, regex, reEval, useHeader);
		}
	}
	
	
	@Override
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader) {
		this.log.clear();
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
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i) == null || input.get(i).isEmpty()) {
				this.log.add("Element Empty");
				continue;
			}
			String element = input.get(i);
			this.log.add("Applying Rule on \"" + element.substring(0, 10) + "...\"");
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
	public List<String> printLog() {
		return this.log;
	}
	
	//Cut the String at the nth appearance of a plain String
	private String[] cut(String input) {
		int appearanceCount = 0;
		int matchCount = 0;
		int start = 0;
		String extra = null;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == this.findValue.charAt(matchCount)) {
				if (matchCount == 0) {
					start = i;					
				}
				matchCount++;
			}
			if (matchCount == findValue.length()) {
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
		
		Pattern pattern = Pattern.compile(this.findValue);
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

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.find);
		rule.put("n", String.valueOf(this.n));
		rule.put("keep", String.valueOf(this.keep));
		rule.put("regex", String.valueOf(this.regex));
		rule.put("reEval", String.valueOf(this.reEval));
		rule.put("useHeader", String.valueOf(this.useHeader));
		return rule;
	}
}
