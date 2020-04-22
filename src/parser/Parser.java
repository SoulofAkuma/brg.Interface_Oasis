package parser;

import java.util.ArrayList;
import java.util.Arrays;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser {

	private ArrayList<ParserInterface> elements = new ArrayList<ParserInterface>();
	private ArrayList<String> log = new ArrayList<String>();
	
	public Parser(ParserInterface element) {
		this.elements.add(element);
	}
	
	public Parser(ArrayList<ParserInterface> elements) {
		for (ParserInterface element: elements) {
			this.elements.add(element);
		}
	}
	
	public Parser(ParserInterface elementArray[]) {
		ArrayList<ParserInterface> elements = new ArrayList<ParserInterface>(Arrays.asList(elementArray));
		for (ParserInterface element: elements) {
			this.elements.add(element);
		}
	}
	
	//Apply all rules to the string
	public ArrayList<String> parse(String input) {
		this.log.add("Starting parsing");
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (ParserInterface element: this.elements) {
			this.log.add("Applying " + ((ParserInterface) element).printElement());
			strList = (((ParserInterface) element).apply(strList));
			this.log.add("--- Start log ---");			
			this.log.addAll((((ParserInterface) element).printLog()));
			this.log.add("--- End log ---");
		}
		if (this.elements.size() > 0) {
			strList = ((ParserInterface) this.elements.get(this.elements.size() - 1)).endProcedure(strList);
		}
		return strList;
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	
}
