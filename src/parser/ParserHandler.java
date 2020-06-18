package parser;

import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import gui.ListElement;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import gui.ParserGUIPanel;
import settings.Setting;
import settings.SettingHandler;

public class ParserHandler {
	
	private static HashMap<String, Parser> parsers = new HashMap<String, Parser>();
	private static Setting parserMasterSetting;

	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String ORDERNAME = "order";
	private static final String INDEXASSIGNERNAME = "indexAssigners";
	private static final String TYPENAME = "type";

	private static final String RULESNAME = "Rules";
	private static final String RULENAME = "Rule";
	private static final String SETTINGNAME = "Parser";
	
	public static HashMap<String, String> parse(String parserID, String input, HashMap<String, String> parsedHeader) {
		return ParserHandler.parsers.get(parserID).parse(input, parsedHeader);
	}
	
	public static void init(Setting parserMasterSetting) {
		ParserHandler.parsers.put(SettingHandler.GETPARSERID, new StdGetParser());
		ParserHandler.parserMasterSetting = parserMasterSetting;
		for (Setting parser : parserMasterSetting.getSettings(ParserHandler.SETTINGNAME)) {
			if (!parser.isEnabled()) {
				continue;
			}
			boolean success = false;
			String id = parser.getAttribute(ParserHandler.IDNAME);
			String name = parser.getAttribute(ParserHandler.NAMENAME);
			ArrayList<String> indexAssigners = (parser.getAttribute(ParserHandler.INDEXASSIGNERNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(parser.getAttribute(ParserHandler.INDEXASSIGNERNAME).split(",")));
			ArrayList<String> order = (parser.getAttribute(ParserHandler.ORDERNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(parser.getAttribute(ParserHandler.ORDERNAME).split(",")));
			ConcurrentHashMap<String, Rule> rules = new ConcurrentHashMap<String, Rule>();
			for (Setting rule : parser.getSettings("Rules").get(0).getSettings(ParserHandler.RULENAME)) {
				if (!rule.isEnabled()) {
					continue;
				}
				success = false;
				HashMap<String, String> attributes = rule.getAttributes();
				HashMap<String, String> constructorArgs = new HashMap<String, String>();
				for (Map.Entry<String, String> attribute : attributes.entrySet()) {
					constructorArgs.put(attribute.getKey(), attribute.getValue());
				}
				if (!constructorArgs.containsKey(ParserHandler.TYPENAME)) {
					reportError("rule type not set in rules of parser " + id + " "+ name, true);
					break;
				}
				
				if (!constructorArgs.containsKey(ParserHandler.IDNAME)) {
					reportError("rule id not set in rules of parser " + id + " " + name, true);
					break;
				}
				try {
					Method createRule = Class.forName(constructorArgs.get(ParserHandler.TYPENAME)).getDeclaredMethod("genRule", HashMap.class);
					Constructor<?> tempObjC = Class.forName(constructorArgs.get("type")).getConstructor();
					Object tempObj = tempObjC.newInstance();
					Rule newRule = (Rule) createRule.invoke(tempObj, constructorArgs);
					if (newRule != null) {
						rules.put(constructorArgs.get(IDNAME), newRule);
					} else {
						break;
					}
				} catch (ClassNotFoundException | LinkageError e) {
					reportError("rule type could not be resolved in Parser " + id + " " + name, true);
					break;
				} catch (NoSuchMethodException | SecurityException e) {
					reportError("rule type found, but method \"genRule\" couldn't be found (May also be due to a missing empty constructor)",e.getMessage(), true);
					break;
				} catch (IllegalAccessException e) {
					reportError("rule type found, but method \"genRule\" couldn't be accessed",e.getMessage(), true);
					break;
				} catch (IllegalArgumentException e) {
					reportError("rule type found, but method \"genRule\" does not require the interface defined parameters",e.getMessage(), true);
					break;
				} catch (InvocationTargetException e) {
					reportError("rule type found, but method \"genRule\" couldn't be invoked",e.getMessage(), true);
					break;
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				success = true;
			}
			if (success) {
				ParserHandler.parsers.put(id, new CustomParser(rules, indexAssigners, order, name, id));
			}
		}
	}
	
	private static void reportError(String cause, boolean onInit) {
		String message = (onInit) ? "Parser creation failed with " + cause : "Parsing failed with " + cause;
		String elements[] = {"ID", "Origin", "Cause", "SettingRelated"};
		String values[] = {SettingHandler.PARSERHANDLERID, MessageOrigin.ParserHandler.name(), cause, String.valueOf(onInit)};
		Logger.addMessage(MessageType.Error, MessageOrigin.ParserHandler, message, SettingHandler.PARSERHANDLERID, elements, values, true);
	}
	
	private static void reportError(String cause, String errorMessage, boolean onInit) {
		String message = (onInit) ? "Parser creation reported " + errorMessage + "because of " + cause : "Parsing failed with " + errorMessage;
		String elements[] = {"ID", "Origin", "Cause", "SettingRelated", "ErrorMessage"};
		String values[] = {SettingHandler.PARSERHANDLERID, MessageOrigin.ParserHandler.name(), cause, String.valueOf(onInit), errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.ParserHandler, message, SettingHandler.PARSERHANDLERID, elements, values, true);		
	}
	
	public static void reportGenRuleError(String missingName, String ruleType, String id) {
		String message = "Rule creation of "+ ruleType + " " + id + " rule failed, because " + missingName + " is missing or incorrectly formatted";
		String elements[] = {"ID", "Origin", "Missing", "RuleType", "RuleID"};
		String values[] = {SettingHandler.PARSERHANDLERID, MessageOrigin.ParserHandler.name(), missingName, ruleType, id};
		Logger.addMessage(MessageType.Error, MessageOrigin.ParserHandler, message, SettingHandler.PARSERHANDLERID, elements, values, true);
	}
	
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isShort(String string) {
		try {
			Short.parseShort(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static boolean isBoolean(String string) {
		if (string.toLowerCase().equals("true") || string.toLowerCase().equals("false")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String returnStringIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			return null;
		}
	}
	
	public static Integer returnIntIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name) && isInt(map.get(name))) {
			return Integer.parseInt(map.get(name));
		} else {
			return null;
		}
	}
	
	public static String[] returnStringArrayIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name)) {
			return (map.get(name).isBlank()) ? new String[] {} : map.get(name).split(",");
		} else {
			return null;
		}
	}
	
	public static Boolean returnBooleanIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name) && isBoolean(map.get(name))) {
			return Boolean.parseBoolean(map.get(name));
		} else {
			return null;
		}
	}
	
	public static List<String> getLog(String id) {
		return ParserHandler.parsers.get(id).printLog();
	}

	public static void close() {
		for (Setting parserSetting : ParserHandler.parserMasterSetting.getSettings(ParserHandler.SETTINGNAME)) {
			if (!parserSetting.isEnabled()) {
				continue;
			}
			String id = parserSetting.getAttribute(ParserHandler.IDNAME);
			if (ParserHandler.parsers.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				Parser preParser = ParserHandler.parsers.get(id);
				if (!(preParser instanceof CustomParser)) {
					continue;
				}
				CustomParser parser = (CustomParser) preParser;
				newAttributes.put(ParserHandler.IDNAME, id);
				newAttributes.put(ParserHandler.NAMENAME, parser.getName());
				newAttributes.put(ParserHandler.ORDERNAME, SettingHandler.alts(parser.getOrder()));
				newAttributes.put(ParserHandler.INDEXASSIGNERNAME, SettingHandler.alts(parser.getIndexAssigners()));
				parserSetting.addReplaceAttributes(newAttributes);
				Setting rulesSetting = parserSetting.getSettings(ParserHandler.RULESNAME).get(0);
				ConcurrentHashMap<String, Rule> rules = parser.getElements();
				HashMap<String, Boolean> ruleMatches = SettingHandler.getMatchList(rules.keySet(), false);
				for (Setting ruleSetting : rulesSetting.getSettings(ParserHandler.RULENAME)) {
					if (!ruleSetting.isEnabled()) {
						continue;
					}
					String ruleID = ruleSetting.getAttribute(ParserHandler.IDNAME);
					if (rules.containsKey(ruleID)) {
						Rule rule = rules.get(ruleID);
						HashMap<String, String> newRuleAttributes = new HashMap<String, String>();
						newRuleAttributes.putAll(rule.storeRule());
						newRuleAttributes.put(ParserHandler.TYPENAME, rule.getClass().getName());
						newRuleAttributes.put(ParserHandler.IDNAME, ruleID);
						ruleSetting.addReplaceAttributes(newRuleAttributes);
						ruleMatches.put(ruleID, true);
					}
				}
				ArrayList<String> missingRules = SettingHandler.getConditionalList(ruleMatches, false);
				for (String ruleID : missingRules) {
					Rule rule = rules.get(ruleID);
					HashMap<String, String> ruleAttributes = new HashMap<String, String>();
					ruleAttributes.putAll(rule.storeRule());
					ruleAttributes.put(ParserHandler.IDNAME, ruleID);
					ruleAttributes.put(ParserHandler.TYPENAME, rule.getClass().getName());
					rulesSetting.addSetting(ParserHandler.RULENAME, null, ruleAttributes);
				}
			}
		}
	}
	
	public static void addParser(CustomParser parser) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(ParserHandler.IDNAME, parser.getID());
		attributes.put(ParserHandler.NAMENAME, parser.getName());
		attributes.put(ParserHandler.ORDERNAME, SettingHandler.alts(parser.getOrder()));
		attributes.put(ParserHandler.INDEXASSIGNERNAME, SettingHandler.alts(parser.getIndexAssigners()));
		Setting parserSetting = ParserHandler.parserMasterSetting.addSetting(ParserHandler.SETTINGNAME, null, attributes);
		Setting rulesSetting = parserSetting.addSetting(ParserHandler.RULESNAME, null, null);
		ConcurrentHashMap<String, Rule> rules = parser.getElements();
		for (Entry<String, Rule> rule : rules.entrySet()) {
			HashMap<String, String> ruleAttributes = new HashMap<String, String>();
			ruleAttributes.putAll(rule.getValue().storeRule());
			ruleAttributes.put(ParserHandler.IDNAME, rule.getKey());
			ruleAttributes.put(ParserHandler.TYPENAME, rule.getValue().getClass().getName());
			rulesSetting.addSetting(ParserHandler.RULENAME, null, ruleAttributes);
		}
	}
	
	public static CustomParser getCustomParser(String id) {
		return (ParserHandler.parsers.containsKey(id) && ParserHandler.parsers.get(id) instanceof CustomParser) ? (CustomParser) ParserHandler.parsers.get(id) : null;
	}
	
	public static void removeRule(String parserID, String ruleID) {
		SettingHandler.removeElement(parserID, ruleID, ParserHandler.IDNAME, ParserHandler.SETTINGNAME, ParserHandler.RULESNAME, ParserHandler.RULENAME, ParserHandler.parserMasterSetting);
	}
	
	public static void removeParser(String id) {
		if (SettingHandler.removeParent(id, ParserHandler.IDNAME, ParserHandler.SETTINGNAME, ParserHandler.parserMasterSetting)) {
			ParserHandler.parsers.remove(id);
		}
	}
	
	public static String getParserName(String parserID) {
		return ParserHandler.parsers.get(parserID).getName();
	}
	
	public static ListElement[] getParserElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Parser> kvp : ParserHandler.parsers.entrySet()) {
			elements.add(new ListElement(kvp.getKey(), kvp.getValue().getName(), kvp.getValue()));
		}
		return elements.toArray(new ListElement[elements.size()]);
	}

	public static List<ParserGUIPanel> getParserPanels() {
		ArrayList<ParserGUIPanel> panels = new ArrayList<ParserGUIPanel>();
		for (Entry<String, Parser> parsers : ParserHandler.parsers.entrySet()) {
			if (parsers.getValue() instanceof CustomParser) {
				ParserGUIPanel panel = new ParserGUIPanel();
				panel.init((CustomParser) parsers.getValue());
				panels.add(panel);
			}
		}
		return panels;
	}
}
