package xmlhandler;

import java.io.*;
import java.util.ArrayList;
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
	
	public ArrayList<Pair<String, String>> getAttributes(Element input) {
		ArrayList<Pair<String, String>> output = new ArrayList<Pair<String, String>>();
		NamedNodeMap attributes = input.getAttributes();
		for (int i = 0; i < attributes.getLength() - 1; i++) {
			Node attribute = attributes.item(i);
			Pair<String, String> kvp = new Pair<String, String>(attribute.getNodeName(), attribute.getNodeValue());
			output.add(kvp);
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
}
