package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import indexassigner.IndexAssignerHandler;

/* This class can apply one or multiple different 
 * parsing rules to an input string. The output will
 * consist of an ArrayList of Strings */

public class CustomParser implements Parser {

	//TODO: Debugging without index assigner
	private String name;
	private List<String> order;
	private ConcurrentHashMap<String, Rule> elements;
	private List<String> log = Collections.synchronizedList(new ArrayList<String>());
	private List<String> indexAssigners;
	private String id;
	
	public CustomParser(ConcurrentHashMap<String, Rule> elements, List<String> indexAssigners, List<String> order, String name, String id) {
		this.indexAssigners = indexAssigners;
		this.elements = elements;
		this.order = order;
		this.id = id;
		this.name = name;
	}
	
	//Apply all rules to the string
	@Override
	public HashMap<String, String> parse(String input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		this.log.add("Starting parsing");
		
		if (parsedHeader == null) {
			this.log.add("No header found");
			parsedHeader = new HashMap<String, String>();
		}
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add(input);
		
		for (String rule : this.order) {
			Rule element = elements.get(rule);
			this.log.add("Applying " + ((Rule) element).getClass().getName() + " " + rule);
			strList = (((Rule) element).apply(strList, parsedHeader));
			this.log.add("--- Start log ---");
			this.log.addAll((((Rule) element).printLog()));
			this.log.add("--- End log ---");
		}
		HashMap<String, String> res = new HashMap<String, String>();
		for (String indexAssigner : this.indexAssigners) {
			res.putAll(IndexAssignerHandler.assign(indexAssigner, strList));
		}
		return res;
	}
	
	@Override
	public List<String> printLog() {
		return this.log;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(ArrayList<String> order) {
		this.order = order;
	}

	public ConcurrentHashMap<String, Rule> getElements() {
		return elements;
	}

	public void setElements(ConcurrentHashMap<String, Rule> elements) {
		this.elements = elements;
	}

	public List<String> getIndexAssigners() {
		return indexAssigners;
	}

	public void setIndexAssigners(ArrayList<String> indexAssigners) {
		this.indexAssigners = indexAssigners;
	}
	
	public String getID() {
		return this.id;
	}
	
	public void addRule(String id, Rule rule) {
		this.elements.put(id, rule);
		this.order.add(id);
	}
	
	public void removeRule(String id) {
		this.elements.remove(id);
		this.order.remove(id);
		ParserHandler.removeRule(this.id, id);
	}
	
	public void changeRulePosition(String id, int position) {
		this.order.remove(id);
		this.order.add(position, id);
	}
	
	public void changeAssignerPosition(int oldPos, int newPos) {
		String val = this.indexAssigners.get(oldPos);
		this.indexAssigners.remove(oldPos);
		this.indexAssigners.add(newPos, val);
	}
 	
}
