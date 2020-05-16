package settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.Pair;
import constant.ConstantHandler;
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
	
	private static int fileID; //ID of the setting file for the file handler
	private static final String FILENAME = "settings.xml"; //Name of the setting file
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis"; //Base program folder path
	private static final String BASESETTING = "InterfaceOasis"; //Name of the root Element in the xml setting
	private static Setting masterSetting; //The content of the setting file parsed into a setting
	private static Setting groupMasterSetting; //The master group setting element derived from the master setting
	private static Setting triggerMasterSetting; //The master trigger setting element derived from the master setting
	private static Setting parserMasterSetting; //The master parser setting element derived from the master setting
	private static Setting constantMasterSetting; //The master constant setting element derived from the master setting
	private static Setting backupSetting; //A backup of the original master setting after the basic setup (initialization of the other master settings)
	private static boolean altered = false; //Indicates whether the individual master settings have ever been altered (Removed corrupt elements)
	
	public static final String FILEHANDLERID = "000000"; //Reserved GroupID for the FileHandler (for error reports)
	public static final String GROUPHANDLERID = "000001"; //Reserved GroupID for the GroupHandler(for error reports)
	public static final String SETTINGHANDLERID = "000002"; //Reserved GroupID for the SettingHandler (for error reports)
	public static final String SETTINGPARSINGID = "000003"; //Reserved GroupID for the Setting parsing process (for error reports)
	public static final String PARSERHANDLERID = "000004"; //Reserved GroupID for the ParserHandler (for error reports)
	public static final String FILEHANDLERNAME = "File Handler"; //Reserved Group Name for the FileHandler (for error reports)
	public static final String GROUPHANDLERNAME = "Group Handler"; //Reserved Group Name for the GroupHandler (for error reports)
	public static final String SETTINGHANDLERNAME = "Setting Handler"; //Reserved Group Name for the SettingHandler (for error reports)
	public static final String SETTINGPARSINGNAME = "Setting Parser"; //Reserved Group Name for the Setting parsing process (for error reports)
	public static final String PARSERHANDLERNAME = "Parser Handler"; //Reserved Group Name for the ParserHandler (for error reports)
	
	public static final ConcurrentHashMap<String, ArrayList<String>> RESERVEDID = new ConcurrentHashMap<String, ArrayList<String>>(); //A multithread HashMap to check whether an element is using a reserved id of the corresponding program area (non interfering duplicate IDs are allowed)
	
	public static final String REGEXNAME = SettingHandler.REGEXNAME; //Regex for names defined in attributes
	public static final String REGEXID = "[0-9]{6}"; //Regex for ids defined in attributes
	public static final String REGEXPORT = "[0-9]+"; //Regex for ports defined in attributes (the port size is unrestricted, so this program can be used on machine with custom defined port numbers)
	public static final String REGEXBOOL = "(true|false)"; //Regex for boolean values
	
	private static HashMap<String, Boolean> missingTable; //Reusable for attribute checking
	
	public static void init() {
		
		initReserved();
		
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
		
		if (!masterSetting.hasSetting("Constants") || masterSetting.getSettings("Constants").get(0).getLevel() != 2) {
			resetInformation("No Constants found - resetting to default");
			masterSetting.addSetting("Constants", null, null);
			alteredInformation = true;
		}
		
		
		if (alteredInformation && !wasEmpty) {
			if (Main.askMessage("Due to missing base settings your settings have been altered - Do you want to create a backup of your old setting file?", "Backup Setting File") == 0) {
				Manager.copyFile(fileID);				
			}
		}
		SettingHandler.backupSetting = SettingHandler.masterSetting.getBackup();
		initSetting(SettingHandler.masterSetting);
	}
	
	public static void resetToBackup() {
		GroupHandler.close();
		initSetting(SettingHandler.backupSetting);
	}
	
	public static void initSetting(Setting setting) {
		checkSettingStructure("Groups");
		checkSettingStructure("Triggers");
		checkSettingStructure("Parsers");
		checkSettingStructure("Constants");
		
		
		Setting tempGroupMasterSetting = masterSetting.getSettings("Groups").get(0);
		Setting tempTriggerMasterSetting = masterSetting.getSettings("Triggers").get(0);
		Setting tempParserMasterSetting = masterSetting.getSettings("Parsers").get(0);
		Setting tempConstantMasterSetting = masterSetting.getSettings("Constants").get(0);
		
		ArrayList<Integer> removeGroupIndexes = new ArrayList<Integer>();
		ArrayList<Integer> removeTriggerIndexes = new ArrayList<Integer>();
		ArrayList<Integer> removeParserIndexes = new ArrayList<Integer>();
		ArrayList<Integer> removeConstantIndexes = new ArrayList<Integer>();
		
		for (int i = 0; i < tempGroupMasterSetting.getSubsettings().size(); i++) {
			int settingID = tempGroupMasterSetting.getSubsettings().get(i).getID();
			Setting finalSetting;
			if ((finalSetting = checkGroupSetting(tempGroupMasterSetting.getSubsettings().get(i), i)) != null) {
				tempGroupMasterSetting.replaceID(settingID, finalSetting);
			} else {
				removeGroupIndexes.add(i);
			}
		}
		
		for (int i = 0; i < tempTriggerMasterSetting.getSubsettings().size(); i++) {
			int settingID = tempGroupMasterSetting.getSubsettings().get(i).getID();
			Setting finalSetting;
			if ((finalSetting = checkTriggerSetting(tempTriggerMasterSetting.getSubsettings().get(i), i)) != null) {
				tempGroupMasterSetting.replaceID(settingID, finalSetting);
			} else {
				removeGroupIndexes.add(i);
			}
		}
		
		for (int i = 0; i < tempParserMasterSetting.getSubsettings().size(); i++) {
			int settingID = tempParserMasterSetting.getSubsettings().get(i).getID();
			Setting finalSetting;
			if ((finalSetting = checkParserSetting(tempParserMasterSetting.getSubsettings().get(i), i)) != null) {
				tempParserMasterSetting.replaceID(settingID, finalSetting);
			} else {
				removeParserIndexes.add(i);
			}
		}
		
		for (int i = 0; i < tempConstantMasterSetting.getSubsettings().size(); i++) {
			int settingID = tempConstantMasterSetting.getSubsettings().get(i).getID();
			Setting finalSetting;
			if ((finalSetting = checkConstantSetting(tempConstantMasterSetting.getSubsettings().get(i), i)) != null) {
				tempConstantMasterSetting.replaceID(settingID, finalSetting);
			} else {
				removeConstantIndexes.add(i);
			}
		}
		
		if (altered || removeGroupIndexes.size() > 0 || removeTriggerIndexes.size() > 0 || removeParserIndexes.size() > 0) {
			if (Main.askMessage("Due to errors in the current setting definitions, your settings have been altered. All error information can be found in the logging messages - Do you want to create a backup of your old Setting file?", "Setting File Error") == 0) {
				Manager.copyFile(SettingHandler.fileID);
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
		
		for (Integer index : removeConstantIndexes) {
			tempConstantMasterSetting.removeSetting(index);
		}
		
		SettingHandler.groupMasterSetting = tempGroupMasterSetting;
		SettingHandler.triggerMasterSetting = tempTriggerMasterSetting;
		SettingHandler.parserMasterSetting = tempParserMasterSetting;
		SettingHandler.constantMasterSetting = tempConstantMasterSetting;
		
		GroupHandler.init(SettingHandler.groupMasterSetting);
		TriggerHandler.init(SettingHandler.triggerMasterSetting);
		ParserHandler.init(SettingHandler.parserMasterSetting);
		ConstantHandler.init(SettingHandler.constantMasterSetting);
	}
	
	public static void initReserved() {
		ArrayList<String> group = new ArrayList<String>();
		group.add("000000"); //FileHandler
		group.add("000001"); //GroupHandler
		group.add("000002"); //SettingHandler
		group.add("000003"); //SettingFunctions
		group.add("000004"); //ParserHandler
		SettingHandler.RESERVEDID.put("Group", group);
		ArrayList<String> parser = new ArrayList<String>();
		parser.add("000000"); //Standard GET Parser
		SettingHandler.RESERVEDID.put("Parser", parser);
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
	
	private static void reportSyntaxError(String source, String message, boolean isWarning) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message;
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String groupID) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (GroupID: " + groupID +")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (Element Iteration: " + String.valueOf(iteration)+")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String groupID, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (GroupID: " + groupID +"Element Iteration: " + String.valueOf(iteration) + ")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void checkSettingStructure(String name) {
		if (SettingHandler.masterSetting.getSettings(name).size() != 1) {
			Logger.addMessage(MessageType.Warning, MessageOrigin.Settings, "Duplicate " + name + " setting - Every Setting except the first one is ignored", SettingHandler.SETTINGHANDLERID, null, null, false);
		}
	}
	
	//Syntax Checkers
	private static Setting checkGroupSetting(Setting checkMe, int ite) {
		ArrayList<Integer> removeIDs = new ArrayList<Integer>();
		if (!checkMe.getName().equals("Group")) {
			reportSyntaxError("Group Name Checker", "Unknown Group \"" + checkMe.getName() + "\"", true, ite);
			return null;
		}
		createMissingTable("name", "id");
		String id = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\". Removing Group", false, ite);
						return null;
					} else {
						updateMissing("name");
					}
				break;
				case "id":
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\". Reoving Group", false, ite);
						return null;
					} else {
						if (SettingHandler.RESERVEDID.get("Group").contains(attribute.getValue())) {
							reportSyntaxError("Group Attribute Checker", "Reserved Group ID found " + attribute.getValue() + ". Removing Group", false, ite);
							return null;
						} else {
							id = attribute.getValue();
							updateMissing("id");
						}
					}
				break;
				default:
					reportSyntaxError("Group Attribute Checker", "Unknown Group Attribute \"" + attribute.getValue(), true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Group Attribute checker", "Missing attribute(s) in Group (" + printMissing() + "). Removing Group", false, ite);
			return null;
		}
		boolean hadListener = false;
		boolean hadResponder = false;
		int groupIte = 0;
		for (Setting groupElement : checkMe.getSubsettings()) {
			groupIte++;
			if (groupElement.getName().equals("Listeners")) {
				if (hadListener) {
					reportSyntaxError("Group Element Checker", "Duplicate Listeners Definition. Ignoring this and all following definitions", true, id, groupIte);
					continue;
				}
				hadListener = true;
				int listenerIte = 0;
				for (Setting subject : groupElement.getSubsettings()) {
					listenerIte++;
					boolean next = false;
					if (!subject.getName().equals("Listener")) {
						reportSyntaxError("Group Listeners Element Checker", "Unknown Element \"" + subject.getName() + "\"", true, id, listenerIte);
						next = true;
					}
					if (!next) {
						createMissingTable("name", "port", "log", "id");
						for (Pair<String, String> attribute : subject.getAttributes()) {
							switch (attribute.getKey()) {
							case "name":
								if (!matchesRegex(SettingHandler.REGEXNAME, subject.getValue())) {
									reportSyntaxError("Group Listener Attribute Checker", "Invalid name value \"" + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
									next = true;
									break;
								} else {
									updateMissing("name");
								}
								break;
							case "port":
								if (!matchesRegex(SettingHandler.REGEXPORT, subject.getValue())) {
									reportSyntaxError("Group Listener Attribute Checker", "Invalid port value \"" + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
									next = true;
									break;
								} else {
									updateMissing("port");
								}
								break;
							case "log":
								if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value", false, id, listenerIte);
									next = true;
									break;
								} else {
									updateMissing("log");
								}
								break;
							case "id":
								if (!matchesRegex(SettingHandler.REGEXID, subject.getValue())) {
									reportSyntaxError("Group Listener Attribute Checker", "Invalid id value \""  + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
									next = true;
									break;
								} else {
									updateMissing("id");
								}
								break;
							default:
								reportSyntaxError("Group Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\"", true, id, listenerIte);
							}
						}
						if (!hasAll()) {
							reportSyntaxError("Group Listener Attribute Checker", "Missing attribute(s) (" + printMissing() + "). Skipping Listener", true, id, listenerIte);
							next = true;
						}
					}
					if (next) {
						reportSyntaxError("Group Listener Checker", "Removing last mentioned Listener", false, id, listenerIte);
						checkMe.removeSetting(subject.getID());
						continue;
					}
				}
			} else if (groupElement.getName().equals("Responders")) {
				boolean next = false;
				if (hadResponder) {
					reportSyntaxError("Group Element Checker", "Duplicate Responders Definition. Ignoring this and all following definitions", true, id, groupIte);
					next = true;
				}
				hadResponder = true;
				int responderIte = 0;
				for (Setting subject : groupElement.getSubsettings()) {
					responderIte++;
					String name = "";
					if (!subject.getName().equals("Responder")) {
						reportSyntaxError("Group Responder Element Checker", "Unknown Element \"" + subject.getName() + "\"", true, id, responderIte);
						continue;
					}
					createMissingTable("name", "port", "log", "literalURL", "url", "id");
					boolean literalURL = false;
					String urlVal = null;
					for (Pair<String, String> attribute : subject.getAttributes()) {
						switch (attribute.getKey()) {
							case "name":
								if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute Checker", "Invalid name value", false, id, responderIte);
									next = true;
									break;
								} else {
									name = attribute.getValue();
									updateMissing("name");;
								}
							break;
							case "port":
								if (!matchesRegex(SettingHandler.REGEXPORT, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute Checker", "Invalid port value", false, id, responderIte);
									next = true;
									break;
								} else {
									updateMissing("port");
								}
							break;
							case "log":
								if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value", false, id, responderIte);
									next = true;
									break;
								} else {
									updateMissing("log");
								}
							break;
							case "literlURL":
								if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute checker", "Invalid literalURL Value", false, id, responderIte);
									next = true;
									break;
								} else {
									literalURL = Boolean.parseBoolean(attribute.getValue());
									updateMissing("literalURL");
								}
							break;
							case "url":
								updateMissing("url"); //This value is checked later on, depending on the literalURL value
								urlVal = attribute.getValue();
							break;
							case "id":
								if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
									reportSyntaxError("Group Responder Attribute Checker", "Invalid id value", false, id, responderIte);
									next = true;
									break;
								} else {
									updateMissing("id");
								}
							break;
						default:
							reportSyntaxError("Group Responder Value Checker", "Unknown Responder Element \"" + subject.getName() + "\"", true, id, responderIte);
						}
					}
					if (!next && !hasAll()) {
						reportSyntaxError("Group Responder Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", true, id, responderIte);
						next = true;
					}
					if (!literalURL) { //If the value is literal the user can give whatever he wants as a value. The socket will try to respond to the value and throw an error if it fails. This makes things easier for checking and provides user individuality
						if (!matchesRegex(SettingHandler.REGEXID, urlVal)) {
							reportSyntaxError("Group Responder Attribute Checker", "Invalid url value (Non-literal url values must contain a constantID)", false, id, responderIte);
							next = true;
						}
					}
					if (next) {
						reportSyntaxError("Group Responder Checker", "Removing last mentioned responder", false, id, responderIte);
						checkMe.removeSetting(subject.getID());
						continue;
					}
				} //End responder iteration
			} else {
				reportSyntaxError("Group Master Element Checker", "Unknown Group Element \"" + groupElement.getName() + "\"", true, id);
			}
		}
		for (int removeID : removeIDs) {
			SettingHandler.altered = true;
			checkMe.removeSetting(removeID);
		}
		return checkMe;
	}
	
	public static Setting checkParserSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Parser")) {
			reportSyntaxError("Parser Name Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return false;
		}
		int count = 0;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
				case "id":
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
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
	
	public static Setting checkTriggerSetting(Setting checkMe, int ite) {
		//TODO: Develop trigger storage model
		return true;
	}
	
	public static Setting checkConstantSetting(Setting checkMe, int ite) {
		ArrayList<String> removeIDs = new ArrayList<String>();
		if (!checkMe.getName().equals("Constants")) {
			reportSyntaxError("Group Name Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return null;
		}
		int constantIte = 0;
		for (Setting constant : checkMe.getSubsettings()) {
			boolean next = false;
			constantIte++;
			String id = null;
			createMissingTable("name", "useHeader", "id");
			for (Pair<String, String> attribute : constant.getAttributes()) {
				switch (attribute.getKey()) {
					case "name":
						if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Invalid name value", false, constantIte);
							next = true;
							break;
						} else {
							updateMissing("name");
						}
					break;
					case "useHeader":
						if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Invalid useHeader value", false, constantIte);
							next = true;
							break;
						} else {
							updateMissing("useHeader");
						}
					break;
					case "id":
						if (matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Invalid id value", false, constantIte);
							next = true;
							break;
						} else {
							id = attribute.getValue();
							updateMissing("id");
						}
					break;
					default:
						reportSyntaxError("Constant Attribute Checker", "Unknown Attribute\"" + attribute.getValue() + "\"", true, constantIte);
				}
			}
			if (next) {
				
			}
			if (!next && !hasAll()) {
				reportSyntaxError("Group Responder Constant Attribute Checker", "Missing attribute(s) (" + printMissing() + "). Skipping Responder", true, constantIte);
				next = true;
			}
			if (constant.hasSetting("Values")) {
				if (constant.getSettings("Values").size() > 1) {
					reportSyntaxError("Group Responder Element Checker", "Multiple Values Elements, using the first one", true, id, responderIte, constantIte);
				}
				for (Setting value : constant.getSettings("Values").get(0).getSettings("Value")) {
					createMissingTable("id", "isDynamic");
					boolean isDynamic = false;
					for (Pair<String, String> attribute : value.getAttributes()) {
						switch (attribute.getKey()) {
							case "id":
								if (matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
									reportSyntaxError("Group Responder Constant Value Attribute Checker", "Invalid id Value", false, id, responderIte, constantIte);
									next = true;
								} else {
									updateMissing("id");
								}
							break;
							case "isDynamic":
								if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
									reportSyntaxError("Group Responder Constant Value Attribute Checker", "Invalid isDynamic Value", false, id, responderIte, constantIte);
									next = true;
								} else {
									isDynamic = Boolean.parseBoolean(attribute.getValue());
									updateMissing("isDynamic");
								}
							default:
								reportSyntaxError("Group Responder Constant Value Attribute Checker", "Unknown Attribute Value \"" + attribute.getValue() + "\"", true, id , responderIte, constantIte);
						}
					}
					if (next) {
						break;
					}
					if (!hasAll()) {
						reportSyntaxError("Group Responder Constant Value Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, id , responderIte, constantIte);
						removeIDs.add(subject.getID());
					}
					if (isDynamic) {
						if (value.getValue() == null || value.getValue().isEmpty()) {
							reportSyntaxError("Group Responder Constant Value Attribute Checker", "An empty key is not allowed on a dynamic value", false, id, responderIte, constantIte);
							next = true;
							break;
						}
					}
				} //End value iteration
			} else {
				reportSyntaxError("Group Responder Constant Element Checker", "Missing Constant Values Element" + name, false, id, responderIte, constantIte);
				next = true;
				break;
			}
		} //End constant iteration
		} else {
			reportSyntaxError("Group Responder Constant Checker", "Missing Consants Element in Responder " + name + "", false, id);
			next = true;
		}
	}
	public static void createMissingTable(String... keys) {
		HashMap<String, Boolean> table = new HashMap<String, Boolean>();
		for (String key : keys) {
			table.put(key, false);
		}
		SettingHandler.missingTable = table;
	}
	
	public static void updateMissing(String key) {
		SettingHandler.missingTable.remove(key);
		SettingHandler.missingTable.put(key, true);
	}
	
	public static String printMissing() {
		String missingString = "";
		for (Map.Entry<String, Boolean> entry : SettingHandler.missingTable.entrySet()) {
			if (!entry.getValue()) {
				if (!missingString.isEmpty()) {
					missingString += ", ";
				}
				missingString += entry.getKey();
			}
		}
		return missingString;
	}
	
	public static boolean hasAll() {
		for (Map.Entry<String, Boolean> entry : SettingHandler.missingTable.entrySet()) {
			if (!entry.getValue()) {
				return false;
			}
		}
		return false;
	}
	
	
	public static boolean matchesRegex(String regex, String subject) {
		if (subject == null || regex == null) {
			return false;
		}
		Matcher matcher = Pattern.compile(regex).matcher(subject);
		return matcher.matches();
	}
}
