package xmlhandler;

import cc.Pair;
import gui.Logger;
import parser.ParserHandler;
import parser.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Trace implements Rule {
	
	private static final int ELEMENT = 0; //The string is the name of an element
	private static final int ATTRIBUTE = 1; //The string is the name of an attribute
	private static final int ELEMENTINDEX = 2; //The string is an integer which indicates the index of a child element
	private static final int COMBINE = 3; //The string is a key key-value pair in format key1:key2=value. key1 is an element name, key2 an attribute name and value the corresponding attribute value. Combine will search through all elements with the matching element name and match the first element which has the search attribute. 
	
	private ArrayList<Pair<Short, String>> nodes; //List of Trace pairs which contain a node type constant and a node name
	private String defVal;
	private ArrayList<String> log = new ArrayList<String>();
	
	public Trace(ArrayList<Pair<Short, String>> nodes, String defVal) {
		this.nodes = nodes;
		this.defVal = defVal;
	}
	
	//Required parameters: nodes, getName, 
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String nodeStrings[] = ParserHandler.returnStringArrayIfExists(constructorArgs, "nodes");
		if (nodeStrings == null || nodeStrings.length / 2 != Math.round(nodeStrings.length / 2)) {
			ParserHandler.reportGenRuleError("nodes", this.getClass().getName());
			return null;
		}
		ArrayList<Pair<Short, String>> nodes = new ArrayList<Pair<Short, String>>();
		for (int i = 0; i < nodeStrings.length; i += 2) {
			if (!ParserHandler.isShort(nodeStrings[i])) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName());
				return null;
			} else if (Short.parseShort(nodeStrings[i]) != Node.ATTRIBUTE_NODE && Short.parseShort(nodeStrings[i]) != Node.ELEMENT_NODE) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName());
				return null;
			} else {
				nodes.add(new Pair<Short, String>(Short.parseShort(nodeStrings[i]), nodeStrings[i + 1]));
			}
		}
		String defVal = ParserHandler.returnStringIfExists(constructorArgs, "defVal");
		if (defVal == null) {
			ParserHandler.reportGenRuleError("defVal", this.getClass().getName());
		}
		return new Trace(nodes, defVal);
	}
	
	//This method will only use the first element of the input list for parsing and will not modify it thus making this parser stackable
	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		this.log.add("Applying parser on \"" + input.get(0) + "\"");
		String traceResult = trace(input.get(0));
		if (traceResult == null) {
			this.log.add("Trace failed");
			return input;
		} else {
			this.log.add("Trace successful - found \"" + traceResult + "\"");
			input.add(traceResult);
		}
		return input;
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	private String trace(String input) {
		if (this.nodes.size() == 0) {
			this.log.add("No trace path - skipping everyting");
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ByteArrayInputStream inputStream;
		DocumentBuilder documentBuilder;
		Document document;
		
		inputStream = new ByteArrayInputStream(input.getBytes());
		try {
			documentBuilder = factory.newDocumentBuilder();			
			document = documentBuilder.parse(inputStream);
		} catch (Exception e) {
			this.log.add("The String does not seem to contain a root element - Adding artificial root element");
			input = "<rootDummy>" + input;
			input += "</rootDummy>";
			inputStream = new ByteArrayInputStream(input.getBytes());
			try {
				documentBuilder = factory.newDocumentBuilder();			
				document = documentBuilder.parse(inputStream);
			} catch (Exception e2) {
				errorLog("Unknown error while parsing document", e.getMessage());
				Logger.reportException("Trace", "trace", e2);
				return null;				
			}
		}
		
		Element rootElement = document.getDocumentElement();
		Node currentNode = null;
		int iteration = 0;
		boolean breakOut = false;
		short type = 0;
		for (Pair<Short, String> kvp : this.nodes) {
			type = kvp.getKey();
			String name = kvp.getValue();
			switch (type) {
				case Node.ATTRIBUTE_NODE:
					if (currentNode != null) {
						if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
							NamedNodeMap attributes = currentNode.getAttributes();
							Node tryGet = attributes.getNamedItem(name);
							if (tryGet == null) {
								errorLog("Cannot find Attribute " + name + "inside of the Element " + currentNode.getNodeName());
								return null;
							} else {
								this.log.add("Trace: Found Attribute " + name + ". Exiting Trace - No further step possible");
								currentNode = tryGet;
								breakOut = true;
							}
						} else {
							errorLog("Cannot find Attribute " + name + " outside of an Element node");
							return null;
						}
					} else {
						currentNode = rootElement.getAttributeNode(name);
						if (currentNode == null) {
							errorLog("Cannot find Attribute " + name + "inside of the element " + rootElement.getNodeName());
							return null;
						}
						this.log.add("Trace: Found Attribute " + name + ". Exiting Trace - No further step possible");
						breakOut = true;
					}
				break;
				case Node.ELEMENT_NODE:
					if (currentNode == null) {
						if (rootElement.getNodeName() == name) {
							currentNode = rootElement;
							this.log.add("Trace: Found Element - element is root node");
							break;
						}
						NodeList elements = rootElement.getElementsByTagName(name);
						if (elements.getLength() >= this.n.get(iteration) && this.n.get(iteration) > 0) {
							this.log.add("Trace: Found Element " + name  + " at given position " + this.n.get(iteration) +  " inside of the Element " + rootElement.getNodeName());
							currentNode = elements.item(this.n.get(iteration) - 1);
						} else if (this.n.get(iteration) < 0) {
							if (elements.getLength() > 0) {
								this.log.add("Trace: Found Element " + name + " at position 1 inside of the Element " + rootElement.getNodeName());
								currentNode = elements.item(0);
							} else {
								errorLog("Cannot find Element " + name + "inside of " + rootElement.getNodeName());
								return null;
							}
						} else {
							if (elements.getLength() > 0) {
								this.log.add("Trace: Found Element " + name + " at position + " + elements.getLength() + " inside of the Element " + rootElement.getNodeName());
								currentNode = elements.item(elements.getLength() - 1);
							} else {
								errorLog("Cannot find Element " + name + "inside of " + rootElement.getNodeName());
								return null;
							}
						}
					} else {
						NodeList nodes = currentNode.getChildNodes();
						ArrayList<Node> elements = new ArrayList<Node>();
						for (int i = 0; i < nodes.getLength(); i++) {
							if (nodes.item(i).getNodeName().equals(name)) {
								elements.add(nodes.item(i));
							}
						}
						if (elements.size() <= this.n.get(iteration) && this.n.get(iteration) > 0) {
							this.log.add("Trace: Found Element " + name + " at given position " + this.n.get(iteration) + " inside of the Element " + rootElement.getNodeName());
							currentNode = elements.get(this.n.get(iteration) - 1);
						} else if (this.n.get(iteration) <= 0) {
							if (elements.size() > 0) {
								this.log.add("Trace: Found Element " + name + " at position 1 inside of the Element " + rootElement.getNodeName());
								currentNode = elements.get(0);
							} else {
								errorLog("Cannot find Element " + name + "inside of " + rootElement.getNodeName());
								return null;
							}
						} else {
							if (elements.size() > 0) {
								this.log.add("Trace: Found Element " + name + " at position " + elements.size() + " inside of the Element " + rootElement.getNodeName());
								currentNode = elements.get(elements.size() - 1);
							} else {
								errorLog("Cannot find Element " + name + "inside of " + rootElement.getNodeName());
								return null;
							}
						}
					}
				break;
			}
			if (breakOut) {
				break;
			}
			if (currentNode == null) {
				errorLog("No trace result");
				return null;
			}
			iteration++;
		}
		
	}
	
	private void errorLog(String message, String errorMessage) {
		this.log.add(message + " - Quitting xmlparser \nError Message " + errorMessage);
	}
	private void errorLog(String message) {
		this.log.add(message + " - Quitting xmlparser");
	}

	@Override
	public HashMap<String, String> storeRule() {
		HashMap<String, String> rule = new HashMap<String, String>();
		String nodes = "";
		for (int i = 0; i < this.nodes.size(); i += 2) {
			nodes += this.nodes.get(i).getKey() + "," + this.nodes.get(i + 1).getValue() + ",";
		}
		if (nodes.length() > 1) {
			nodes = nodes.substring(0, nodes.length() - 1);
		}
		rule.put("nodes", nodes);
		return rule;
	}
}
