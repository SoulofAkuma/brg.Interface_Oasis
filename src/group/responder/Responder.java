package group.responder;

import java.util.ArrayList;
import java.util.HashMap;

import parser.ParserHandler;

public class Responder {
	
	private String parserID;
	private ArrayList<Constant> constants = new ArrayList<Constant>();
	private String port;

	public void repond() {
		// TODO Auto-generated method stub
		
	}

	public void repond(String[] response) {
		ArrayList<String> parsedBody = ParserHandler.getParser(this.parserID).parse(response[0]);
		HashMap<String, String> headerArgs = transformHeader(response[1]);
	}
	
	public static HashMap<String, String> transformHeader(String input) {
		String[] lines = input.split("\r\n");
		HashMap<String, String> returnVal = new HashMap<String, String>();
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			int splitIndex = line.indexOf(":", 0);
			String name = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 1, line.length());
			returnVal.put(name, value);
		}
		return returnVal;
	}

}
