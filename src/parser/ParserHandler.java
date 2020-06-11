package parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import settings.Setting;
import settings.SettingHandler;

public class ParserHandler {
	
	private static HashMap<String, Parser> parsers = new HashMap<String, Parser>();
	private static Setting parserMasterSetting;
	private static final String stdGetParser = "<Parser id=\"" + SettingHandler.PARSERHANDLERID + "\" name=\"Standard GET Parser\"> <>";
	
	public static HashMap<String, String> parse(String parserID, String input, HashMap<String, String> parsedHeader) {
		return ParserHandler.parsers.get(parserID).parse(input, parsedHeader);
	}
	
	public static void init(Setting parserMasterSetting) {
		ParserHandler.parserMasterSetting = parserMasterSetting;
		for (Setting parser : parserMasterSetting.getSubsettings()) {
			if (!parser.isEnabled()) {
				continue;
			}
			boolean success = false;
			String id = parser.getAttribute("id");
			String name = parser.getAttribute("name");
			ArrayList<String> order = new ArrayList<String>(Arrays.asList(parser.getAttribute("order").split(",")));
			HashMap<String, Rule> rules = new HashMap<String, Rule>();
			for (Setting rule : parser.getSettings("Rules").get(0).getSettings("Rule")) {
				success = false;
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
					reportError("rule id not set in rules of parser " + id + " " + name, true);
					break;
				}
				try {
					Method createRule = Class.forName(constructorArgs.get("type")).getDeclaredMethod("genRule", HashMap.class);
					Constructor<?> tempObjC = Class.forName(constructorArgs.get("type")).getConstructor();
					Object tempObj = tempObjC.newInstance();
					Rule newRule = (Rule) createRule.invoke(tempObj, constructorArgs);
					if (newRule != null) {
						rules.put(constructorArgs.get("id"), newRule);
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
				ParserHandler.parsers.put(id, new Parser(rules, order));
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
	
	public static ArrayList<String> getLog(String id) {
		return ParserHandler.parsers.get(id).printLog();
	}
}
