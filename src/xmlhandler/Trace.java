package xmlhandler;

import cc.Pair;
import gui.ListElement;
import gui.Logger;
import parser.ParserHandler;
import parser.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Trace implements Rule {
	
	private static final int ELEMENT = 0; //The string is the name of an element
	private static final int ATTRIBUTE = 1; //The string is the name of an attribute
	private static final int ELEMENTINDEX = 2; //The string is an integer which indicates the index of a child element
	private static final int COMBINE = 3; //The string is a key key-value pair in format key1:key2=value. key1 is an element name, key2 an attribute name and value the corresponding attribute value. Combine will search through all elements with the matching element name and match the first element which has the search attribute. 
	
	private List<Pair<Short, String>> nodes; //List of Trace pairs which contain a node type constant and a node name
	private String defVal;
	private List<String> log = Collections.synchronizedList(new ArrayList<String>());
	
	public Trace() {}
	
	public Trace(List<Pair<Short, String>> nodes, String defVal) {
		this.nodes = nodes;
		this.defVal = defVal;
	}
	
	public static String cToStr(short constant) {
		String rString = "";
		switch (constant) {
			case 0:
				rString = "Element";
			break;
			case 1:
				rString = "Attribute";
			break;
			case 2:
				rString = "ElementIndex";
			break;
			case 3:
				rString = "Combine";
			break;
		}
		return rString;
	}
	
	public static ListElement[] getNodeTypes() {
		ListElement[] vals = new ListElement[4];
		vals[0] = new ListElement("0", "Element");
		vals[1] = new ListElement("1", "Attribute");
		vals[2] = new ListElement("2", "ElementIndex");
		vals[3] = new ListElement("3", "Combine");
		return vals;
	}
	
	//Required parameters: nodes, getName, 
	@Override
	public Rule genRule(HashMap<String, String> constructorArgs) {
		String id = constructorArgs.get("id");
		String nodeStrings[] = ParserHandler.returnStringArrayIfExists(constructorArgs, "nodes");
		if (nodeStrings == null || nodeStrings.length / 2.0 != Math.round(nodeStrings.length / 2.0)) {
			ParserHandler.reportGenRuleError("nodes", this.getClass().getName(), id);
			return null;
		}
		ArrayList<Pair<Short, String>> nodes = new ArrayList<Pair<Short, String>>();
		for (int i = 0; i < nodeStrings.length; i += 2) {
			if (!ParserHandler.isShort(nodeStrings[i])) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName(), id);
				return null;
			} else if (Short.parseShort(nodeStrings[i]) != 0 && Short.parseShort(nodeStrings[i]) != 1 && Short.parseShort(nodeStrings[i]) != 2 && Short.parseShort(nodeStrings[i]) != 3) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName(), id);
				return null;
			} else if (Short.parseShort(nodeStrings[i]) == 2 && !ParserHandler.isInt(nodeStrings[i + 1])) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName(), id);
				return null;
			} else if (Short.parseShort(nodeStrings[i]) == 3 && !nodeStrings[i + 1].matches("[^:=]+:[^:=]+=[^:=]+")) {
				ParserHandler.reportGenRuleError("nodes", this.getClass().getName(), id);
				return null;
			} else {
				nodes.add(new Pair<Short, String>(Short.parseShort(nodeStrings[i]), nodeStrings[i + 1]));
			}
		}
		String defVal = ParserHandler.returnStringIfExists(constructorArgs, "defVal");
		if (defVal == null) {
			ParserHandler.reportGenRuleError("defVal", this.getClass().getName(), id);
		}
		return new Trace(Collections.synchronizedList(nodes), defVal);
	}
	
	//This method will only use the first element of the input list for parsing and will not modify it thus making this parser stackable
	@Override
	public ArrayList<String> apply(ArrayList<String> input, HashMap<String, String> parsedHeader) {
		this.log.clear();
		if (input.get(0) == null || input.get(0).isBlank()) {
			this.log.add("Body Empty");
			return input;
		}
		this.log.add("Applying parser on \"" + input.get(0).substring(0, 10) + "...\"");
		String traceResult = trace(input.get(0));
		if (traceResult == null) {
			this.log.add("Trace failed");
			input.add(this.defVal);
			return input;
		} else {
			this.log.add("Trace successful - found \"" + traceResult + "\"");
			input.add(traceResult);
		}
		return input;
	}

	@Override
	public List<String> printLog() {
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
			this.log.add((kvp.getKey() + " = " + kvp.getValue()));
			type = kvp.getKey();
			String name = kvp.getValue();
			String cNodeName = (currentNode == null) ? rootElement.getNodeName() : currentNode.getNodeName();
			switch (type) {
				case Trace.ATTRIBUTE:
					NamedNodeMap attributes = (currentNode == null) ? rootElement.getAttributes() : currentNode.getAttributes();
					Node tryGet = (attributes == null) ? null : attributes.getNamedItem(name);
					if (tryGet == null) {
						errorLog("Cannot find Attribute " + name + " inside of the Element " + cNodeName);
						return null;
					} else {
						this.log.add("Trace: Found Attribute " + name + " inside of " + cNodeName + ". Exiting Trace - No further step possible");
						currentNode = tryGet;
						breakOut = true;
					}
				break;
				case Trace.ELEMENT:
					if (currentNode == null && rootElement.getNodeName().equals(name)) {
						this.log.add("Trace: Found Element " + name + " - Element is root element");
						currentNode = rootElement;
						break;
					}
					NodeList elements = (currentNode == null) ? rootElement.getChildNodes() : currentNode.getChildNodes();
					boolean success = false;
					for (int i = 0; i < elements.getLength(); i++) {
						if (elements.item(i).getNodeName().equals(name)) {
							currentNode = elements.item(i);
							this.log.add("Trace: Found Element " + name + " inside of " + cNodeName);
							success = true;
							break;
						}
					}
					if (success) {
						break;
					}
					errorLog("Cannot find " + name + " inside of " + cNodeName);
					return null;
				case Trace.ELEMENTINDEX:
					elements = (currentNode == null) ? rootElement.getChildNodes() : currentNode.getChildNodes();
					ArrayList<Node> childrenWithoutText = new ArrayList<Node>();
					if (elements != null) {
						for (int i = 0; i < elements.getLength(); i++) {
							if (!elements.item(i).getNodeName().equals("#text")) {
								childrenWithoutText.add(elements.item(i));
							}
						}
					}
					if (elements == null || childrenWithoutText.size() <= Integer.parseInt(name) || Integer.parseInt(name) < 0) {
						errorLog("Cannot find the index " + name + " inside of " + cNodeName + " with a list length of " + elements.getLength());
						return null;
					}
					currentNode = childrenWithoutText.get(Integer.parseInt(name));
					this.log.add("Trace: Found Element " + currentNode.getNodeName() + " at index " + name + " inside of " + cNodeName + " with a list length of " + elements.getLength());
				break;
				case Trace.COMBINE:
					String nodeName = name.substring(0, name.indexOf(":"));
					String attrName = name.substring(name.indexOf(":") + 1, name.indexOf("="));
					String attrValue = name.substring(name.indexOf("=") + 1, name.length());
					elements = (currentNode == null) ? rootElement.getChildNodes() : currentNode.getChildNodes();
					ArrayList<Node> nameMatches = new ArrayList<Node>();
					success = false;
					for (int i = 0; i < elements.getLength(); i++) {
						if (elements.item(i).getNodeName().equals(nodeName)) {
							nameMatches.add(elements.item(i));
						}
					}
					for (Node match : nameMatches) {
						attributes = match.getAttributes();
						tryGet = attributes.getNamedItem(attrName);
						if (tryGet != null && tryGet.getNodeValue().equals(attrValue)) {
							currentNode = match;
							this.log.add("Trace: Found Element " + name + " with the attribute " + attrName + "=\"" + attrValue + "\"");
							success = true;
							break;
						}
					}
					if (nameMatches.size() > 0 && !success) {
						errorLog("Cannot find any " + nodeName + " element with the attribute " + attrName + "=\"" + attrValue + "\" among the name matches");
						return null;
					} else if (!success) {
						errorLog("Cannot find any element matches for " + nodeName);
						return null;
					}
			}
			if (breakOut) {
				break;
			}
			iteration++;
		}
		if (currentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
			return currentNode.getNodeValue();
		} else {
			return currentNode.getTextContent();
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
		for (int i = 0; i < this.nodes.size(); i++) {
			nodes += this.nodes.get(i).getKey() + "," + this.nodes.get(i).getValue() + ",";
		}
		if (nodes.length() > 1) {
			nodes = nodes.substring(0, nodes.length() - 1);
		}
		rule.put("nodes", nodes);
		rule.put("defVal", this.defVal);
		return rule;
	}
	
	@Override
	public String printRuleLRP() {
		String traceString = "";
		for (Pair<Short, String> pathElement : this.nodes) {
			traceString += cToStr(pathElement.getKey()) + " " + pathElement.getValue() + ", ";
		}
		traceString = (traceString.length() > 0) ? traceString.substring(0, traceString.length() - 1) : traceString;
		return "XMLTrace; " + this.defVal + "; " + traceString;
	}

	public List<Pair<Short, String>> getNodes() {
		return nodes;
	}

	public void setNodes(List<Pair<Short, String>> nodes) {
		this.nodes = nodes;
	}

	public String getDefVal() {
		return defVal;
	}

	public void setDefVal(String defVal) {
		this.defVal = defVal;
	}
	
}
