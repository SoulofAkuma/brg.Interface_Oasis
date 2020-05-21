package xmlhandler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import cc.Pair;

public class SettingFunctions {
	
	private DocumentBuilderFactory factory;
	private ByteArrayInputStream in;
	private DocumentBuilder builder;
	private Document document;
	private StringWriter out;
	private Transformer transformer;
	
	private Element rootElement = null;
	private NodeList artificialResult;
	
	public void parseRootElement(String input) {
		if (initIn(input)) {
			this.rootElement = this.document.getDocumentElement();
		}
	}
	
	public String getRootName() {
		if (hasRoot()) {
			return this.rootElement.getNodeName();
		} else {
			return "";
		}
	}
	
	public boolean hasRoot() {
		if (this.rootElement == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasSubsettings() {
		if (hasRoot()) {
			if (this.rootElement.hasChildNodes()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public NodeList getRootChildren() {
		if (hasRoot()) {
			return this.rootElement.getChildNodes();
		} else {
			return null;
		}
	}
	
	public String getRootText() {
		NodeList children = getRootChildren();
		ArrayList<Node> textParts = new ArrayList<Node>(); 
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.TEXT_NODE) {
				textParts.add(children.item(i));
			}
		}
		String text = "";
		boolean hasReplaced = false;
		for (int i = 0; i < textParts.size(); i++) {
			if (!hasEmptyText(textParts.get(i).getTextContent())) {
				text += textParts.get(i).getTextContent() + "\n";
				hasReplaced = true;
			}
		}
		text = (hasReplaced) ? text.substring(0, text.length() - 2) : text;
		return text;
	}
	
	public String getRootNamespaceURI() {
		return this.rootElement.getNamespaceURI();
	}
	
	public String stripXML(String input) {
		boolean crlf;
		String[] lines = null;
		if (input.contains("\r\n")) {
			lines = input.split("\\r\\n");
			crlf = false;
		} else {
			input.split("\\x0D\\x0A");
			crlf = true;
		}
		String output = "";
		if (lines == null) {
			return input;
		}
		for (String line : lines) {	
			line = line.strip();
			output += line + ((crlf) ? ((char) 0x0D + (char) 0x0A) : "\r\n");
		}
		return output;
	}
	
	public HashMap<String, String> getAttributes(Element input) {
		HashMap<String, String> output = new HashMap<String, String>();
		NamedNodeMap attributes = input.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			output.put(attribute.getNodeName(), attribute.getNodeValue());
		}
		return output;
	}
	
	public boolean tryArtificial(String input) {
		input = "<artificialRoot>" + input;
		input += "</artificialRoot>";
		if (initIn(input)) {
			this.rootElement = this.document.getDocumentElement();
			if (rootElement.hasChildNodes()) {
				NodeList children = rootElement.getChildNodes();
				this.artificialResult = children;
				return true;
			}
		}
		return false;
	}
	
	public NodeList getArtificialResult() {
		return this.artificialResult;
	}
	
	public String getWork(String input) {
		parseRootElement(input);
		if (hasRoot()) {
			return nodeListToString(this.rootElement.getChildNodes());
		} else {
			if (tryArtificial(input)) {
				return nodeListToString(this.artificialResult);
			} else {
				return null;
			}
		}
	}
	
	public ArrayList<Pair<String, String>> getAttributes(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		ArrayList<Pair<String, String>> attributeList = new ArrayList<Pair<String, String>>();
		if (attributes == null) {
			return attributeList;
		}
		for (int i = 0; i < attributes.getLength() - 1; i++) {
			Pair<String, String> kvp = new Pair<String, String>(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
			attributeList.add(kvp);
		}
		return attributeList;
	}
	
	public Element getRootElement() {
		return this.rootElement;
	}
	
	public boolean hasEmptyText(String input) {
		String codes = "abfnrtv";
		for (int i = 0; i < codes.length(); i++) {
			input = input.replaceAll("\\" + codes.charAt(i), "");
		}
		input.replace(((char) 0x0A + ""), "");
		input.replace(((char) 0x0D + ""), "");
		if (input.isEmpty()) {			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasEmptyText(Node node) {
		String input = node.getTextContent();
		String codes = "abfnrtv";
		for (int i = 0; i < codes.length(); i++) {
			input = input.replaceAll("\\" + codes.charAt(i), "");
		}
		input.replace(((char) 0x0A + ""), "");
		input.replace(((char) 0x0D + ""), "");
		if (!input.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasChildElement(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Node> filterElement(ArrayList<Node> input) {
		ArrayList<Node> output = new ArrayList<Node>();
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(input.get(i));
			}
		}
		return output;
	}
	
	public String nodeListToString(NodeList nodeList) {
		String result = "";
		for (int i = 0; i < nodeList.getLength() - 1; i++) {
			if (initOut(false, false)) {
				try {
					this.transformer.transform(new DOMSource(nodeList.item(i)), new StreamResult(this.out));
					result += this.out.toString();
				} catch (Exception e) {
					return null;
				}			
			} else {
				return null;
			}
		}
		return result;
	}
	
	public String nodeToString(Node node) {
		if (initOut(false, false)) {
			try {
				this.transformer.transform(new DOMSource(node), new StreamResult(this.out));
				return this.out.toString();
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public ArrayList<Node> toArrayList(NodeList list) {
		ArrayList<Node> asArrayList = new ArrayList<Node>();
		for (int i = 0; i < list.getLength() - 1; i++) {
			asArrayList.add(list.item(i));
		}
		return asArrayList;
	}
	
	private boolean initOut(boolean declaration, boolean indent) {
		String decString = (declaration) ? "no" : "yes";
		String indString = (indent) ? "yes" : "no";
		try {
			this.transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, decString);
			transformer.setOutputProperty(OutputKeys.INDENT, indString);
			this.out = new StringWriter();
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	private boolean initIn(String input) {
		
		factory = DocumentBuilderFactory.newInstance();
		in = new ByteArrayInputStream(input.getBytes());
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(in);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
//	private static void reportError(String source, String causes, String errorMessage) {
//		String[] elements = {"GroupID", "GroupName", "Source", "Causes", "ErrorMessage"};
//		String[] values = {SettingHandler.SETTINGPARSINGID, SettingHandler.SETTINGPARSINGID, source, causes, errorMessage};
//		String message = source + " in " + SettingHandler.SETTINGPARSINGNAME + " reported " + causes + " caused by " + errorMessage;
//		Logger.addMessage(MessageType.Error, MessageOrigin.Settings, errorMessage, SettingHandler.SETTINGPARSINGID, elements, values, false);
//	}
}
