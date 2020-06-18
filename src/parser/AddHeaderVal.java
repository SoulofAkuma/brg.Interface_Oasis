package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AddHeaderVal implements Rule {
	
	private String find;
	private List<String> log = Collections.synchronizedList(new ArrayList<String>());
	
	private AddHeaderVal(String find) {
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
			return new AddHeaderVal(find);
		}
	}

	@Override
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		if (parsedHeader.containsKey(this.find)) {
			input.add(parsedHeader.get(this.find));
			this.log.add("Added \"" + parsedHeader.get(this.find) + "\" to the list");
		} else {
			this.log.add("Could not find the key \"" + this.find + "\" in header");
		}
		return input;
	}

	@Override
	public List<String> printLog() {
		return this.log;
	}

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("find", this.find);
		return attributes;
	}
	
	@Override
	public String printRuleLRP() {
		return "AddHeaderVal; " + this.find;
	}
	
}
