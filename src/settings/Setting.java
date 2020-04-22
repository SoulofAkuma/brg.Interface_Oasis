package settings;

import cc.Pair;
import xmlhandler.SettingFunctions;
import java.util.ArrayList;
import org.w3c.dom.*;


public class Setting {
	
	/*
	 * This class accepts raw xml setting input
	 * and converts it into one Setting Object
	 * containing Setting Objects or values
	 */
	
	private String name; //Name of the Setting
	private ArrayList<Setting> subsettings = new ArrayList<Setting>();; //Content of the Setting
	private ArrayList<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();
	private String namespaceURI = null;
	private String value; //Value of the Setting
	private SettingFunctions functions = new SettingFunctions();
	private int level = -1;
	private Node node = null;
	
	private boolean corrupt = false;
	
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Setting> getSubsettings() {
		return this.subsettings;
	}	
	
	public static Setting parseSetting(String input, int level) {
		SettingFunctions functions = new SettingFunctions();
		String name = "";
		String value = "";
		ArrayList<Setting> subsettings = new ArrayList<Setting>();
		ArrayList<Node> subs = new ArrayList<Node>();
		ArrayList<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();
		Node node = null;
		functions.parseRootElement(input);
		if (functions.hasRoot()) {
			name = functions.getRootName();
			node = functions.getRootElement();
			attributes = (functions.getAttributes(functions.getRootElement()) == null) ? attributes : functions.getAttributes(functions.getRootElement());
			if (level == 1 && functions.getRootElement().getNamespaceURI()) {
				
			}
			if (!functions.hasSubsettings()) {
				return new Setting(name, node);
			}
			subs = functions.toArrayList(functions.getRootChildren());
		} else {
			if (functions.tryArtificial(input)) {
				if (level == 1) {
					return new Setting(functions.getArtificialResult());					
				} else {
					subs = functions.toArrayList(functions.getArtificialResult());
					node = null;
				}
			}
		}
		
		
		for (Node sub : subs) {
			if (sub.getNodeType() == Node.ELEMENT_NODE) {
				if (sub.hasChildNodes()) {
					subsettings.add(Setting.parseSetting(functions.nodeToString(sub), level + 1));
				} else {		
					subsettings.add(new Setting((Element) sub));
				}
			} else if (sub.getNodeType() == Node.TEXT_NODE){
				String addVal = sub.getNodeValue().replace("\\\\t", "");
				addVal = addVal.replace("\\\\r", "");
				addVal = addVal.replace("\\\\r", "");
				addVal = addVal.replace("\\\\b", "");
				if (!addVal.isEmpty()) {
					value += sub.getNodeValue();					
				}
			}
		}
		
		return new Setting(name, subsettings, node, value, attributes);
	}
	
	private Setting(String name, Node node) {
		this.level = 1;
		this.name = name;
		this.node = node;
	}
	
	private Setting(String name, ArrayList<Setting> subsettings, Node node, String value, ArrayList<Pair<String, String>> attributes) {
		this.level = 1;
		this.name = name;
		this.subsettings = subsettings;
		this.node = node;
		this.value = value;
		this.attributes = attributes;
	}
	
	private Setting(NodeList input) {
		this.level = 1;
		this.name = null;
		this.attributes = null;
		this.value = null;
		for (int i = 0; i < input.getLength(); i++) {
			Setting subSetting = Setting.parseSetting(this.functions.nodeToString(input.item(i)), 2);
			this.subsettings.add(subSetting);
		}
	}
	
	private Setting(Element element) {
		this.name = element.getNodeName();
		this.value = element.getTextContent();
		this.attributes = (this.functions.getAttributes(element) == null) ? this.attributes : this.functions.getAttributes(element) ;
	}
	
	public boolean isCorrupt() {
		return this.corrupt;
	}
	
	public void addSubsetting(Setting setting) {
		this.subsettings.add(setting);
	}
	
	public boolean hasSubsettings() {
		if (this.subsettings.size() == 0 || this.subsettings == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public String getXML() {
		String setXML = "";
		if (this.name == null) {
			setXML += getSubXML();
		} else {
			setXML += getTabLevel() + "<" + this.name + getAttrXML() + ">\n";						
			setXML += (this.value == null && !this.value.isEmpty()) ? "" : (getTabLevel(this.level) + this.value + "\n");
			setXML += getSubXML() + "\n";
			setXML += getTabLevel() + "</" + this.name + ">";
		}
		return setXML;
	}
	
	
	public String getSubXML() {
		String subXML = "";
		for (Setting sub : this.subsettings) {
			subXML += sub.getXML() + "\n";
		}
		subXML = subXML.substring(0, (subXML.length() > 2) ? (subXML.length() - 2) : 0);
		return subXML;
	}
	
	public String getAttrXML() {
		String attrXML = " ";
		for (Pair<String,String> attribute : this.attributes) {
			attrXML += attribute.getKey() + "=\"" + attribute.getValue() +"\" ";
		}
		attrXML = attrXML.substring(0, attrXML.length() - 1);
		return attrXML;
	}
	
	private String getTabLevel() {
		String tabs = "";
		int i;
		if (this.name == null) {
			i = 2;
		} else {
			i = 1;
		}
		for (; i < this.level; i++) {
			tabs += "\t";
		}
		return tabs;
	}
	
	private String getTabLevel(int amount) {
		String tabs = "";
		for (int i = 0; i < amount; i++) {
			tabs += "\t";
		}
		return tabs;
	}

	/*
	 * public String printSetting() { return "{\n      \"level\"=\"" + this.level +
	 * "\"\n      \"name\"=\"" + this.name + "\",\n      \"value\"=\"" +
	 * String.valueOf(this.value) + "\",\n      \"attributes\"=" + printAttributes()
	 * + ",\n      \"subsettings\"= " + printSubsettings() + "}"; }
	 * 
	 * public String printSubsettings() { String subsettingsString = "[\n"; for
	 * (Setting subsetting : this.subsettings) { subsettingsString += "      " +
	 * subsetting.printSetting() + "\n"; } subsettingsString += "]"; return
	 * subsettingsString; }
	 * 
	 * public String printAttributes() { String attributeString = "[\n"; for
	 * (Pair<String,String> attribute : this.attributes) { attributeString +=
	 * "      {\"name\"=\"" + attribute + "\",\"value\"=\"" + attribute + "\"}\n"; }
	 * attributeString += "]"; return attributeString; }
	 */
}
