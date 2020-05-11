package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser {

	private ArrayList<Rule> elements = new ArrayList<Rule>();
	private ArrayList<String> log = new ArrayList<String>();
	private IndexAssigner indexAssigner;
	
	public Parser(ArrayList<Rule> elements, IndexAssigner indexAssigner) {
		this.indexAssigner = indexAssigner;
		for (Rule element: elements) {
			this.elements.add(element);
		}
	}
	
	//Apply all rules to the string
	public HashMap<String, String> parse(String input) {
		this.log.add("Starting parsing");
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (Rule element: this.elements) {
			this.log.add("Applying " + ((Rule) element).printElement());
			strList = (((Rule) element).apply(strList));
			this.log.add("--- Start log ---");			
			this.log.addAll((((Rule) element).printLog()));
			this.log.add("--- End log ---");
		}
		if (this.elements.size() > 0) {
			strList = ((Rule) this.elements.get(this.elements.size() - 1)).endProcedure(strList);
		}
		return this.indexAssigner.assign(strList);
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	
}
