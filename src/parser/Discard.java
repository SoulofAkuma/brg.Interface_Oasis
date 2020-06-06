package parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashMap;

public class Discard implements Rule {
	
	//Default values
	public static final boolean INVERTED = false;
	public static final boolean REGEX = false;
	public static final char FLAGS[] = new char[0];
	
	private String find; //String to find in the input
	private boolean inverted; //If inverted the rule will discard all strings which match the condition
	private boolean regex;
	private String flags[]; //Flags for the rule s: remove all subsequent elements, p: remove all preceding elements, z: push discarded elements to the end of the result list instead of deleting them, a: push discarded elements to the beginning of the result list instead of deleting them
	private ArrayList<String> log = new ArrayList<String>(); //Log for the rule
	
	public Discard(String find, boolean inverted, boolean regex, String flags[]) {
		this.find = find;
		this.inverted = inverted;
		this.regex = regex;
		this.flags = flags;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Boolean inverted = ParserHandler.returnBooleanIfExists(constructorArgs, "inverted");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		String[] flags = ParserHandler.returnStringArrayIfExists(constructorArgs, "flags");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName());
			return null;
		} else if (inverted == null) {
			ParserHandler.reportGenRuleError("inverted", this.getClass().getName());
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName());
			return null;
		} else if (flags == null) {
			ParserHandler.reportGenRuleError("flags", this.getClass().getName());
			return null;
		} else {
			return new Discard(find, inverted, regex, flags);
		}
	}
	
	
	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<String> discarded = new ArrayList<String>();
		boolean s = false;
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element + "\"");
			if (s) {
				this.log.add("Discarding element due to discard subsequent flag");
				discarded.add(element);
				continue;
			}
			boolean match = (regex) ? hasRegexMatch(element) : element.contains(this.find);
			if (match) {
				this.log.add("Match found");
				for (String flag : this.flags) {
					switch (flag) {
						case "s":
							this.log.add("Applying discard subsequent flag");
							s = true;
						break;
						case "p":
							this.log.add("Applying discard preceding flag");
							for (String outputElement : output) {
								this.log.add("Discarded " + outputElement);
								discarded.add(outputElement);
							}
							output = new ArrayList<String>();
						break;
					}
				}
			}
			if (!inverted && match) {
				this.log.add("Adding " + element + " to the result list");
				output.add(element);
			} else if (inverted && !match) {
				this.log.add("Adding " + element + " to the result list");
				output.add(element);
			}
		}
		if (Arrays.asList(this.flags).contains("z")) {
			for (String element : discarded) {
				output.add(element);
			}
		} else if(Arrays.asList(this.flags).contains("a")) {
			ArrayList<String> dummy = new ArrayList<String>();
			for (String element : discarded) {
				dummy.add(element);
			}
			for (String element : output) {
				dummy.add(element);
			}
			output = dummy;
		}
		return output;
	}
	
	@Override
	public ArrayList<String> printLog() {
		return this.log;
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

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.find);
		rule.put("inverted", String.valueOf(this.inverted));
		rule.put("regex", String.valueOf(this.regex));
		rule.put("flags", String.join(",", this.flags));
		return rule;
	}
}