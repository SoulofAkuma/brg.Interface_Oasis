package parser;

import java.util.ArrayList;

public class Rule {
	
	public final RuleType ruleType = null;
	
	//Applies the rule to the Parser
	public ArrayList<String> apply(ArrayList<String> input) {
		return input;
	}
	
	//Prints information about the rule
	public String printRule() {
		return "";
	}
	
	//Prints the log of the rule application
	public ArrayList<String> printLog() {
		return null;
	}
	
}
