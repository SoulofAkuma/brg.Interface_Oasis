package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Parser {
	
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader);
	
	public String getName();
	
	public List<String> printLog();

}
