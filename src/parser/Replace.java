package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Replace implements Rule {
	
	//Default value
	public static final boolean REGEX = false;
	
	private String findValue; //String to find
	private String find;
	private String replace; //Replacement String
	private boolean regex; //Find is a regular expression
	private boolean useHeader; //Get find Value from the Header
	private List<String> log = Collections.synchronizedList(new ArrayList<String>()); //Log of rule steps
	
	public Replace() {}
	
	public Replace(String find, String replace, boolean regex, boolean useHeader) {
		this.find = find;
		this.replace = replace;
		this.regex = regex;
		this.useHeader = useHeader;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		String replace = ParserHandler.returnStringIfExists(constructorArgs, "replace");
		Boolean regex = ParserHandler.returnBooleanIfExists(constructorArgs, "regex");
		Boolean useHeader = ParserHandler.returnBooleanIfExists(constructorArgs, "useHeader");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else if (replace == null) {
			ParserHandler.reportGenRuleError("replace", this.getClass().getName(), id);
			return null;
		} else if (regex == null) {
			ParserHandler.reportGenRuleError("regex", this.getClass().getName(), id);
			return null;
		} else if (useHeader == null) {
			ParserHandler.reportGenRuleError("useHeader", this.getClass().getName(), id);
			return null;
		} else {
			return new Replace(find, replace, regex, useHeader);
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
		for (String element : input) {
			if (element == null || element.isEmpty()) {
				this.log.add("Element Empty");
				continue;
			}
			this.log.add("Applying Rule on \"" + element.substring(0, 10) + "...\"");
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
	public List<String> printLog() {
		return this.log;
	}
	
	//Replace all appearances of a plain String with a plain string
	private String replace(String input) {
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
				this.log.add("Replaced from index " + start + " to " + (start + matchCount));
				input = input.substring(0, start) + this.replace + input.substring(start + matchCount, input.length());
				matchCount = 0;
			}
		}
		return input;
	}
	
	//Replace all appearances of a regex (custom function for logging purposes) with a replacement String
	private String replaceRegex(String input) {
		Pattern pattern = Pattern.compile(this.findValue);
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
		rule.put("useHeader", String.valueOf(this.useHeader));
		return rule;
	}
	
	@Override
	public String printRuleLRP() {
		String useHeader = (this.useHeader) ? "useHeader; " : "";
		String regex = (this.regex) ? "regex; " : "";
		return "Replace; " + useHeader + regex + this.find + "; " + this.replace;
	}

	public String getFind() {
		return find;
	}

	public void setFind(String find) {
		this.find = find;
	}

	public String getReplace() {
		return replace;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}

	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	public boolean isUseHeader() {
		return useHeader;
	}

	public void setUseHeader(boolean useHeader) {
		this.useHeader = useHeader;
	}
	
}
