package settings;

import cc.Pair;
import xmlhandler.SettingFunctions;
import java.util.ArrayList;

import org.w3c.dom.*;

public class Setting {
	
	private static int sIDState = 0;

	/*
	 * This class accepts raw xml setting input and converts it into one Setting
	 * Object containing Setting Objects or values
	 */

	private String name; // Name of the Setting
	private ArrayList<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();
	private String namespaceURI = null;
	private String value = null; // Value of the Setting
	private int level = -1;
	private int sID;
	private boolean isEmpty = false;

	private Node node = null;

	private ArrayList<Setting> subsettings = new ArrayList<Setting>();; // Content of the Setting
	private SettingFunctions functions = new SettingFunctions();

	private boolean corrupt = false;

	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public int getID() {
		return this.sID;
	}
	
	public ArrayList<Pair<String, String>> getAttributes() {
		return this.attributes;
	}

	public ArrayList<Setting> getSubsettings() {
		return this.subsettings;
	}
	
	public boolean reset() {
		return this.isEmpty;
	}

	public static Setting parseSetting(String input, int level) {
		if ((input == null || input.isBlank()) && level != 1) {
			return new Setting(true, level);
		} else if ((input == null | input.isBlank()) && level == 1) {
			return new Setting(true, true, level);
		}
		SettingFunctions functions = new SettingFunctions();
		if (level == 1) {
			input = functions.stripXML(input);
		}
		String name = "";
		String value = null;
		ArrayList<Setting> subsettings = new ArrayList<Setting>();
		ArrayList<Node> subs = new ArrayList<Node>();
		ArrayList<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();
		Node node = null;
		String namespaceURI = null;
		functions.parseRootElement(input);
		if (functions.hasRoot()) {
			name = functions.getRootName();
			node = functions.getRootElement();
			attributes = (functions.getAttributes(functions.getRootElement()) == null) ? attributes
					: functions.getAttributes(functions.getRootElement());
			value = (functions.getRootText().isEmpty()) ? null : functions.getRootText();
			if (level == 1 && functions.getRootNamespaceURI() != null) {
				namespaceURI = functions.getRootNamespaceURI();
			}
			if (!functions.hasSubsettings()) {
				return new Setting(name, node);
			}
			subs = functions.filterElement(functions.toArrayList(functions.getRootChildren()));
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
				if (functions.hasChildElement(sub)) {
					subsettings.add(Setting.parseSetting(functions.nodeToString(sub), level + 1));
				} else {
					subsettings.add(new Setting((Element) sub, level + 1));
				}
			}
		}

		return new Setting(name, subsettings, node, value, attributes, namespaceURI, level);
	}
	
	private Setting(boolean corrupt, int level) {
		this.corrupt = true;
		this.level = level;
	}
	
	private Setting(boolean placeholder, boolean isEmpty, int level) {
		this.isEmpty = true;
		this.level = level;
	}

	private Setting(String name, Node node) {
		this.level = 1;
		this.name = name;
		this.node = node;
		this.sID = Setting.sIDState;
		Setting.sIDState++;
	}

	private Setting(String name, ArrayList<Setting> subsettings, Node node, String value,
			ArrayList<Pair<String, String>> attributes, String namespaceURI, int level) {
		this.level = level;
		this.name = name;
		this.subsettings = subsettings;
		this.node = node;
		this.value = (value == null || value.isEmpty()) ? null : value; //Main Setting should not have an empty value
		this.attributes = attributes;
		this.namespaceURI = namespaceURI;
		this.sID = Setting.sIDState;
		Setting.sIDState++;
	}

	private Setting(NodeList input) {
		this.level = 1;
		this.name = "";
		this.attributes = null;
		this.value = null;
		for (int i = 0; i < input.getLength(); i++) {
			Setting subSetting = Setting.parseSetting(this.functions.nodeToString(input.item(i)), 2);
			this.subsettings.add(subSetting);
		}
		this.sID = Setting.sIDState;
		Setting.sIDState++;
	}

	private Setting(Element element, int level) {
		this.level = level;
		this.name = element.getNodeName();
		this.value = (element.getTextContent().isEmpty()) ? null : element.getTextContent();
		this.attributes = (this.functions.getAttributes(element) == null) ? this.attributes : this.functions.getAttributes(element);
		this.sID = Setting.sIDState;
		Setting.sIDState++;
	}
	
	private Setting(String name, String value, ArrayList<Pair<String, String>> attributes, int level) {
		this.name = name;
		this.value = value;
		this.attributes = attributes;
		this.level = level;
		this.sID = Setting.sIDState++;
		Setting.sIDState++;
	}
	
	private void replaceAll(Setting setting) {
		this.name = setting.name;
		this.attributes = setting.attributes;
		this.namespaceURI = setting.namespaceURI;
		this.value = setting.value;
		this.level = setting.level;
		this.sID = setting.sID;
		this.node = setting.node;
		this.subsettings = setting.subsettings;
		this.functions = setting.functions;
		this.corrupt = setting.corrupt;
		this.isEmpty = setting.isEmpty;
	}

	public boolean isCorrupt() {
		return this.corrupt;
	}

	public void addSubsetting(Setting setting) {
		this.subsettings.add(setting);
	}

	public boolean hasSubsettings() {
		if (this.subsettings == null || this.subsettings.size() == 0) {
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
			setXML += (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + ">\r\n") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + this.namespaceURI + "\">\r\n");
			if (this.value != null) {
				if (this.value.contains("\n")) {
					String[] lines = this.value.split("\\n");
					for (String line : lines) {
						setXML += getTabLevel(this.level + 1) + line + "\r\n";
					}
				} else {
					setXML += getTabLevel(this.level + 1) + this.value + "\r\n";
				}
				if (this.subsettings.size() == 0) {
					setXML = (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + ">") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + this.namespaceURI + "\">");
					setXML += value;
					setXML += "</" + this.name + ">";
					return setXML;
				}
			} else if (this.value == null && !hasSubsettings() && level != 1) {
				System.out.println("true");
				setXML = (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + "/>") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + this.namespaceURI + "\"/>");
				return setXML;
			}
			setXML += (this.subsettings.size() > 0) ? (getSubXML() + "\r\n") : "";
			setXML += getTabLevel() + "</" + this.name + ">";					
		}
		return setXML;
	}

	public String getSubXML() {
		String subXML = "";
		for (Setting sub : this.subsettings) {
			subXML += sub.getXML() + "\r\n";
		}
		subXML = subXML.substring(0, (subXML.length() > 2) ? (subXML.length() - 2) : 0);
		return subXML;
	}

	public String getAttrXML() {
		String attrXML = " ";
		for (Pair<String, String> attribute : this.attributes) {
			attrXML += attribute.getKey() + "=\"" + attribute.getValue() + "\" ";
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

	
	public String printSetting() { 
		return "{\n\t\"level\"=\"" + this.level +"\"\n\t\"name\"=\"" + this.name + "\",\n\t\"value\"=\"" + String.valueOf(this.value) + "\",\n\t\"attributes\"=" + printAttributes() + ",\n\t\"subsettings\"= " + printSubsettings() + "}"; 
	}
	  
	public String printSubsettings() { 
		String subsettingsString = "[\n";
		for (Setting subsetting : this.subsettings) {
			subsettingsString += "\t" + subsetting.printSetting() + "\n";
		} 
		subsettingsString += "]"; 
		return subsettingsString;
	}
	  
	public String printAttributes() { 
		String attributeString = "[\n"; 
		for (Pair<String,String> attribute : this.attributes) {
			attributeString += "\t{\"name\"=\"" + attribute.getKey() + "\",\"value\"=\"" + attribute.getValue() + "\"}\n"; 
		}
		attributeString += "]"; return attributeString;
	}
	
	public ArrayList<Setting> getSettings(String name) {
		ArrayList<Setting> results = new ArrayList<Setting>();
		if (this.name.equals(name)) {
			results.add(this);
			return results;
		}
		int lockLevel = -1;
		for (int i = 0; i < this.subsettings.size(); i++) {
				results.addAll(this.subsettings.get(i).getSettings(name));				
		}
		while (!levelEquality(results)) {
			for (int i = 0; i < results.size(); i++) {
				if (lockLevel == -1) {
					lockLevel = results.get(i).level;
				} else {
					if (results.get(i).level != lockLevel) {
						results.remove(i);
					}
				}
			}
		}
		return results;
	}
	
	public Pair<String, String> getAttribute(String name) {
		for (Pair<String, String> attribute : this.attributes) {
			if (attribute.getKey().equals(name)) {
				return attribute;
			}
		}
		return null;
	}
	
	public boolean levelEquality(ArrayList<Setting> list) {
		ArrayList<Integer> levels = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (levels.contains(list.get(i))) {
				return false;
			} else {
				levels.add(list.get(i).level);
			}
		}
		return true;
	}
	
	public boolean replaceID(int sID, Setting setting) {
		if (this.sID == sID) {
			replaceAll(setting);
			return true;
		}
		for (int i = 0; i < this.subsettings.size(); i++) {
			if (this.subsettings.get(i).replaceID(sID, setting)) {
				return true;
			}
		}
		return false;
	}
	
	public void addSetting(String name, String value, ArrayList<Pair<String, String>> attributes) {
		this.subsettings.add(new Setting(name, value, attributes, this.level + 1));
	}
	
	public void resetSetting(String name) {
		if (this.level == 1 && this.isEmpty) {
			this.name = name;
			this.attributes = new ArrayList<Pair<String, String>>();
			this.namespaceURI = null;
			this.value = null;
			this.node = null;
			this.subsettings = new ArrayList<Setting>();
			this.functions = new SettingFunctions();
			this.corrupt = false;
			this.isEmpty = false;
		}
	}
}
