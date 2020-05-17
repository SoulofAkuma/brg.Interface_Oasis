package settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
	
	public static final String FILEHANDLERID = "000000000"; //Reserved ID for the FileHandler (for error reports)
	public static final String GROUPHANDLERID = "000000001"; //Reserved ID for the GroupHandler(for error reports)
	public static final String SETTINGHANDLERID = "000000002"; //Reserved ID for the SettingHandler (for error reports)
	public static final String SETTINGPARSINGID = "000000003"; //Reserved ID for the Setting parsing process (for error reports)
	public static final String PARSERHANDLERID = "000000004"; //Reserved ID for the ParserHandler (for error reports)
	
	public static final Set<String> IDS = Collections.synchronizedSet(new HashSet<String>()); //A multithread set with all existing IDs which takes O(1) to iterate over
	
	public static final String REGEXNAME = "[0-9A-Za-z_.()\\[\\]\\-{};,:%/!?& ]{0,50}"; //Regex for names defined in attributes which can be empty
	public static final String REGEXID = "[0-9]{9}"; //Regex for ids defined in attributes
	public static final String REGEXPORT = "[0-9]+"; //Regex for ports defined in attributes (the port size is unrestricted, so this program can be used on machine with custom defined port numbers)
	public static final String REGEXBOOL = "(true|false)"; //Regex for boolean values
	public static final String REGEXIDLIST = "([0-9]{9})*(,[0-9]{9})*"; //Regex for a list of IDs which can be empty
	
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
		SettingHandler.IDS.add(SettingHandler.FILEHANDLERID); //FileHandler
		SettingHandler.IDS.add(SettingHandler.GROUPHANDLERID); //GroupHandler
		SettingHandler.IDS.add(SettingHandler.PARSERHANDLERID); //SettingHandler
		SettingHandler.IDS.add(SettingHandler.SETTINGHANDLERID); //SettingFunctions
		SettingHandler.IDS.add(SettingHandler.SETTINGPARSINGID); //ParserHandler
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
		Logger.addMessage(MessageType.Information, MessageOrigin.SettingHandler, message, SettingHandler.SETTINGHANDLERID, null, null, false);
	}
	
	private static void reportError(String message, String causes) {
		String objectMessage = message + " - " + causes;
		String[] elements = {"ID", "Origin", "Message", "Causes"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), message, causes};
		Logger.addMessage(MessageType.Error, MessageOrigin.SettingHandler, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, true);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message;
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String id) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (ID: " + id +")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (Element Iteration: " + String.valueOf(iteration)+")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String id, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported " + message + " (ID: " + id +"Element Iteration: " + String.valueOf(iteration) + ")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void checkSettingStructure(String name) {
		if (SettingHandler.masterSetting.getSettings(name).size() != 1) {
			Logger.addMessage(MessageType.Warning, MessageOrigin.SettingHandler, "Duplicate " + name + " setting - Every Setting except the first one is ignored", SettingHandler.SETTINGHANDLERID, null, null, false);
		}
	}
	
	//Syntax Checkers
	private static Setting checkGroupSetting(Setting checkMe, int ite) {
		ArrayList<Integer> removeIDs = new ArrayList<Integer>();
		Set<String> localIDs = new HashSet<String>();
		if (!checkMe.getName().equals("Group")) {
			reportSyntaxError("Group Name Checker", "Unknown Groups Element \"" + checkMe.getName() + "\"", true, ite);
			return null;
		}
		createMissingTable("name", "id");
		boolean returnNull = false;
		String id = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\". Removing Group", false, ite);		
						returnNull = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\". Removing Group", false, ite);
						returnNull = true;
					} else {
						if (SettingHandler.IDS.contains(attribute.getValue())) {
							reportSyntaxError("Group Attribute Checker", "Duplicate ID found " + attribute.getValue() + ". Removing Group", false, ite);
							returnNull = true;
							id = "unset";
						} else {
							localIDs.add(attribute.getValue());
							id = attribute.getValue();
						}
					}
				break;
				default:
					reportSyntaxError("Group Attribute Checker", "Unknown Group Attribute \"" + attribute.getValue(), true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Group Attribute checker", "Missing attribute(s) in Group (" + printMissing() + "). Removing Group", false, ite);
			returnNull = true;
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
					String listenerID = null;
					if (!subject.getName().equals("Listener")) {
						reportSyntaxError("Group Listeners Element Checker", "Unknown Element \"" + subject.getName() + "\". Removing Element", true, id, listenerIte);
						next = true;
					}
					if (!next) {
						createMissingTable("name", "port", "log", "id");
						for (Pair<String, String> attribute : subject.getAttributes()) {
							switch (attribute.getKey()) {
								case "name":
									updateMissing("name");
									if (!matchesRegex(SettingHandler.REGEXNAME, subject.getValue())) {
										reportSyntaxError("Group Listener Attribute Checker", "Invalid name value \"" + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
										next = true;
									}
								break;
								case "port":
									updateMissing("port");
									if (!matchesRegex(SettingHandler.REGEXPORT, subject.getValue())) {
										reportSyntaxError("Group Listener Attribute Checker", "Invalid port value \"" + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
										next = true;
									}
								break;
								case "log":
									updateMissing("log");
									if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value", false, id, listenerIte);
										next = true;
									}
								break;
								case "id":
									updateMissing("id");
									if (!matchesRegex(SettingHandler.REGEXID, subject.getValue())) {
										reportSyntaxError("Group Listener Attribute Checker", "Invalid id value \""  + String.valueOf(subject.getValue()) + "\"", false, id, listenerIte);
										next = true;
									} else {
										if (localIDs.contains(attribute.getValue()) || SettingHandler.IDS.contains(attribute.getValue())) {
											reportSyntaxError("Group Listener Attribute Checker", "Duplicate ID found " + attribute.getValue(),								 false, id, listenerIte);
											next = true;
										} else {
											listenerID = attribute.getValue();
										}
									}
								break;
								default:
									reportSyntaxError("Group Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\"", true, id, listenerIte);
							}
						}
						if (!hasAll()) {
							reportSyntaxError("Group Listener Attribute Checker", "Missing Attribute(s) (" + printMissing() + "). Skipping Listener", true, id, listenerIte);
							next = true;
						}
					}
					if (next) {
						reportSyntaxError("Group Listener Checker", "Removing last mentioned Listener", false, id, listenerIte);
						checkMe.removeSetting(subject.getID());
					} else {
						localIDs.add(listenerID);
					}
				}
			} else if (groupElement.getName().equals("Responders")) {
				if (hadResponder) {
					reportSyntaxError("Group Element Checker", "Duplicate Responders Definition. Ignoring this and all following definitions", true, id, groupIte);
					continue;
				}
				hadResponder = true;
				int responderIte = 0;
				for (Setting subject : groupElement.getSubsettings()) {
					responderIte++;
					boolean next = false;
					String responderID = null;
					if (!subject.getName().equals("Responder")) {
						reportSyntaxError("Group Responder Element Checker", "Unknown Element \"" + subject.getName() + "\". Removing Element", true, id, responderIte);
						next = true;
					}
					if (!next) {
						createMissingTable("name", "port", "log", "literalURL", "url", "constants", "id");
						boolean literalURL = false;
						String urlVal = null;
						for (Pair<String, String> attribute : subject.getAttributes()) {
							switch (attribute.getKey()) {
								case "name":
									updateMissing("name");
									if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid name value", false, id, responderIte);
										next = true;
									}
								break;
								case "port":
									updateMissing("port");
									if (!matchesRegex(SettingHandler.REGEXPORT, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid port value", false, id, responderIte);
										next = true;
									}
								break;
								case "log":
									updateMissing("log");
									if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value", false, id, responderIte);
										next = true;
									}
								break;
								case "literlURL":
									updateMissing("literalURL");
									if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute checker", "Invalid literalURL Value", false, id, responderIte);
										next = true;
									} else {
										literalURL = Boolean.parseBoolean(attribute.getValue());
									}
								break;
								case "url":
									updateMissing("url"); //This value is checked later on, depending on the literalURL value
									urlVal = attribute.getValue();
								break;
								case "constants":
									updateMissing("constants");
									if (!matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid constants value", false, id, responderIte);
										next = true;
									}
								break;	
								case "id":
									updateMissing("id");
									if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
										reportSyntaxError("Group Responder Attribute Checker", "Invalid id value", false, id, responderIte);
										next = true;
									} else {
										if (localIDs.contains(attribute.getValue()) || SettingHandler.IDS.contains(attribute.getValue())) {
											reportSyntaxError("Group Responder Attribute Checker", "Duplicate ID found", false, id, responderIte);
											next = true;
										} else {
											responderID = attribute.getValue();
										}
									}
								break;
							default:
								reportSyntaxError("Group Responder Value Checker", "Unknown Responder Element \"" + subject.getName() + "\"", true, id, responderIte);
							}
						}
						if (!hasAll()) {
							reportSyntaxError("Group Responder Attribute Checker", "Missing Attribute(s) (" + printMissing() + ")", false, id, responderIte);
							next = true;
						}
						if (!literalURL) { //If the value is literal the user can give whatever he wants as a value. The socket will try to respond to the value and throw an error if it fails. This makes things easier for checking and provides user individuality. Otherwise a constant id is expected
							if (!matchesRegex(SettingHandler.REGEXID, urlVal)) {
								reportSyntaxError("Group Responder Attribute Checker", "Invalid url value (Non-literal url values must contain a constantID)", false, id, responderIte);
								next = true;
							}
						}
					}
					if (next) {
						reportSyntaxError("Group Responder Checker", "Removing last mentioned responder", false, id, responderIte);
						checkMe.removeSetting(subject.getID());
					} else {
						localIDs.add(responderID);
					}
				} //End responder iteration
			} else {
				reportSyntaxError("Group Master Element Checker", "Unknown Group Element \"" + groupElement.getName() + "\"", true, id);
			}
		}
		if (returnNull) {
			return null;
		}
		SettingHandler.IDS.addAll(localIDs);
		for (int removeID : removeIDs) {
			SettingHandler.altered = true;
			checkMe.removeSetting(removeID);
		}
		return checkMe;
	}
	
	public static Setting checkParserSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Parser")) {
			reportSyntaxError("Parser Name Checker", "Unknown Parser \"" + checkMe.getName() + "\". Removing Parser", true);
			return null;
		}
		createMissingTable("name", "id");
		boolean next = false;
		String parserID = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					} else {
						if (SettingHandler.IDS.contains(attribute.getValue())) {
							reportSyntaxError("Parser Attribute Checker", "Duplicate ID found", false, ite);
						} else {
							parserID = attribute.getValue();
						}
					}
				break;
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Parser Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, ite);
			next = true;
		}
		if (next) {
			reportSyntaxError("Parser Checker", "Removing last mentioned Parser", false, ite);
			return null;
		}
		SettingHandler.IDS.add(parserID);
		//Further syntax checking is not necessary. The parser consists of many rules which are independently checked by the corresponding rule
		return checkMe;
	}
	
	public static Setting checkTriggerSetting(Setting checkMe, int ite) {
		//TODO: Develop trigger storage model
		return checkMe;
	}
	
	public static Setting checkConstantSetting(Setting checkMe, int ite) {
		ArrayList<Integer> removeIDs = new ArrayList<Integer>();
		ArrayList<String> values = new ArrayList<String>();
		Set<String> localIDs = new HashSet<String>();
		if (!checkMe.getName().equals("Constant")) {
			reportSyntaxError("Constant Element Checker", "Unknown Element \"" + checkMe.getName() + "\". Removing Element", true, ite);
			return null;
		}
		boolean returnNull = false;
		String id = null;
		String[] order = null;
		createMissingTable("name", "id", "order");
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid name value", false, ite);
						returnNull = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid id value", false, ite);
						id = "unset";
						returnNull = true;
					} else {
						if (SettingHandler.IDS.contains(attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Duplicate ID found " + attribute.getValue(), false, ite);
							returnNull = true;
						} else {
							id = attribute.getValue();
							localIDs.add(attribute.getValue());
						}
					}
				break;
				case "order":
					updateMissing("order");
					if (matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid order Value", false, ite);
						returnNull = true;
					} else {
						order = attribute.getValue().split(",");
					}
				break;
				default:
					reportSyntaxError("Constant Attribute Checker", "Unknown Attribute\"" + attribute.getValue() + "\"", true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Constant Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", true, ite);
			returnNull = true;
		}
		if (!checkMe.hasSetting("Values") || checkMe.getSettings("Values").get(0).getLevel() != 2) {
			reportSyntaxError("Constant Element Checker", "Missing Values Element. Removing Constant", false, id);
			return null;
		}
		int valueIte = 0;
		for (Setting value : checkMe.getSettings("Values").get(0).getSettings("Value")) {
			valueIte++;
			boolean next = false;
			String valueID = null;
			createMissingTable("id", "isDynamic", "useHeader");
			boolean isDynamic = false;
			for (Pair<String, String> attribute : value.getAttributes()) {
				switch (attribute.getKey()) {
					case "id":
						updateMissing("id");
						if (matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
							reportSyntaxError("Constant Value Attribute Checker", "Invalid id Value", false, id, valueIte);
							next = true;
						} else {
							if (localIDs.contains(attribute.getValue()) || SettingHandler.IDS.contains(attribute.getValue())) {
								reportSyntaxError("Constant Value Attribute Checker", "Duplicate ID found " + attribute.getValue(), false, id, valueIte);
								next = true;
							} else {
								valueID = attribute.getValue();
							}
						}
					break;
					case "isDynamic":
						updateMissing("isDynamic");
						if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
							reportSyntaxError("Constant Value Attribute Checker", "Invalid isDynamic Value", false, id, valueIte);
							next = true;
						} else {
							isDynamic = Boolean.parseBoolean(attribute.getValue());
						}
					break;
					case "useHeader":
						updateMissing("useHeader");
						if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Invalid useHeader value", false, id, valueIte);
							next = true;
						}
					break;
					default:
						reportSyntaxError("Constant Value Attribute Checker", "Unknown Attribute Value \"" + attribute.getValue() + "\"", true, id , valueIte);
				}
			}
			if (!hasAll()) {
				reportSyntaxError("Constant Value Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, id , valueIte);
				next = true;
			}
			if (isDynamic) {
				if (value.getValue() == null || value.getValue().isEmpty()) {
					reportSyntaxError("Constant Value Attribute Checker", "An empty key is not allowed on a dynamic value", false, id, valueIte);
					next = true;
				}
			}
			if (next) {
				reportSyntaxError("Constant Value Checker", "Removing last mentioned Value", true, id, valueIte);
				removeIDs.add(value.getID());
			} else {
				values.add(valueID);
				localIDs.add(valueID);
			}
		}
		//TODO: Test for order of the values (via defined id list)
		if (returnNull) {
			return null;
		}
		SettingHandler.IDS.addAll(localIDs);
		for (String valueID : order) {
			if (!values.contains(valueID)) {
				reportSyntaxError("Constant Attribute Checker", "Undefined ValueID " + valueID + " in the order attribute", false, id);
				return null;
			}
		}
		for (Integer index : removeIDs) {
			checkMe.removeSetting(index);
		}
		return checkMe;
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
