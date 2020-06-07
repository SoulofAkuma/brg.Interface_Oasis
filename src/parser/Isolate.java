package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Isolate implements Rule {
	
	private String find; //Regex to isolate
	private ArrayList<String> log = new ArrayList<String>();
	
	public Isolate() {}

	public Isolate(String find) {
		this.find = find;
	}
	
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String find = ParserHandler.returnStringIfExists(constructorArgs, "find");
		if (find == null) {
			ParserHandler.reportGenRuleError("find", this.getClass().getName(), id);
			return null;
		} else {
			return new Isolate(find);
		}
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element.substring(0, 10) + "...\"");
			ArrayList<String> dummy = isolations(element);
			for (String dummyElement : dummy) {
				output.add(dummyElement);
			}
		}
		return output;
	}
	
	private ArrayList<String> isolations(String input) {
		Pattern pattern = Pattern.compile(this.find);
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
		return rule;
	}
}
