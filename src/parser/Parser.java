package parser;

import java.util.ArrayList;
import java.util.Arrays;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser {
	
	private Rule rule;
	private ArrayList<Rule> rules = new ArrayList<Rule>();
	private ArrayList<String> log = new ArrayList<String>();
	
	public Parser(Rule rule) {
		this.rule = rule;
	}
	
	public Parser(ArrayList<Rule> rules) {
		for (Rule rule: rules) {
			this.rules.add(rule);
		}
	}
	
	public Parser(Rule ruleArray[]) throws Exception {
		ArrayList<Rule> rules = new ArrayList<Rule>(Arrays.asList(ruleArray));
		for (Rule rule : rules) {
			this.rules.add(rule);
		}
	}
	
	//Apply all rules to the string
	public ArrayList<String> parse(String input) {
		this.log.add("Starting parsing");
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (Rule rule : this.rules) {
			this.log.add("Applying " + rule.printRule());
			strList = rule.apply(strList);
			this.log.add("--- Start rule log ---");			
			this.log.addAll(rule.printLog());
			this.log.add("--- End rule log ---");						
		}
		
		
		return strList;
	}
}
