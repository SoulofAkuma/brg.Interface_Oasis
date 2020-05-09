package parser;

import java.util.ArrayList;
import java.util.HashMap;

public interface Rule {
	
	public Rule genRule(HashMap<String, String> contructorArgs);
	
	//Applies the rule to the Parser
	public ArrayList<String> apply(ArrayList<String> input);
	
	//Prints information about the rule
	public String printElement();
	
	//Prints the log of the rule application
	public ArrayList<String> printLog();
	
	//Is called when all parsing is done and allows final modifications of the list
	public ArrayList<String> endProcedure(ArrayList<String> input);

}
