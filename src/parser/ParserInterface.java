package parser;

import java.util.ArrayList;

public interface ParserInterface {
	
	//Applies the rule to the Parser
	public ArrayList<String> apply(ArrayList<String> input);
	
	//Prints information about the rule
	public String printElement();
	
	//Prints the log of the rule application
	public ArrayList<String> printLog();

}
