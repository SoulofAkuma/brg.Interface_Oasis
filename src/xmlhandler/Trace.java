package xmlhandler;

import cc.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Trace implements parser.ParserInterface {
	
	//Default values
	public static final String ENCODING = "UTF-8";
	public static final ArrayList<Integer> N = null;
	public static final boolean GETNAME = false;
	
	private ArrayList<Pair<Short, String>> nodes; //List of Trace pairs which contain a node type constant and a node name
	private ArrayList<Integer> n;
	private boolean getName; //If true gets the name of the node instead of its value
	private String encoding; //Encoding of the input string
	private ArrayList<String> log = new ArrayList<String>();
	
	public Trace(ArrayList<Pair<Short, String>> nodes, boolean getName, ArrayList<Integer> n, String encoding) {
		this.nodes = nodes;
		this.getName = getName;
		this.encoding = encoding;
		if (n == null) {
			this.n = new ArrayList<Integer>();
			for (int i = 0; i < this.nodes.size(); i++) {
				this.n.add(1);
			}
		} else {
			this.n = n;
		}
	}
	
	//This method will only use the first element of the input list for parsing and will not modify it thus making this parser stackable
	@Override
	public ArrayList<String> apply(ArrayList<String> input) {
		this.log.add("Applying parser on \"" + input.get(0) + "\"");
		String traceResult = trace(input.get(0));
		if (traceResult == null) {
			return input;
		} else {
			input.add(traceResult);
		}
		return input;
	}

	@Override
	public String printElement() {
		String nodesAsJSON = "";
		for (Pair<Short, String> kvp : this.nodes) {
			nodesAsJSON += (kvp.getKey() == Node.ATTRIBUTE_NODE) ? "{Attribute," + kvp.getValue() + "}," : "{Element," + kvp.getValue() + "},";
		}
		if (!nodesAsJSON.isBlank()) {
			nodesAsJSON = nodesAsJSON.substring(0, nodesAsJSON.length() - 2);
		}
		String ns = "";
		for (int i = 0; i < this.n.size(); i++) {
			ns += this.n.get(i).toString();
			if (i != this.n.size() - 1) {
				ns += ",";
			}
		}
		return "XML Trace - nodes = [" + nodesAsJSON + "] | n = [" + ns + "] | getName = " + String.valueOf(this.getName) + " | encoding = " + this.encoding;
	}

	@Override
	public ArrayList<String> printLog() {
		return this.log;
	}
	
	//Removes first unmodified element
	@Override
	public ArrayList<String> endProcedure(ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 1; i < input.size(); i++) {
			output.add(input.get(i));
		}
		return output;
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
		
		try {
			inputStream = new ByteArrayInputStream(input.getBytes(this.encoding));
		} catch (Exception e) {
			errorLog("Unknown encoding", e.getMessage());
			return null;
		}
		try {
			documentBuilder = factory.newDocumentBuilder();			
			document = documentBuilder.parse(inputStream);
		} catch (Exception e) {
			this.log.add("The String does not seem to contain a root element - Adding artificial root element");
			input = "<rootDummy>" + input;
			input += "</rootDummy>";
			try {
				inputStream = new ByteArrayInputStream(input.getBytes(this.encoding));
			} catch (Exception e2) {
				errorLog("Unknown encoding", e.getMessage());
				return null;
			}
			try {
				documentBuilder = factory.newDocumentBuilder();			
				document = documentBuilder.parse(inputStream);
			} catch (Exception e2) {
				errorLog("Unknown error while parsing document", e.getMessage());
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
				default:
					errorLog("Unknown Node Type, Element and Attribute Support only");
					return null;
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
		if (this.getName) {
			return currentNode.getNodeName();
		} else {
			if (type == Node.ELEMENT_NODE) {
				return currentNode.getTextContent();
			} else {
				return currentNode.getNodeValue();				
			}
		}
		
	}
	
	private void errorLog(String message, String errorMessage) {
		this.log.add(message + " - Quitting xmlparser \nError Message " + errorMessage);
	}
	private void errorLog(String message) {
		this.log.add(message + " - Quitting xmlparser");
	}
	

}
