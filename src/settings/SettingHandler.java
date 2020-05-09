package settings;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.Pair;
import filehandler.Manager;
import group.GroupHandler;
import gui.Logger;
import gui.Main;
import gui.MessageOrigin;
import gui.MessageType;
import parser.ParserHandler;
import trigger.TriggerHandler;

//This class sets up all setting parsing, checks the syntax and forwards the setting processing to the corresponding classes ()
public class SettingHandler {
	
	private static int fileID;
	private static final String FILENAME = "settings.xml";
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis";
	private static final String BASESETTING = "InterfaceOasis";
	private static Setting masterSetting;
	private static Setting groupMasterSetting;
	private static Setting triggerMasterSetting;
	private static Setting ParserMasterSetting;
	
	public static final String FILEHANDLERID = "000000";
	public static final String GROUPHANDLERID = "000001";
	public static final String SETTINGHANDLERID = "000002";
	public static final String SETTINGPARSINGID = "000003";
	public static final String PARSERHANDLERID = "000004";
	public static final String FILEHANDLERNAME = "File Handler";
	public static final String GROUPHANDLERNAME = "Group Handler";
	public static final String SETTINGHANDLERNAME = "Setting Handler";
	public static final String SETTINGPARSINGNAME = "Setting Parser";
	public static final String PARSERHANDLERNAME = "Parser Handler";
	
	public static void init() {
		
		fileID = Manager.newFile(Manager.checkPath(PATH) + Manager.SEPERATOR + FILENAME);
		
		if (fileID == -1) {
			reportError("Missing Setting File", "All actions will not be saved");
			return;
		}
		
		SettingHandler.masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);
		boolean alteredInformation = false;
		boolean wasEmpty = false;
		
		if (masterSetting.reset()) {
			resetInformation("No setting found - resetting to default");
			masterSetting.resetSetting(BASESETTING);
			alteredInformation = true;
			wasEmpty = true;
		}
		
		if (!masterSetting.hasSetting("Groups") || masterSetting.getSettings("Groups").get(0).getLevel() != 2) {
			resetInformation("No Groups found - Resetting to default");
			masterSetting.addSetting("Groups", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("Triggers") || masterSetting.getSettings("Triggers").get(0).getLevel() != 2) {
			resetInformation("No Triggers found - Resetting to default");
			masterSetting.addSetting("Triggers", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("Parsers") || masterSetting.getSettings("Parsers").get(0).getLevel() != 2) {
			resetInformation("No Parsers found - resetting to default");
			masterSetting.addSetting("Parsers", null, null);
			alteredInformation = true;
		}
		
		
		if (alteredInformation && !wasEmpty) {
			if (Main.askMessage("Due to missing base settings your settings have been altered - Do you want to create a backup of your old setting file?", "Backup Setting File") == 0) {
				Manager.copyFile(fileID);				
			}
		}
		
		checkSettingStructure("Groups");
		checkSettingStructure("Triggers");
		checkSettingStructure("Parsers");
		
		Setting tempGroupMasterSetting = masterSetting.getSettings("Groups").get(0);
		Setting tempTriggerMasterSetting = masterSetting.getSettings("Triggers").get(0);
		Setting tempParserMasterSetting = masterSetting.getSettings("Parsers").get(0);
		
		ArrayList<Integer> removeGroupIndexes = new ArrayList<Integer>();
		ArrayList<Integer> removeTriggerIndexes = new ArrayList<Integer>();
		ArrayList<Integer> removeParserIndexes = new ArrayList<Integer>();
		
		for (int i = 0; i < tempGroupMasterSetting.getSubsettings().size(); i++) {
			if (!checkGroupSetting(tempGroupMasterSetting.getSubsettings().get(i), i)) {
				removeGroupIndexes.add(i);
			}
		}
		
		for (int i = 0; i < tempTriggerMasterSetting.getSubsettings().size(); i++) {
			if (!checkTriggerSetting(tempTriggerMasterSetting.getSubsettings().get(i), i)) {
				removeTriggerIndexes.add(i);
			}
		}
		
		for (int i = 0; i < tempParserMasterSetting.getSubsettings().size(); i++) {
			if (checkParserSetting(tempParserMasterSetting.getSubsettings().get(i), i)) {
				removeParserIndexes.add(i);
			}
		}
		
		for (Integer index : removeGroupIndexes) {
			tempGroupMasterSetting.removeSetting(index);
		}
		
		for (Integer index : removeTriggerIndexes) {
			tempTriggerMasterSetting.removeSetting(index);
		}
		
		for (Integer index : removeParserIndexes) {
			tempParserMasterSetting.removeSetting(index);
		}
		
		SettingHandler.groupMasterSetting = tempGroupMasterSetting;
		SettingHandler.triggerMasterSetting = tempTriggerMasterSetting;
		SettingHandler.ParserMasterSetting = tempParserMasterSetting;
		
		GroupHandler.init(SettingHandler.groupMasterSetting);
		TriggerHandler.init(SettingHandler.triggerMasterSetting);
		ParserHandler.init(SettingHandler.ParserMasterSetting);
		
	}
	
	public static void close() {
		try {
			Manager.writeFile(fileID, masterSetting.getXML(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createSetting(String name, String value, ArrayList<Pair<String, String>> attributes) {
		masterSetting.addSetting(name, value, attributes);
	}
	
	public static ArrayList<Setting> getSettings(String name) {
		return masterSetting.getSettings(name);
	}
	
	public static void replaceSetting(Setting setting) {
		masterSetting.replaceID(setting.getID(), setting);
	}
	
	private static void resetInformation(String message) {
		Logger.addMessage(MessageType.Information, MessageOrigin.Settings, message, SettingHandler.SETTINGHANDLERID, null, null, false);
	}
	
	private static void reportError(String message, String causes) {
		String objectMessage = message + " - " + causes;
		String[] elements = {"GroupID", "GroupName", "Message", "Causes"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, message, causes};
		Logger.addMessage(MessageType.Error, MessageOrigin.Settings, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, true);
	}
	
	private static void checkSettingStructure(String name) {
		if (SettingHandler.masterSetting.getSettings(name).size() != 1) {
			Logger.addMessage(MessageType.Warning, MessageOrigin.Settings, "Duplicate " + name + " setting - The second setting is ignored", SettingHandler.SETTINGHANDLERID, null, null, false);
		}
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message;
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	//Syntax Checkers
	private static boolean checkGroupSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Group")) {
			reportSyntaxError("Group Name Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return false;
		}
		int count = 0;
		String id = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
				case "id":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportSyntaxError("Group Attribute checker", "Invalid id value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						id = attribute.getValue();
						count++;
					}
				break;
			}
		}
		if (count != 2) {
			reportSyntaxError("Group Attribute checker", (2 - count) + " missing attribute(s) in " + ite + ". Group", false);
			return false;
		}
		boolean hadListener = false;
		boolean hadResponder = false;
		for (Setting group : checkMe.getSubsettings()) {
			if (group.getName().equals("Listeners")) {
				if (hadListener) {
					reportSyntaxError("Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true);
				}
				hadListener = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					if (!subject.getName().equals("Listener")) {
						reportSyntaxError("Group Listeners Element Checker", "Unknown Element \"" + subject.getName() + "\"", true);
						continue;
					}
					for (Pair<String, String> attribute : subject.getAttributes()) {
						switch (attribute.getKey()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportSyntaxError("Group Listener Attribute Checker", "Invalid name value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportSyntaxError("Group Listener Attribute Checker", "Invalid port value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportSyntaxError("Group Listener Attribute Checker", "Invalid id value \""  + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						default:
							reportSyntaxError("Group Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\"", true);
						}
					}
					if (attrCount != 3) {
						reportSyntaxError("Group Listener Attribute Checker", (3 - attrCount) + " Missing attribute(s)", true);
						return false;
					}
				}
			} else if (group.getName().equals("Responders")) {
				if (hadResponder) {
					reportSyntaxError("Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true); //TODO: Assignment of IDs, calling of handlers
				}
				hadResponder = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					switch (subject.getName()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportSyntaxError("Group Responder Value Checker", "Invalid name value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportSyntaxError("Group Responder Value Checker", "Invalid port value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportSyntaxError("Group Responder Value Checker", "Invalid id value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
					default:
						reportSyntaxError("Group Responder Value Checker", "Unknown Responder Element \"" + subject.getName() + "\"", true);
					}
				}
				if (attrCount != 3) {
					return false;
				}
			} else {
				reportSyntaxError("Group Master Element Checker", "Unknown Group Element \"" + group.getName() + "\"", true);
			}
		}
		return true;
	}
	
	public static boolean checkParserSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Parser")) {
			reportSyntaxError("Parser Name Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return false;
		}
		int count = 0;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
				case "id":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportSyntaxError("Parser Attribute checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
			}
		}
		if (count != 2) {
			reportSyntaxError("Parser Attribute checker", (2 - count) + " missing attribute(s) in " + ite + ". Parser", false);
			return false;
		}
		//Further syntax checking is not necessary. The parser consists of many rules which are independently checked by the corresponding rule
		return true;
	}
	
	public static boolean checkTriggerSetting(Setting checkMe, int ite) {
		//TODO: Develop trigger storage model
		return true;
	}
	
	public static boolean matchesRegex(String regex, String subject) {
		if (subject == null || regex == null) {
			return false;
		}
		Matcher matcher = Pattern.compile(regex).matcher(subject);
		return matcher.matches();
	}
}
