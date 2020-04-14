package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Isolate extends Rule {
	
	private String find; //Regex to isolate
	private ArrayList<String> log = new ArrayList<String>();
	private RuleType ruleTypeValue = RuleType.Isolate;

	public Isolate(String find) {
		this.find = find;
	}

	@Override
	public String printElement() {
		return "Isolate - find = " + this.find;
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	@Override
	public RuleType ruleType() {
		return this.ruleTypeValue;
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (String element : input) {
			this.log.add("Applying Rule on \"" + element + "\"");
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
}
