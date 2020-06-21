package parser;

import java.util.HashMap;
import java.util.List;

public interface Parser {
	
	//Parse the unparsed body and parsed header into a map
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader);
	
	//Return the name of the parser for GUI Selection
	public String getName();
	
	//Print the log (optional, the log is used for program debugging and has no implementation in the program yet)
	public List<String> printLog();

}
