package parser;

import java.util.ArrayList;
import java.util.HashMap;

public interface Rule {
	
	public Rule genRule(HashMap<String, String> contructorArgs);
	
	//Applies the rule to the Parser
	public ArrayList<String> apply(ArrayList<String> input);
	
	//Prints the log of the rule application
	public ArrayList<String> printLog();
	
	//Returns all stored attributes to store them in an xml attribute (use xml attribute valid signs only)
	public HashMap<String, String> storeRule();
}
