package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class Parser {

	//TODO: Debugging without index assigner
	
	private ArrayList<String> order;
	private HashMap<String, Rule> elements;
	private ArrayList<String> log = new ArrayList<String>();
	private IndexAssigner indexAssigner;
	
	public Parser(HashMap<String, Rule> elements, IndexAssigner indexAssigner, ArrayList<String> order) {
		this.indexAssigner = indexAssigner;
		this.elements = elements;
		this.order = order;
	}
	
	public Parser (HashMap<String, Rule> elements, ArrayList<String> order) {
		this.elements = elements;
		this.order = order;
	}
	
	//Apply all rules to the string
	public void parse(String input) {
		this.log.add("Starting parsing");
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (String rule : this.order) {
			Rule element = elements.get(rule);
			this.log.add("Applying " + ((Rule) element).printElement());
			strList = (((Rule) element).apply(strList));
			this.log.add("--- Start log ---");			
			this.log.addAll((((Rule) element).printLog()));
			this.log.add("--- End log ---");
		}
		for (String result : strList) {
			System.out.println(result);
		}
	}
	
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	
}
