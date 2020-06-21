package settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xmlhandler.SettingFunctions;

public class Setting {
	
	private static int sIDState = 0;

	/*
	 * This class is an interface for DOM Nodes and accepts raw XML which is then parsed into settings 
	 * It offers setting functionality node doesn't, but builds a setting tree of a similar kind than a node tree
	 */

	private String name; // Name of the Setting
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private String namespaceURI = null;
	private String value = ""; // Value of the Setting
	private int level = -1;
	private int sID;
	private boolean isEmpty = false;
	private boolean enabled = true;

	private Node node = null;

	private ArrayList<Setting> subsettings = new ArrayList<Setting>();; // Content of the Setting
	private SettingFunctions functions = new SettingFunctions();

	private boolean corrupt = false;

	public boolean isEnabled() {
		return this.enabled;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSID() {
		return this.sID;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getID() {
		return this.sID;
	}
	
	public HashMap<String, String> getAttributes() {
		return this.attributes;
	}

	public ArrayList<Setting> getSubsettings() {
		return this.subsettings;
	}
	
	public boolean reset() {
		return this.isEmpty;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public Setting getBackup() {
		return new Setting(this);
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void addReplaceAttributes(HashMap<String, String> newAttributes) {
		this.attributes.putAll(newAttributes);
	}
	
	//Parses an xml String into a setting
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
		HashMap<String, String> attributes = new HashMap<String, String>();
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
		this.sID = Setting.sIDState++;
	}

	private Setting(String name, ArrayList<Setting> subsettings, Node node, String value,
			HashMap<String, String> attributes, String namespaceURI, int level) {
		this.level = level;
		this.name = name;
		this.subsettings = subsettings;
		this.node = node;
		this.value = (value == null) ? "" : value; //Main Setting should not have an empty value
		this.attributes = attributes;
		this.namespaceURI = namespaceURI;
		this.sID = Setting.sIDState++;
	}

	private Setting(NodeList input) {
		this.level = 1;
		this.name = "";
		this.value = "";
		for (int i = 0; i < input.getLength(); i++) {
			Setting subSetting = Setting.parseSetting(this.functions.nodeToString(input.item(i)), 2);
			this.subsettings.add(subSetting);
		}
		this.sID = Setting.sIDState++;
	}

	private Setting(Element element, int level) {
		this.level = level;
		this.name = element.getNodeName();
		this.value = (element.getTextContent() == null) ? "" : element.getTextContent();
		this.attributes = (this.functions.getAttributes(element) == null) ? this.attributes : this.functions.getAttributes(element);
		this.sID = Setting.sIDState++;
	}
	
	private Setting(String name, String value, HashMap<String, String> attributes, int level) {
		this.name = name;
		this.value = (value == null) ? "" : value;
		this.attributes = (attributes == null) ? this.attributes : attributes;
		this.level = level;
		this.sID = Setting.sIDState++;
	}
	
	private Setting(Setting obj) {
		this.attributes = (HashMap<String, String>) obj.attributes.clone();
		this.corrupt = obj.corrupt;;
		this.isEmpty = obj.isEmpty;
		this.level = obj.level;
		this.name = obj.name;
		this.namespaceURI = String.valueOf(obj.namespaceURI);
		this.node = obj.node;
		this.sID = obj.sID;
		this.subsettings = (ArrayList<Setting>) obj.subsettings.clone();
		this.value = String.valueOf(obj.value);
		this.enabled = obj.enabled;
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
		this.enabled = setting.enabled;
	}

	//Returns whether this setting is corrupt
	public boolean isCorrupt() {
		return this.corrupt;
	}

	//Adds a subsetting to this settings subsettings
	public void addSubsetting(Setting setting) {
		this.subsettings.add(setting);
	}

	//Returns whether the setting has subsettings
	public boolean hasSubsettings() {
		if (this.subsettings == null || this.subsettings.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	//Returns the whole setting in xml format
	public String getXML() {
		String setXML = "";
		if (this.name == null) {
			setXML += getSubXML();
		} else {
			setXML += (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + ">\r\n") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + escapeSpecialChars(this.namespaceURI) + "\">\r\n");
			if (!this.isEmptyValue(this.value)) {
				if (this.value.contains("\n")) {
					String[] lines = this.value.split("\\n");
					for (String line : lines) {
						setXML += getTabLevel(this.level + 1) + escapeSpecialChars(line) + "\r\n";
					}
				} else {
					setXML += getTabLevel(this.level + 1) + escapeSpecialChars(this.value) + "\r\n";
				}
				if (this.subsettings.size() == 0) {
					setXML = (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + ">") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + escapeSpecialChars(this.namespaceURI) + "\">");
					setXML += escapeSpecialChars(value);
					setXML += "</" + this.name + ">";
					return setXML;
				}
			} else if (isEmptyValue(this.value) && !hasSubsettings() && level != 1) {
				setXML = (this.namespaceURI == null) ? (getTabLevel() + "<" + this.name + getAttrXML() + "/>") : (getTabLevel() + "<" + this.name + getAttrXML() + " xmlns=\"" + escapeSpecialChars(this.namespaceURI) + "\"/>");
				return setXML;
			}
			setXML += (this.subsettings.size() > 0) ? (getSubXML() + "\r\n") : "";
			setXML += getTabLevel() + "</" + this.name + ">";					
		}
		return setXML;
	}
	
	private boolean isEmptyValue(String value) {
		String test = String.valueOf(value);
		return (test.replaceAll("\r\n", "").replaceAll("\t", "").isEmpty());
	}

	//Returns all subsettings of the setting in xml format
	public String getSubXML() {
		String subXML = "";
		for (Setting sub : this.subsettings) {
			subXML += sub.getXML() + "\r\n";
		}
		subXML = subXML.substring(0, (subXML.length() > 2) ? (subXML.length() - 2) : 0);
		return subXML;
	}

	//Returns all attributes of the setting in xml format
	public String getAttrXML() {
		String attrXML = " ";
		for (Map.Entry<String, String> attribute : this.attributes.entrySet()) {
			if (attribute.getValue() == null) {
				continue;
			}
			attrXML += attribute.getKey() + "=\"" + escapeSpecialChars(attribute.getValue()) + "\" ";
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

	//Returns the whole setting in json format
	public String printSetting() { 
		return "{\n\t\"level\"=\"" + this.level + "\",\n\t\"settingID\"=\"" + this.sID + "\",\n\t\"name\"=\"" + this.name + "\",\n\t\"value\"=\"" + String.valueOf(this.value) + "\",\n\t\"attributes\"=" + printAttributes() + ",\n\t\"subsettings\"= " + printSubsettings() + "}"; 
	}
	
	//Returns all subsettings of the setting in json format
	public String printSubsettings() { 
		String subsettingsString = "[\n";
		for (Setting subsetting : this.subsettings) {
			subsettingsString += "\t" + subsetting.printSetting() + "\n";
		} 
		subsettingsString += "]"; 
		return subsettingsString;
	}
	
	//Returns all attributes of the setting in json format
	public String printAttributes() { 
		String attributeString = "[\n"; 
		for (Map.Entry<String, String> attribute : this.attributes.entrySet()) {
			attributeString += "\t{\"name\"=\"" + attribute.getKey() + "\",\"value\"=\"" + attribute.getValue() + "\"},\n"; 
		}
		if (attributeString.length() > 1) {
			attributeString = attributeString.substring(0, attributeString.length() - 2) + "\n";
		}
		attributeString += "]"; return attributeString;
	}
	
	//Returns all Settings with a specified name (which are on the same level)
	public ArrayList<Setting> getSettings(String name) {
		return getSettingsSub(name, false);
	}
	
	public ArrayList<Setting> getSettingsSub(String name, boolean matchMe) {
		ArrayList<Setting> results = new ArrayList<Setting>();
		if (matchMe && this.name.equals(name)) {
			results.add(this);
			return results;
		}
		for (int i = 0; i < this.subsettings.size(); i++) {
			if (this.subsettings.get(i).name.equals(name)) {
				results.add(this.subsettings.get(i));
			}
		}
		for (int i = 0; i < this.subsettings.size(); i++) {
			results.addAll(this.subsettings.get(i).getSettingsSub(name, false));				
		}
		int lockLevel = -1;
		if (results.size() > 0 && !levelEquality(results)) {
			lockLevel = results.get(0).getLevel();
			for (Iterator<Setting> ite = results.iterator(); ite.hasNext();) {
				Setting subject = ite.next();
				
				if (subject.getLevel() != lockLevel) {
					ite.remove();
				}
			}
		}
		return results;
	}
	
	//Returns whether the setting has a setting of a specified name
	public boolean hasSetting(String name) {
		return (this.getSettingsSub(name, false).size() > 0);
	}
	
	//Returns whether the setting has the specified attribute key
	public boolean hasAttribute(String name) {
		return this.attributes.containsKey(name);
	}
	
	//Returns the value of the defined attribute name
	public String getAttribute(String name) {
		if (this.attributes.containsKey(name)) {
			return this.attributes.get(name);
		}
		return null;
	}
	
	public void setAttribute(String name, String value) {
		this.attributes.put(name, value);
	}
	
	//Returns whether a list of settings has duplicate levels
	public boolean levelEquality(ArrayList<Setting> list) {
		ArrayList<Integer> levels = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (levels.contains(list.get(i).level)) {
				return false;
			} else {
				levels.add(list.get(i).level);
			}
		}
		return true;
	}
	
	//Returns whether replacing the one setting with another one identified by the sID worked
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
	
	//Adds a custom setting this setting on the level below and return the setting which has been added
	public Setting addSetting(String name, String value, HashMap<String, String> attributes) {
		attributes = (attributes == null) ? new HashMap<String, String>() : attributes;
		value = (value == null) ? "" : value;
		Setting ns = new Setting(name, value, attributes, this.level + 1);
		this.subsettings.add(ns);
		return ns;
	}
	
	//Removes the specified setting id and returns the successfulness of the operation
	public boolean removeSetting(int sID) {
		if (sID < Setting.sIDState && this.sID != sID) {
			for (int i = 0; i < this.subsettings.size(); i++) {
				if (this.subsettings.get(i).sID == sID) {
					this.subsettings.remove(i);
					return true;
				}
			}
			for (Setting sub : this.subsettings) {
				if (sub.removeSetting(sID)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//Disables the specified setting id and returns the successfulness of the operation
	public boolean disableSetting(int sID) {
		if (this.sID == sID) {
			this.disable();
			return true;
		}
		if (sID < Setting.sIDState) {
			for (int i = 0; i < this.subsettings.size(); i++) {
				if (this.subsettings.get(i).sID == sID) {
					this.subsettings.get(i).disable();;
					return true;
				}
			}
			for (Setting sub : this.subsettings) {
				if (sub.disableSetting(sID)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//Reset this setting to an empty setting with only a name defined
	public void resetSetting(String name) {
		if (this.level == 1 && this.isEmpty) {
			this.name = name;
			this.attributes = new HashMap<String, String>();
			this.namespaceURI = null;
			this.value = "";
			this.node = null;
			this.subsettings = new ArrayList<Setting>();
			this.functions = new SettingFunctions();
			this.corrupt = false;
			this.isEmpty = false;
			this.enabled = true;
		}
	}
	
	public String escapeSpecialChars(String input) {
		String output = input.replaceAll("\"", "&quot;");
		output = output.replaceAll("&", "&amp;");
		output = output.replaceAll("'", "&apos;");
		output = output.replaceAll("<", "&lt;");
		output = output.replaceAll(">", "&gt;");
		return output;
	}
}
