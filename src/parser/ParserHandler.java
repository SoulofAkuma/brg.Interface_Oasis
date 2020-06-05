package parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import settings.Setting;
import settings.SettingHandler;

public class ParserHandler {
	
	private static HashMap<String, Parser> parsers = new HashMap<String, Parser>();
	private static HashMap<String, String> indexAssigners = new HashMap<String, String>();
	@SuppressWarnings("rawtypes")
	private static ConcurrentHashMap<String, Class> stdRules = new ConcurrentHashMap<String, Class>();
	
	private static final String stdGetParser = "<Parser id=\"" + SettingHandler.PARSERHANDLERID + "\" name=\"Standard GET Parser\"> <>";
	
	public static Parser getParser(String parserID) {
		return ParserHandler.parsers.get(parserID);
	}
	
	public static void init(Setting parserMasterSetting) {
		initStdList();
		for (Setting parser : parserMasterSetting.getSubsettings()) {
			boolean success = false;
			String id = parser.getAttribute("id");
			String name = parser.getAttribute("name");
			ArrayList<String> order = new ArrayList<String>(Arrays.asList(parser.getAttribute("order").split(",")));
			HashMap<String, Rule> rules = new HashMap<String, Rule>();
			for (Setting rule : parser.getSettings("Rule")) {
				if (rule.getName().equals("Rule") && parser.getID() - rule.getLevel() == 2) {
					HashMap<String, String> attributes = rule.getAttributes();
					HashMap<String, String> constructorArgs = new HashMap<String, String>();
					for (Map.Entry<String, String> attribute : attributes.entrySet()) {
						constructorArgs.put(attribute.getKey(), attribute.getValue());
					}
					if (!constructorArgs.containsKey("type")) {
						reportError("rule type not set in rules of parser " + id + " "+ name, true);
						break;
					}
					
					if (!constructorArgs.containsKey("id")) {
						reportError("rule type not set in rules of parser " + id + " " + name, true);
						break;
					}
					if (!ParserHandler.stdRules.containsKey(constructorArgs.get("type"))) {
						reportError("unkown rule type in rules of parser " + id + " " + name, true);
						break;
					}
					try {
						@SuppressWarnings("unchecked")
						Method createRule = ParserHandler.stdRules.get(constructorArgs.get("type")).getDeclaredMethod("genRule", HashMap.class);
						Rule newRule = (Rule) createRule.invoke(null, constructorArgs);
						if (newRule != null) {
							rules.put(constructorArgs.get("id"), newRule);						
						} else {
							break;
						}
					} catch (NoSuchMethodException | SecurityException e) {
						reportError("rule type found, but method \"genRule\" couldn't be found",e.getMessage(), true);
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
					}
				}
			}
			if (success) {
				ParserHandler.parsers.put(id, new Parser(rules, order));
			}
		}
	}
	
	public static void initStdList() {
		ParserHandler.stdRules.put("Cut", Cut.class);
		ParserHandler.stdRules.put("Discard", Discard.class);
		ParserHandler.stdRules.put("Isolate", Isolate.class);
		ParserHandler.stdRules.put("Replace", Replace.class);
		ParserHandler.stdRules.put("Split", Split.class);
		ParserHandler.stdRules.put("XMLTrace", xmlhandler.Trace.class);
		ParserHandler.stdRules.put("JSONTrace", jsonhandler.Trace.class);
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
	
	public static void reportGenRuleError(String missingName, String ruleType) {
		String message = "Rule creation of "+ ruleType + " rule failed, because " + missingName + "is missing or incorrectly formatted";
		String elements[] = {"ID", "Origin", "Missing", "RuleType"};
		String values[] = {SettingHandler.PARSERHANDLERID, MessageOrigin.ParserHandler.name(), missingName, ruleType};
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
			return map.get(name).split(",");
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

}
