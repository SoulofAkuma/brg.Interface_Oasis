package parser;

import java.util.ArrayList;

public abstract class Rule implements ParserInterface {
	
	//Stores the type of the rule
	public abstract RuleType ruleType();
	
	//Applies the rule to the Parser
	public abstract ArrayList<String> apply(ArrayList<String> input);
	
	//Prints information about the rule
	public abstract String printElement();
	
	//Prints the log of the rule application
	public abstract ArrayList<String> printLog();
	
}
