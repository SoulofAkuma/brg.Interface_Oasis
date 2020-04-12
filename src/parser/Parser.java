package parser;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.Arrays;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser {
	
	private Pattern regex;
	private Rule rule;
	private ParserType type;
	private ArrayList<Rule> rules = new ArrayList<Rule>();
	private ArrayList<String> log = new ArrayList<String>();
	
	
	public Parser(Pattern regex) {
		this.regex = regex;
		this.type = ParserType.Regex;
	}
	
	public Parser(Rule rule) {
		this.rule = rule;
		this.type = ParserType.Rule;
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
	
	public Rule getRule() {
		return this.rule;
	}
	
	public ParserType getParserType() {
		return this.type;
	}
	
	public ArrayList<String> parse(String input) {
		ArrayList<String> output = new ArrayList<String>();
		output.add(input);
		
		switch (this.type) {
			case Regex:
				output.remove(0); //If Regex is called via parse, it is not stacked and hence ignores the input String as first element
				Matcher matcher = this.regex.matcher(input);
				this.log.add("Starting regex isolation");
				while (matcher.find()) {
					output.add(matcher.group());
					this.log.add("Found \"" + matcher.group() + "\" at " + matcher.start());
				}
				this.log.add("Finished regex isolation");
			break;
			case Rule:
				output = rule.apply(output);
			break;
			case Parser:
				this.log.add("Starting to apply stacked parsers");
				for (Rule rule : this.rules) {
					output = rule.apply(output);
				}
			break;
		}
		
		return output;
	}
}
