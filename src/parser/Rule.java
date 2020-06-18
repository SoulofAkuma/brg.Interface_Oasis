package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Rule {
	
	public Rule genRule(HashMap<String, String> constructorArgs);
	
	//Applies the rule to the Parser
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader);
	
	//Prints the log of the rule application
	public List<String> printLog();
	
	//Returns all stored attributes to store them in an xml attribute (use xml attribute valid signs only)
	public HashMap<String, String> storeRule();
	
	//Print a String which describes the rule in the GUI
	public String printRuleLRP();
}
