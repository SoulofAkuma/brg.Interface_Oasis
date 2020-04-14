package parser;

import java.util.ArrayList;
import java.util.Arrays;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser<T> {

	private ArrayList<T> elements = new ArrayList<T>();
	private ArrayList<String> log = new ArrayList<String>();
	
	public Parser(T element) {
		this.elements.add(element);
	}
	
	public Parser(ArrayList<T> elements) {
		for (T element: elements) {
			this.elements.add(element);
		}
	}
	
	public Parser(T elementArray[]) {
		ArrayList<T> elements = new ArrayList<T>(Arrays.asList(elementArray));
		for (T element: elements) {
			this.elements.add(element);
		}
	}
	
	//Apply all rules to the string
	public ArrayList<String> parse(String input) {
		this.log.add("Starting parsing");
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (T element: this.elements) {
			this.log.add("Applying " + ((ParserInterface) element).printElement());
			strList = (((ParserInterface) element).apply(strList));
			this.log.add("--- Start log ---");			
			this.log.addAll((((ParserInterface) element).printLog()));
			this.log.add("--- End log ---");						
		}
		return strList;
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	
}
