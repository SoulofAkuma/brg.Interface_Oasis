package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Isolate implements Rule {
	
	private String findValue; //Regex to isolate
	private String find;
	private boolean useHeader;
	private List<String> log = Collections.synchronizedList(new ArrayList<String>());
	
	public Isolate() {}

	public Isolate(String find, boolean useHeader) {
		this.find = find;
		this.useHeader = useHeader;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		Boolean useHeader = ParserHandler.returnBooleanIfExists(constructorArgs, "useHeader");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else if (useHeader == null) {
			ParserHandler.reportGenRuleError("useHeader", this.getClass().getName(), id);
			return null;
		} else {
			return new Isolate(find, useHeader);
		}
	}

	@Override
	public List<String> printLog() {
		return this.log;
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
			ArrayList<String> dummy = isolations(element);
			for (String dummyElement : dummy) {
				output.add(dummyElement);
			}
		}
		return output;
	}
	
	private ArrayList<String> isolations(String input) {
		Pattern pattern = Pattern.compile(this.findValue);
		Matcher matcher = pattern.matcher(input);
		ArrayList<String> output = new ArrayList<String>();
		while (matcher.find()) {
			this.log.add("Match found from index " + matcher.start() + " to " + matcher.end());
			this.log.add("Adding \"" + matcher.group() + "\"");
			output.add(matcher.group());
		}
		return output;
	}

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		rule.put("find", this.find);
		rule.put("useHeader", String.valueOf(this.useHeader));
		return rule;
	}
	
	@Override
	public String printRuleLRP() {
		String useHeader = (this.useHeader) ? "useHeader; " : "";
		return "Isolate; " + useHeader + this.find;
	}

	public String getFind() {
		return find;
	}

	public void setFind(String find) {
		this.find = find;
	}

	public boolean isUseHeader() {
		return useHeader;
	}

	public void setUseHeader(boolean useHeader) {
		this.useHeader = useHeader;
	}
	
}
