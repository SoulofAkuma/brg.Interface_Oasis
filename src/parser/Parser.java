package parser;

import java.util.ArrayList;
import java.util.HashMap;

public interface Parser {
	
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader);
	
	public ArrayList<String> printLog();

}
