package parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import settings.Setting;
import settings.SettingHandler;
import xmlhandler.Trace;

public class ParserHandler {
	
	private static ArrayList<Parser> parsers = new ArrayList<Parser>();
	private static HashMap<String, Class> stdRules = new HashMap<String, Class>();
	
	private static final String getParser = "<Parser id=\"" + SettingHandler.PARSERHANDLERID + "\" name=\"Standard GET Parser\"> <>";
	
	public static void init(Setting parserMasterSetting) {
		initStdList();
		for (Setting parser : parserMasterSetting.getSubsettings()) {
			boolean success = false;
			String id = parser.getAttribute("id").getValue();
			String name = parser.getAttribute("name").getValue();
			ArrayList<Rule> rules = new ArrayList<Rule>();
			for (Setting rule : parser.getSettings("Rule")) {
				ArrayList<Pair<String, String>> attributes = rule.getAttributes();
				HashMap<String, String> constructorArgs = new HashMap<String, String>();
				for (Pair<String, String> attribute : attributes) {
					constructorArgs.put(attribute.getKey(), attribute.getValue());
				}
				if (!constructorArgs.containsKey("type")) {
					reportError("rule type not set in rules of parser " + id + " "+ name, true);
					break;
				}
				if (!ParserHandler.stdRules.containsKey(constructorArgs.get("type"))) {
					reportError("unkown rule type in rules of parser " + id + " " + name, true);
					break;
				}
				try {
					Method createRule = ParserHandler.stdRules.get(constructorArgs.get("type")).getDeclaredMethod("genRule", HashMap.class);
					Rule newRule = (Rule) createRule.invoke(null, constructorArgs);
					if (newRule != null) {
						rules.add(newRule);						
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
			if (success) {
				ParserHandler.parsers.add(new Parser(rules));
			}
		}
	}
	
	public static void initStdList() {
		ParserHandler.stdRules.put("Cut", Cut.class);
		ParserHandler.stdRules.put("Discard", Discard.class);
		ParserHandler.stdRules.put("Isolate", Isolate.class);
		ParserHandler.stdRules.put("Replace", Replace.class);
		ParserHandler.stdRules.put("Split", Split.class);
		ParserHandler.stdRules.put("Trace", Trace.class);
	}
	
	private static void reportError(String cause, boolean onInit) {
		String message = (onInit) ? "Parser creation failed with " + cause : "Parsing failed with " + cause;
		String elements[] = {"GroupID", "GroupName", "Cause", "SettingRelated"};
		String values[] = {SettingHandler.PARSERHANDLERID, SettingHandler.PARSERHANDLERNAME, cause, String.valueOf(onInit)};
		Logger.addMessage(MessageType.Error, MessageOrigin.Parser, message, SettingHandler.PARSERHANDLERID, elements, values, true);
	}
	
	private static void reportError(String cause, String errorMessage, boolean onInit) {
		String message = (onInit) ? "Parser creation reported " + errorMessage + "because of " + cause : "Parsing failed with " + errorMessage + " due to " + cause;
		String elements[] = {"GroupID", "GroupName", "Cause", "SettingRelated", "ErrorMessage"};
		String values[] = {SettingHandler.PARSERHANDLERID, SettingHandler.PARSERHANDLERNAME, cause, String.valueOf(onInit), errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.Parser, message, SettingHandler.PARSERHANDLERID, elements, values, true);		
	}
	
	public static void reportGenRuleError(String missingName, String ruleType) {
		String message = "Rule creation of "+ ruleType + " rule failed, because ";
		String elements[] = {"GroupID", "GroupName", "Cause", "RuleType"};
		String values[] = {SettingHandler.PARSERHANDLERID, SettingHandler.PARSERHANDLERNAME, cause, ruleType};
		Logger.addMessage(MessageType.Error, MessageOrigin.Parser, message, SettingHandler.PARSERHANDLERID, elements, values, true);
	}
	
	private static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
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
	
	protected static String returnStringIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			return null;
		}
	}
	
	protected static Integer returnIntIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name) && isInt(map.get(name))) {
			return Integer.parseInt(map.get(name));
		} else {
			return null;
		}
	}
	
	protected static String[] returnStringArrayIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name)) {
			return map.get(name).split(",");
		} else {
			return null;
		}
	}
	
	protected static Boolean returnBooleanIfExists(HashMap<String, String> map, String name) {
		if (map.containsKey(name) && isBoolean(map.get(name))) {
			return Boolean.parseBoolean(map.get(name));
		} else {
			return null;
		}
	}

}
