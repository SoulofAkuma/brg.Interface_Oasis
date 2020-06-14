package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import indexassigner.IndexAssigner;
import indexassigner.IndexAssignerHandler;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class CustomParser implements Parser {

	//TODO: Debugging without index assigner
	private String name;
	private ArrayList<String> order;
	private HashMap<String, Rule> elements;
	private ArrayList<String> log = new ArrayList<String>();
	private ArrayList<String> indexAssigners;
	
	public CustomParser(HashMap<String, Rule> elements, ArrayList<String> indexAssigners, ArrayList<String> order, String name) {
		this.indexAssigners = indexAssigners;
		this.elements = elements;
		this.order = order;
	}
	
	//Apply all rules to the string
	@Override
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		this.log.add("Starting parsing");
		
		if (parsedHeader == null) {
			this.log.add("No header found");
			parsedHeader = new HashMap<String, String>();
		}
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (String rule : this.order) {
			Rule element = elements.get(rule);
			this.log.add("Applying " + ((Rule) element).getClass().getName() + " " + rule);
			strList = (((Rule) element).apply(strList, parsedHeader));
			this.log.add("--- Start log ---");
			this.log.addAll((((Rule) element).printLog()));
			this.log.add("--- End log ---");
		}
		HashMap<String, String> res = new HashMap<String, String>();
		for (String indexAssigner : this.indexAssigners) {
			res.putAll(IndexAssignerHandler.assign(indexAssigner, strList));
		}
		return res;
	}
	
	public HashMap<String, String> storeParser() {
		HashMap<String, String> parser = new HashMap<String, String>();
		parser.put("order", String.join(",", this.order.toArray(new String[this.order.size()])));
		parser.put("name", this.name);
		return parser;
	}
	
	public ArrayList<HashMap<String, String>> storeRules() {
		ArrayList<HashMap<String, String>> rules = new ArrayList<HashMap<String, String>>();
		for (String ruleID : this.order) {
			HashMap<String, String> rule = this.elements.get(ruleID).storeRule();
			rule.put("id", ruleID);
			rules.add(rule);
		}
		return rules;
	}
	
	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	
}
