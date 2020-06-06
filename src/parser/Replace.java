package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Replace implements Rule {
	
	//Default value
	public static final boolean REGEX = false;
	
	private String find; //String to find
	private String replace; //Replacement String
	private boolean regex; //Find is a regular expression
	private ArrayList<String> log = new ArrayList<String>(); //Log of rule steps
	
	public Replace(String find, String replace, boolean regex) {
		this.find = find;
		this.replace = replace;
		this.regex = regex;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		String replace = ParserHandler.returnStringIfExists(constructorArgs, "replace");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName());
			return null;
		} else if (replace == null) {
			ParserHandler.reportGenRuleError("replace", this.getClass().getName());
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName());
			return null;
		} else {
			return new Replace(find, replace, regex);
		}
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element + "\"");
			if (regex) {
				output.add(replaceRegex(element));
			} else {
				output.add(replace(element));				
			}
			this.log.add("Application resulted in \"" + output.get(output.size() - 1) + "\"");
		}
		return output;
	}
	
	@Override
	public ArrayList<String> printLog() {
		return this.log;
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
			if (matchCount == find.length()) {
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

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.find);
		rule.put("replace", this.replace);
		rule.put("regex", String.valueOf(this.regex));
		return rule;
	}
	
}
