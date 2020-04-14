package gui;

import java.util.ArrayList;
import parser.*;


public class Main {
	
	public static void main (String[] args) {
		
		ArrayList<Rule> rules = new ArrayList<Rule>();
		
		Rule first = new Split("&", 1, false);
		Rule funnycut = new Cut("test", 1, true, false, false);
		rules.add(first);
		rules.add(funnycut);
		Parser<Rule> parser = new Parser<Rule>(rules);
		printList((ArrayList<String>) parser.parse("testValOne=5&testValTwo=random&testValThree="));
		printList(parser.printLog());
		Rule second = new Isolate("[a-zA-Z]+=[^&]*");
		rules.removeAll(rules);
		rules.add(second);
		rules.add(funnycut);
		Parser<Rule> parser1 = new Parser<Rule>(rules);
		printList((ArrayList<String>) parser1.parse("testValOne=5&testValTwo=random&testValThree="));
		printList(parser1.printLog());
	}
	
	public static void printList(ArrayList<String> output) {
		for (String element : output) {
			System.out.println(element);			
		}
	}
}
