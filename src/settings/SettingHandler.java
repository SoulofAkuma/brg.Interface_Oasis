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
import indexassigner.IndexAssignerHandler;
import parser.ParserHandler;
import trigger.TriggerHandler;
import trigger.TriggerType;

//This class sets up all setting parsing, checks the syntax and forwards the setting processing to the corresponding classes ()
public class SettingHandler {
	
	private static int fileID; //ID of the setting file for the file handler
	private static final String SETTINGFOLDER = Manager.PATH + Manager.SEPERATOR + "Settings";
	private static final String SETTINGBACKUPFOLDER = SettingHandler.SETTINGFOLDER + Manager.SEPERATOR + "Backups";
	private static final String SETTINGFILEPATH = SettingHandler.SETTINGFOLDER + Manager.SEPERATOR + "settings.xml"; //Name of the setting file
	private static final String SETTINGBACKUPPATH = SettingHandler.SETTINGBACKUPFOLDER + Manager.SEPERATOR + "Setting Backup Session " + Main.SESSIONTIME + ".xml";
	private static final String BASESETTING = "InterfaceOasis"; //Name of the root Element in the xml setting
	private static Setting masterSetting; //The content of the setting file parsed into a setting
	private static Setting groupMasterSetting; //The master group setting element derived from the master setting
	private static Setting triggerMasterSetting; //The master trigger setting element derived from the master setting
	private static Setting parserMasterSetting; //The master parser setting element derived from the master setting
	private static Setting constantMasterSetting; //The master constant setting element derived from the master setting
	private static Setting indexAssignerMasterSetting; //The master index assigner setting element derived from the master setting
	private static Setting launchIDSMasterSetting; //The master launch id setting element derived from the master setting
	private static Setting backupSetting; //A backup of the original master setting after the basic setup (initialization of the other master settings)
	
	public static final String FILEHANDLERID = "000000000"; //Reserved ID for the FileHandler (for error reports)
	public static final String GROUPHANDLERID = "000000001"; //Reserved ID for the GroupHandler(for error reports)
	public static final String SETTINGHANDLERID = "000000002"; //Reserved ID for the SettingHandler (for error reports)
	public static final String SETTINGPARSINGID = "000000003"; //Reserved ID for the Setting parsing process (for error reports)
	public static final String PARSERHANDLERID = "000000004"; //Reserved ID for the ParserHandler (for error reports)
	public static final String GETPARSERID = "00000005";
	
	public static final ConcurrentHashMap<String, IDType> IDS = new ConcurrentHashMap<String, IDType>(); //A multithread set with all existing IDs which takes O(1) to iterate over
	public static final Set<String> CONSTANTIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid constant ids for quick validation with O(1)
	public static final Set<String> LISTENERIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid listener ids for quick validation with O(1)
	public static final Set<String> RESPONDERIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid responder ids for quick validation with O(1)
	public static final Set<String> GROUPIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid group ids for quick validation with O(1)
	public static final Set<String> PARSERIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid parser ids for quick validation with O(1)
	public static final Set<String> INDEXASSIGNERIDS = Collections.synchronizedSet(new HashSet<String>()); //List of all valid indexassigner ids for quick validation with O(1)
	
	public static final String REGEXNAME = "[0-9A-Za-z_.()\\[\\]\\-{};,:%/!?& ]{0,50}"; //Regex for names defined in attributes which can be empty
	public static final String REGEXID = "[0-9]{9}"; //Regex for ids defined in attributes
	public static final String REGEXPORT = "[0-9]+"; //Regex for ports defined in attributes (the port size is unrestricted, so this program can be used on machine with custom defined port numbers)
	public static final String REGEXBOOL = "(true|false)"; //Regex for boolean values
	public static final String REGEXIDLIST = "([0-9]{9}){0,1}(,[0-9]{9})*"; //Regex for a list of IDs which can be empty
	public static final String REGEXSTRICTNAME = "[0-9a-zA-Z ]{0,50}"; //Regex for strict names (used for string arrays)
	public static final String REGEXSTRICTNAMELIST = "([0-9a-zA-Z ]+){0,50}(,[0-9a-zA-Z ]+){0,50}";
	
	private static HashMap<String, Boolean> missingTable; //Reusable for attribute checking
	
	public static void init() {
		
		initReserved();
		
		Manager.checkPath(SettingHandler.SETTINGFOLDER);
		Manager.checkPath(SettingHandler.SETTINGBACKUPFOLDER);
		fileID = Manager.newFile(SettingHandler.SETTINGFILEPATH);
		
		if (fileID == -1) {
			reportError("Missing Setting File", "All actions will not be saved");
			return;
		}
		
		SettingHandler.masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);
		boolean alteredInformation = false;
		boolean wasEmpty = false;
		boolean dpCorrupt = false;
		
		if (masterSetting.reset()) {
			resetInformation("No setting found - resetting to default");
			masterSetting.resetSetting(SettingHandler.BASESETTING);
			alteredInformation = true;
			wasEmpty = true;
		}
		
		if (!masterSetting.hasSetting("Groups") || masterSetting.getSettings("Groups").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No Groups found - Resetting to default");
			masterSetting.addSetting("Groups", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("Triggers") || masterSetting.getSettings("Triggers").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No Triggers found - Resetting to default");
			masterSetting.addSetting("Triggers", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("Parsers") || masterSetting.getSettings("Parsers").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No Parsers found - resetting to default");
			masterSetting.addSetting("Parsers", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("Constants") || masterSetting.getSettings("Constants").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No Constants found - resetting to default");
			masterSetting.addSetting("Constants", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("IndexAssigners") || masterSetting.getSettings("IndexAssigners").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No IndexAssigners found - resetting to default");
			masterSetting.addSetting("IndexAssigners", null, null);
			alteredInformation = true;
		}
		
		if (!masterSetting.hasSetting("LaunchIDS") || masterSetting.getSettings("LaunchIDS").get(0).getLevel() != 2) {
			if (!wasEmpty && !dpCorrupt) {
				resetInformation("The setting file syntax is corrutp - Resetting to default");
				masterSetting = Setting.parseSetting("", 1);
				masterSetting.resetSetting(SettingHandler.BASESETTING);
				alteredInformation = true;
				dpCorrupt = true;
			}
			resetInformation("No LaunchIDS found - resetting to default");
			masterSetting.addSetting("LaunchIDS", null, null);
			alteredInformation = true;
		}
		
		
		if (alteredInformation && !wasEmpty) {
			String message = "Due to "; 
			message += (dpCorrupt) ? "a corrupt setting file" : "missing base settings";
			message += " your settings have been altered - Do you want to create a backup of your old setting file?";
			if (Main.askMessage(message, "Backup Setting File") == 0) {
				Manager.copyFile(fileID, SettingHandler.SETTINGBACKUPPATH);				
			}
		}
		SettingHandler.backupSetting = SettingHandler.masterSetting.getBackup();
		initSetting(SettingHandler.masterSetting);
	}
	
	public static void initSetting(Setting setting) {
		
		checkSettingStructure("Groups");
		checkSettingStructure("Triggers");
		checkSettingStructure("Parsers");
		checkSettingStructure("Constants");
		checkSettingStructure("IndexAssigners");
		checkSettingStructure("LaunchIDS");
		
		
		SettingHandler.groupMasterSetting = masterSetting.getSettings("Groups").get(0);
		SettingHandler.triggerMasterSetting = masterSetting.getSettings("Triggers").get(0);
		SettingHandler.parserMasterSetting = masterSetting.getSettings("Parsers").get(0);
		SettingHandler.constantMasterSetting = masterSetting.getSettings("Constants").get(0);
		SettingHandler.indexAssignerMasterSetting = masterSetting.getSettings("IndexAssigners").get(0);
		SettingHandler.launchIDSMasterSetting = masterSetting.getSettings("LaunchIDS").get(0);
		
		//Constants before groups
		ArrayList<Pair<String, String>> toCheckReference = new ArrayList<Pair<String, String>>();
		for (int i = 0; i < SettingHandler.constantMasterSetting.getSubsettings().size(); i++) {
			checkConstantSetting(SettingHandler.constantMasterSetting.getSubsettings().get(i), i, toCheckReference);
		}
		
		
		ArrayList<String> disable = new ArrayList<String>();
		for (Pair<String, String> kvp : toCheckReference) {
			if (!SettingHandler.CONSTANTIDS.contains(kvp.getKey())) {
				reportSyntaxError("Constant Checker", "The back referenced constant \"" + kvp.getKey() + "\" does not exist. Disabling Constant \"" + kvp.getValue() + "\"", false);
				disable.add(kvp.getValue());
			}
		}
		
		disableSubByIdAttribute(constantMasterSetting, disable); //Remove Constants refering to other constants after all constants have been parsed
		
		//Group before trigger
		for (int i = 0; i < SettingHandler.groupMasterSetting.getSubsettings().size(); i++) {
			checkGroupSetting(SettingHandler.groupMasterSetting.getSubsettings().get(i), i);
		}
		
		//IndexAssigner before parser
		for (int i = 0; i < SettingHandler.indexAssignerMasterSetting.getSubsettings().size(); i++) {
			checkIndexAssignerSetting(SettingHandler.indexAssignerMasterSetting.getSubsettings().get(i), i);
		}
		
		//Parser before trigger
		for (int i = 0; i < SettingHandler.parserMasterSetting.getSubsettings().size(); i++) {
			checkParserSetting(SettingHandler.parserMasterSetting.getSubsettings().get(i), i);
		}
		
		//Trigger after parser and group and before launchids
		for (int i = 0; i < SettingHandler.triggerMasterSetting.getSubsettings().size(); i++) {
			checkTriggerSetting(SettingHandler.triggerMasterSetting.getSubsettings().get(i), i);
		}
		
		checkLaunchIDSSetting(SettingHandler.launchIDSMasterSetting);
		
		GroupHandler.init(SettingHandler.groupMasterSetting);
		TriggerHandler.init(SettingHandler.triggerMasterSetting);
		ParserHandler.init(SettingHandler.parserMasterSetting);
		ConstantHandler.init(SettingHandler.constantMasterSetting);
		IndexAssignerHandler.init(SettingHandler.indexAssignerMasterSetting);
	}
	
	public static void disableSubByIdAttribute(Setting master, ArrayList<String> disable) {
		HashMap<String, Setting> idSettings = new HashMap<String, Setting>();
		for (Setting sub : master.getSubsettings()) {
			String id = (sub.getAttributes().containsKey("id")) ? sub.getAttribute("id") : null;
			if (id != null) {
				idSettings.put(id, sub);
			}
		}
		
		for (String disableID : disable) {
			if (idSettings.containsKey(disableID)) {
				idSettings.get(disableID).disable();
			}
		}
	}

	public static void initReserved() {
		SettingHandler.IDS.put(SettingHandler.FILEHANDLERID, IDType.FileHandler); //FileHandler
		SettingHandler.IDS.put(SettingHandler.GROUPHANDLERID, IDType.GroupHandler); //GroupHandler
		SettingHandler.IDS.put(SettingHandler.PARSERHANDLERID, IDType.ParserHandler); //SettingHandler
		SettingHandler.IDS.put(SettingHandler.SETTINGHANDLERID, IDType.SettingHandler); //SettingFunctions
		SettingHandler.IDS.put(SettingHandler.SETTINGPARSINGID, IDType.SettingParser); //ParserHandler
		SettingHandler.IDS.put(SettingHandler.GETPARSERID, IDType.Parser);
	}
	
	public static void close() {
		Manager.writeFile(fileID, masterSetting.getXML(), false);
	}
	
	public static void createSetting(String name, String value, HashMap<String, String> attributes) {
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
		String objectMessage = source + " in the setting syntax checker reported: " + message;
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String id) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported: " + message + " (ID: " + id +")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported: " + message + " (Element Iteration: " + String.valueOf(iteration)+")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String id, int iteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported: " + message + " (GroupID: " + id +" Element Iteration: " + String.valueOf(iteration) + ")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void reportSyntaxError(String source, String message, boolean isWarning, String id, int iteration, int innerIteration) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.SettingHandler;
		String[] elements = {"ID", "Origin", "Source", "Message"};
		String[] values = {SettingHandler.SETTINGHANDLERID, MessageOrigin.SettingHandler.name(), source, message};
		String objectMessage = source + " in the setting syntax checker reported: " + message + " (GroupID: " + id +" Element Iteration: " + String.valueOf(iteration) + " Inner Element Interation: " + String.valueOf(innerIteration) + ")";
		Logger.addMessage(type, origin, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, false);
	}
	
	private static void checkSettingStructure(String name) {
		if (SettingHandler.masterSetting.getSettings(name).size() != 1) {
			Logger.addMessage(MessageType.Warning, MessageOrigin.SettingHandler, "Duplicate " + name + " setting - Every Setting except the first one is ignored", SettingHandler.SETTINGHANDLERID, null, null, false);
		}
	}

	//Syntax Checkers
	private static void checkIndexAssignerSetting(Setting checkMe, int ite) {
		HashMap<String, IDType> localIDs = new HashMap<String, IDType>();
		if (!checkMe.getName().equals("IndexAssigner")) {
			reportSyntaxError("IndexAssigner Checker", "Unknown Element \"" + checkMe.getName() + "\"", true, ite);
			checkMe.disable();
			return;
		}
		boolean disable = checkIndIndexAssignerSetting(checkMe, ite, localIDs);
	}
	
	private static void checkLaunchIDSSetting(Setting checkMe) {
		//TODO: Implement
	}
	
	private static void checkGroupSetting(Setting checkMe, int ite) {
		HashMap<String, IDType> localIDs = new HashMap<String, IDType>();
		if (!checkMe.getName().equals("Group")) {
			reportSyntaxError("Group Checker", "Unknown Element \"" + checkMe.getName() + "\"", true, ite);
			checkMe.disable();
			return;
		}
		boolean disable = checkIndGroupSetting(checkMe, ite, localIDs);
		String id = (disable) ? "unset" : checkMe.getAttribute("id");
		if (!disable) {
			int listenerIte = 0;
			for (Setting listener : checkMe.getSettings("Listeners").get(0).getSubsettings()) {
				listenerIte++;
				if (checkIndListenerSetting(listener, listenerIte, id, localIDs)) {
					listener.disable();
				}
			}
			int responderIte = 0;
			for (Setting responder : checkMe.getSettings("Responders").get(0).getSubsettings()) {
				responderIte++;
				if (checkIndResponderSetting(responder, responderIte, id, localIDs)) {
					responder.disable();
				}
			}
		}
		if (disable) {
			reportSyntaxError("Group Checker", "Disabling last mentioned Group", false, ite);
			checkMe.disable();
		} else {
			SettingHandler.IDS.putAll(localIDs);
			ArrayList<String> listenerIDs = new ArrayList<String>();
			ArrayList<String> responderIDs = new ArrayList<String>();
			for (Map.Entry<String, IDType> lid : localIDs.entrySet()) {
				if (lid.getValue() == IDType.Listener) {
					listenerIDs.add(lid.getKey());
				} else if (lid.getValue() == IDType.Responder) {
					responderIDs.add(lid.getKey());
				}
			}
			SettingHandler.LISTENERIDS.addAll(listenerIDs);
			SettingHandler.RESPONDERIDS.addAll(responderIDs);
			SettingHandler.GROUPIDS.add(id);
		}
	}
	
	public static void checkParserSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Parser")) {
			reportSyntaxError("Parser Checker", "Unknown Element \"" + checkMe.getName() + "\". Disabling Parser", true);
			checkMe.disable();
			return;
		}
		HashMap<String, IDType> localIDs = new HashMap<String, IDType>();
		boolean disable = checkIndParserSetting(checkMe, ite, localIDs);
		String id = (disable) ? null : checkMe.getAttribute("id");
		if (!disable) {
			int ruleIte = 0;
			for (Setting rule : checkMe.getSettings("Rules").get(0).getSubsettings()) {
				if (checkIndRuleSetting(rule, id, ruleIte, localIDs)) {
					rule.disable();
				}
			}
			
			String[] order = checkMe.getAttribute("order").split(",");
			ArrayList<String> ruleIDs = new ArrayList<String>();
			for (Map.Entry<String, IDType> idE : localIDs.entrySet()) {
				if (idE.getValue() == IDType.Rule) {
					ruleIDs.add(idE.getKey());
				}
			}
			String missing = "";
			for (String rule : order) {
				if (!ruleIDs.contains(rule)) {
					missing += rule + ",";
				}
			}
			if (missing.length() > 0) {
				reportSyntaxError("Parser Attribute Checker", "Missing Rules (" + missing.substring(0, missing.length() - 1) + ")", false, id);
				disable = true;
			}
		}
		
		if (disable) {
			reportSyntaxError("Parser Checker", "Disabling last mentioned Parser", false);
			checkMe.disable();
		} else {
			SettingHandler.IDS.putAll(localIDs);
			SettingHandler.PARSERIDS.add(id);
		}
	}
	
	public static void checkTriggerSetting(Setting checkMe, int ite) {
		HashMap<String, IDType> localIDs = new HashMap<String, IDType>();
		if (!checkMe.getName().equals("Trigger")) {
			reportSyntaxError("Trigger Checker", "Unknown Element \"" + checkMe.getName() + "\". Disabling Element", true, ite);
			checkMe.disable();
			return;
		}
		if (!checkIndTriggerSetting(checkMe, ite, localIDs)) {
			reportSyntaxError("Trigger Checker", "Disabling last mentioned Trigger", false);
			checkMe.disable();
		} else {
			SettingHandler.IDS.putAll(localIDs);
		}
	}
	
	public static void checkConstantSetting(Setting checkMe, int ite, ArrayList<Pair<String, String>> toCheckReference) {
		HashMap<String, IDType> localIDs = new HashMap<String, IDType>();
		if (!checkMe.getName().equals("Constant")) {
			reportSyntaxError("Constant Checker", "Unknown Element \"" + checkMe.getName() + "\". Disabling Element", true, ite);
			checkMe.disable();
			return;
		}
		boolean disable = checkIndConstantSetting(checkMe, ite, localIDs);
		String id = (disable) ? null : checkMe.getAttribute("id");
		if (!disable) {
			int valueIte = 0;
			for (Setting value : checkMe.getSettings("Values").get(0).getSubsettings()) {
				if (checkIndValueSetting(value, id, valueIte, localIDs, toCheckReference)) {
					value.disable();
				}
			}
			String missing = "";
			String[] order = checkMe.getAttribute("order").split(",");
			ArrayList<String> valueIDs = new ArrayList<String>();
			for (Map.Entry<String, IDType> idE : localIDs.entrySet()) {
				if (idE.getValue() == IDType.Value) {
					valueIDs.add(idE.getKey());
				}
			}
			for (String value : order) {
				if (!valueIDs.contains(value)) {
					missing += value + ",";
				}
			}
			if (missing.length() > 0) {
				reportSyntaxError("Constant Attribute Checker", "Missing Values (" + missing.substring(0, missing.length() - 1) + ")", false, id);
				disable = true;
			}
		}
		
		if (disable) {
			reportSyntaxError("Constant Checker", "Disabling last mentioned Constant", false, ite);
			checkMe.disable();
		} else {
			SettingHandler.IDS.putAll(localIDs);
			SettingHandler.CONSTANTIDS.add(id);
		}
	}
	
	public static boolean checkIndIndexAssignerSetting(Setting checkMe, int ite, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String id = null;
		createMissingTable("name","id","rmMatch");
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("IndexAssigner Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("IndexAssigner Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					} else {
						if (SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("IndexAssigner Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, ite);
							next = true;
						} else {
							id = attribute.getValue();
						}
					}
				break;
				case "rmMatch":
					updateMissing("rmMatch");
					if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
						reportSyntaxError("IndexAssigner Attribute Checker", "Invalid rmMatch value " + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				default:
					reportSyntaxError("IndexAssigner Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Trigger Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", true, ite);
			next = true;
		}
		
		if (!next) {
			localIDs.put(id, IDType.IndexAssigner);
		}
		
		return !next;
	}
	
	public static boolean checkIndIndexSetting(Setting checkMe, String id) {
		boolean next = false;
		createMissingTable("position","key");
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "position":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportSyntaxError("IndexAssigner Index Attribute Checker", "Invalid position value \"" + attribute.getValue() + "\"", false, id);
						next = true;
					}
				break;
				case "key":
					if (!matchesRegex(SettingHandler.REGEXSTRICTNAME, attribute.getValue())) {
						reportSyntaxError("IndexAssigner Index Attribute Checker", "Invalid key value \"" + attribute.getValue() + "\"", false, id);
						next = true;
					}
				break;
				default:
					reportSyntaxError("IndexAssigner Index Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, id);
			}
		}
		
		return !next;
	}
	
	public static boolean checkIndRegexSetting(Setting checkMe, String id) {
		boolean next = false;
		createMissingTable("regex","keys","defInd");
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "defInd":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportSyntaxError("IndexAssigner Regex Attribute Checker", "Invalid defInd value \"" + attribute.getValue() + "\"", false, id);
						next = true;
					}
				break;
				case "keys":
					if (!matchesRegex(SettingHandler.REGEXSTRICTNAMELIST, attribute.getValue())) {
						reportSyntaxError("IndexAssigner Regex Attribute Checker", "Invalid keys value \"" + attribute.getValue() + "\"", false, id);
						next = true;
					}
				break;
				case "regex":
					updateMissing("regex");
				default:
					reportSyntaxError("IndexAssigner Index Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, id);
			}
		}
		
		return !next;
	}
	
	public static boolean checkIndTriggerSetting(Setting checkMe, int ite, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String id = null;
		TriggerType type = null;
		createMissingTable("name", "id", "type", "responderIDs");
		ArrayList<String> responderIDs = new ArrayList<String>();
		ArrayList<String> parserIDs = new ArrayList<String>();
		ArrayList<String> listenerIDs = new ArrayList<String>();
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Trigger Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Trigger Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					} else {
						if (SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Trigger Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, ite);
							next = true;
						} else {
							id = attribute.getValue();
						}
					}
				break;
				case "type":
					updateMissing("type");
					try {
						type = TriggerType.valueOf(attribute.getValue());
					} catch (Exception e) {
						reportSyntaxError("Trigger Attribute Chekcer", "Unknown Trigger Type \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "responderIDs":
					updateMissing("responderIDs");
					String[] array = attribute.getValue().split(",");
					if (array.length / 2.0 != Math.round(array.length / 2.0)) {
						reportSyntaxError("Trigger Attribute Checker", "Invalid responderIDs syntax" + attribute.getValue() + "\"", false, ite);
						next = true;
					} else {
						for (int i = 0; i < array.length; i += 2) {
							parserIDs.add(array[i]);
							responderIDs.add(array[i + 1]);
						}
					}
				break;
				case "triggeredBy":
				break;
				case "cooldown":
				break;
				default:
					reportSyntaxError("Trigger Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Trigger Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", true, ite);
			next = true;
		}
		if (!next) {
			if (type == TriggerType.Listener || type == TriggerType.Responder) {
				if (!checkMe.hasAttribute("triggeredBy")) {
					reportSyntaxError("Trigger Attribute Checker", "Missing attribute (triggeredBy)", true, ite);
					next = true;
				} else {
					if (!matchesRegex(SettingHandler.REGEXIDLIST, checkMe.getAttribute("triggeredBy"))) {
						reportSyntaxError("Trigger Attribute Checker", "Invalid triggeredBy value \"" + checkMe.getAttribute("triggeredBy") + "\"", true, ite);
						next = true;						
					}
					String[] array = checkMe.getAttribute("triggeredBy").split(",");
					for (String eid : array) {
						if (type == TriggerType.Listener) {
							listenerIDs.add(eid);
						} else {
							responderIDs.add(eid);
						}
					}
				}
			} else if (type == TriggerType.Timer) {
				if (!checkMe.hasAttribute("cooldown")) {
					reportSyntaxError("Trigger Attribute Checker", "Missing attribute (cooldown)", true, ite);
					next = true;
				} else {
					if (!matchesRegex("[0-9]+", checkMe.getAttribute("cooldown"))) {
						reportSyntaxError("Trigger Attribute Checker", "Invalid triggeredBy value \"" + checkMe.getAttribute("cooldown") + "\"", true, ite);
						next = true;						
					}
				}
			}
			for (String lid : listenerIDs) {
				if (!SettingHandler.LISTENERIDS.contains(lid)) {
					reportSyntaxError("Trigger Attribute Checker", "Invalid listenerID in trigger \"" + lid + "\"", true, ite);
					next = true;
				}
			}
			for (String rid : responderIDs) {
				if (!SettingHandler.RESPONDERIDS.contains(rid)) {
					reportSyntaxError("Trigger Attribute Checker", "Invalid responderID in trigger \"" + rid + "\"", true, ite);
					next = true;
				}
			}
			for (String pid : parserIDs) {
				if (!SettingHandler.PARSERIDS.contains(pid)) {
					reportSyntaxError("Trigger Attribute Checker", "Invalid responderID in trigger \"" + pid + "\"", true, ite);
					next = true;
				}
			}
		}
		if (!next) {
			localIDs.put(id, IDType.Trigger);
		}
		return !next;
	}
	
	public static boolean checkIndConstantSetting(Setting checkMe, int ite, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String id = null;
		createMissingTable("name", "id", "order");
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, ite);
						id = "unset";
						next = true;
					} else {
						if (SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Constant Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, ite);
							next = true;
						} else {
							id = attribute.getValue();
						}
					}
				break;
				case "order":
					updateMissing("order");
					if (!matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid order Value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				default:
					reportSyntaxError("Constant Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Constant Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", true, ite);
			next = true;
		}
		
		boolean hadValues = false;
		for (Setting sub : checkMe.getSubsettings()) {
			if (sub.getName().equals("Values")) {
				if (hadValues) {
					reportSyntaxError("Constant Element Checker", "Duplicate Values Element", true, ite);
				}
				hadValues = true;
			}
		}
		
		if (!hadValues) {
			reportSyntaxError("Constant Element Checker", "Missing Values Element", false, ite);
			next = true;
		}
		
		if (next) {
			reportSyntaxError("Constant Checker", "Disabling last mentioned Constant", false, ite);
		} else {
			localIDs.put(id, IDType.Constant);
		}
		
		return !next;
	}
	
	public static boolean checkIndGroupSetting(Setting checkMe, int ite, HashMap<String, IDType> localIDs) {
		createMissingTable("name", "id");
		boolean disable = false;
		String id = null;
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "name":
					updateMissing("name");
					if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, ite);		
						disable = true;
					}
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Group Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, ite);
						disable = true;
					} else {
						if (SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Group Attribute Checker", "Duplicate ID found " + attribute.getValue(), false, ite);
							disable = true;
							id = "unset";
						} else {
							localIDs.put(attribute.getValue(), IDType.Group);
							id = attribute.getValue();
						}
					}
				break;
				default:
					reportSyntaxError("Group Attribute Checker", "Unknown Group Attribute \"" + attribute.getValue(), true, ite);
			}
		} //End Group Attribute Checking
		if (!hasAll()) {
			reportSyntaxError("Group Attribute Checker", "Missing attribute(s) in Group (" + printMissing() + ")", false, ite);
			disable = true;
		}
		boolean hadListener = false;
		boolean hadResponder = false;
		int groupIte = 0;
		for (Setting groupElement : checkMe.getSubsettings()) {
			groupIte++;
			if (groupElement.getName().equals("Listeners")) {
				if (hadListener) {
					reportSyntaxError("Group Element Checker", "Duplicate Listeners Definition. Ignoring this and all following definitions", true, id, ite, groupIte);
				}
				hadListener = true;
				continue;
			} else if (groupElement.getName().equals("Responders")) {
				if (hadResponder) {
					reportSyntaxError("Group Element Checker", "Duplicate Responders Definition. Ignoring this and all following definitions", true, id, ite, groupIte);
				}
				hadResponder = true;
				continue;
			} else {
				reportSyntaxError("Group Master Element Checker", "Unknown Group Element \"" + groupElement.getName() + "\"", true, id, ite, groupIte);
			}
		} //End Group Element Checking

		if (!hadListener) {
			reportSyntaxError("Group Master Element Checker", "Missing Listener", true, id, ite);
			disable = true;
		}
		
		if (!hadResponder) {
			reportSyntaxError("Group Master Element Checker", "Missing Responder", true, id, ite);
			disable = true;
		}
		
		if (disable) {
			reportSyntaxError("Group Checker", "Disabling last mentioned Element", true, id, ite);
		} else {
			localIDs.put(id, IDType.Group);
		}
		return disable;
	}
	
	public static boolean checkIndListenerSetting(Setting subject, int listenerIte, String id, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String listenerID = null;
		if (!subject.getName().equals("Listener")) {
			reportSyntaxError("Group Listener Checker", "Unknown Element \"" + subject.getName() + "\"", true, id, listenerIte);
			next = true;
		}
		if (!next) {
			createMissingTable("name", "port", "log", "id");
			for (Map.Entry<String, String> attribute : subject.getAttributes().entrySet()) {
				switch (attribute.getKey()) {
					case "name":
						updateMissing("name");
						if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
							reportSyntaxError("Group Listener Attribute Checker", "Invalid name value \"" + subject.getValue() + "\"", false, id, listenerIte);
							next = true;
						}
					break;
					case "port":
						updateMissing("port");
						if (!matchesRegex(SettingHandler.REGEXPORT, attribute.getValue())) {
							reportSyntaxError("Group Listener Attribute Checker", "Invalid port value \"" + subject.getValue() + "\"", false, id, listenerIte);
							next = true;
						}
					break;
					case "log":
						updateMissing("log");
						if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
							reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value \"" + attribute.getValue() + "\"", false, id, listenerIte);
							next = true;
						}
					break;
					case "id":
						updateMissing("id");
						if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
							reportSyntaxError("Group Listener Attribute Checker", "Invalid id value \""  + subject.getValue() + "\"", false, id, listenerIte);
							next = true;
						} else {
							if (localIDs.containsKey(attribute.getValue()) || SettingHandler.IDS.containsKey(attribute.getValue())) {
								reportSyntaxError("Group Listener Attribute Checker", "Duplicate ID found " + attribute.getValue(),								 false, id, listenerIte);
								next = true;
							} else {
								listenerID = attribute.getValue();
							}
						}
					break;
					default:
						reportSyntaxError("Group Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\" = \"" + attribute.getValue() + "\"", true, id, listenerIte);
				}
			} //End Listener Attribute Checking
			if (!hasAll()) {
				reportSyntaxError("Group Listener Attribute Checker", "Missing Attribute(s) (" + printMissing() + ")", true, id, listenerIte);
				next = true;
			}
		}
		if (next) {
			reportSyntaxError("Group Listener Checker", "Disabling last mentioned Element", false, id, listenerIte);
		} else {
			localIDs.put(listenerID, IDType.Listener);
		}
		return next;
	}
	
	public static boolean checkIndResponderSetting(Setting subject, int responderIte, String id, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String responderID = null;
		if (!subject.getName().equals("Responder")) {
			reportSyntaxError("Group Responder Checker", "Unknown Element \"" + subject.getName() + "\"", true, id, responderIte);
			next = true;
		}
		if (!next) {
			createMissingTable("name", "log", "id");
			for (Map.Entry<String, String> attribute : subject.getAttributes().entrySet()) {
				switch (attribute.getKey()) {
					case "name":
						updateMissing("name");
						if (!matchesRegex(SettingHandler.REGEXNAME, attribute.getValue())) {
							reportSyntaxError("Group Responder Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false, id, responderIte);
							next = true;
						}
					break;
					case "log":
						updateMissing("log");
						if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
							reportSyntaxError("Group Responder Attribute Checker", "Invalid log Value \"" + attribute.getValue() + "\"", false, id, responderIte);
							next = true;
						}
					break;
					case "id":
						updateMissing("id");
						if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
							reportSyntaxError("Group Responder Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, id, responderIte);
							next = true;
						} else {
							if (localIDs.containsKey(attribute.getValue()) || SettingHandler.IDS.containsKey(attribute.getValue())) {
								reportSyntaxError("Group Responder Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, id, responderIte);
								next = true;
							} else {
								responderID = attribute.getValue();
							}
						}
					break;
				default:
					reportSyntaxError("Group Responder Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, id, responderIte);
				}
			} //End Responder Attribute Checking
			if (!hasAll()) {
				reportSyntaxError("Group Responder Attribute Checker", "Missing Attribute(s) (" + printMissing() + ")", false, id, responderIte);
				next = true;
			}
			boolean hasHeader = false;
			boolean hasBody = false;
			int inner = 0;
			for (Setting sub : subject.getSubsettings()) {
				inner++;
				if (sub.getName().equals("Header")) {
					if (hasHeader) {
						reportSyntaxError("Group Responder Element Checker", "Duplicate Header Element", true, responderID, inner);
						continue;
					}
					createMissingTable("url");
					for (Map.Entry<String, String> attribute : sub.getAttributes().entrySet()) {
						switch (attribute.getKey()) {
							case "url":
								updateMissing("url");
								if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid url value \"" + attribute.getValue() + "\" (Not an id)", false, responderID, inner);
									next = true;
								} else if (!SettingHandler.CONSTANTIDS.contains(attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid url value \"" + attribute.getValue() + "\" (This constant does not exist)", false, responderID, inner);
									next = true;
								}
							break;
							case "userAgent":
								if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid userAgent value \"" + attribute.getValue() + "\" (Not an id)", false, responderID, inner);
									next = true;
								} else if (!SettingHandler.CONSTANTIDS.contains(attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid userAgent value \"" + attribute.getValue() + "\" (This constant does not exist)", false, responderID, inner);
									next = true;
								}
							break;
							case "contentType":
								if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid contentType value \"" + attribute.getValue() + "\" (Not an id)", false, responderID, inner);
									next = true;
								} else if (!SettingHandler.CONSTANTIDS.contains(attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid contentType value \"" + attribute.getValue() + "\" (This constant does not exist)", false, responderID, inner);
									next = true;
								}
							break;
							case "customArgs":
								if (!matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid customArgs value \"" + attribute.getValue() + "\" (Not an id list)", false, responderID, inner);
									next = true;
									break;
								}
								String missing = "";
								String[] cids = attribute.getValue().split(",");
								for (String cid : cids) {
									if (!SettingHandler.CONSTANTIDS.contains(cid)) {
										missing += cid + ",";
									}
								}
								if (missing.length() > 0) {
									next = true;
									reportSyntaxError("Group Responder Header Attribute Checker", "Invalid customArgs value \"" + attribute.getValue() + " (The following constants do not exist: " + missing.substring(0, missing.length() - 1) + ")", false, responderID, inner);
								}
							break;
							default:
								reportSyntaxError("Group Responder Header Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, responderID, inner);
						}
					} //End Responder Header Attribute Checking
					if (!hasAll()) {
						reportSyntaxError("Group Responder Header Attribute Checker", "Missing Attribute(s) (" + printMissing() + ")", false, responderID, inner);
						next = true;
					}
				} else if (sub.getName().equals("Body")) {
					if (hasBody) {
						reportSyntaxError("Group Responder Element Checker", "Duplicate Body Element", true, responderID, inner);
						continue;
					}
					createMissingTable("constants", "seperator");
					for (Map.Entry<String, String> attribute : sub.getAttributes().entrySet()) {
						switch (attribute.getKey()) {
							case "constants":
								updateMissing("constants");
								if (!matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
									reportSyntaxError("Group Responder Body Attribute Checker", "Invalid constants value \"" + attribute.getValue() + "\" (Not an id list)", false, responderID, inner);
									next = true;
									break;
								}
								String missing = "";
								String[] cids = attribute.getValue().split(",");
								for (String cid : cids) {
									if (!SettingHandler.CONSTANTIDS.contains(cid)) {
										missing += cid + ",";
									}
								}
								if (missing.length() > 0) {
									next = true;
									reportSyntaxError("Group Responder Body Attribute Checker", "Invalid constants value \"" + attribute.getValue() + " (The following constants do not exist: " + missing.substring(0, missing.length() - 1) + ")", false, responderID, inner);
								}
							break;
							case "seperator":
								updateMissing("seperator");
							break;
							default:
								reportSyntaxError("Group Responder Body Attribute Checker", "Unknown Attribute \"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, responderID, inner);
						}
					} //End Responder Body Attribute Checking
				} else {
					reportSyntaxError("Group Responder Element Checker", "Unknown Element \"" + sub.getName() + "\"", true, responderID);
				}
			} //End Responder Element Checking
		}
		if (next) {
			reportSyntaxError("Group Responder Checker", "Disabling last mentioned Responder", false, id, responderIte);
		} else {
			localIDs.put(responderID, IDType.Responder);
		}
		
		return next;
	}
	
	public static boolean checkIndParserSetting(Setting checkMe, int ite, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String id = null;
		createMissingTable("name", "id", "order", "indexAssigner");
		for (Map.Entry<String, String> attribute : checkMe.getAttributes().entrySet()) {
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
						if (SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Parser Attribute Checker", "Duplicate ID found \" " + attribute.getValue() + "\"", false, ite);
							next = true;
						} else {
							id = attribute.getValue();
						}
					}
				break;
				case "order":
					updateMissing("order");
					if (!matchesRegex(SettingHandler.REGEXIDLIST, attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid order Value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				break;
				case "indexAssigner":
					updateMissing("indexAssigner");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Parser Attribute Checker", "Invalid indexAssigner Value \"" + attribute.getValue() + "\"", false, ite);
						next = true;
					}
				default:
					reportSyntaxError("Parser Attribute Checker", "Unknown Attribute\"" + attribute.getKey() + "\" = \"" + attribute.getValue() + "\"", true, ite);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Parser Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, ite);
			next = true;
		}
		
		boolean hadRules = false;
		for (Setting sub : checkMe.getSubsettings()) {
			if (sub.getName().equals("Rules")) {
				if (hadRules) {
					reportSyntaxError("Parser Element Checker", "Duplicate Rules Element", true, ite);
				}
				hadRules = true;
			} else {
				reportSyntaxError("Parser Element Checker", "Unknown Element \"" + sub.getName() + "\"", true, ite);
			}
		}
		
		if (!hadRules) {
			next = true;
			reportSyntaxError("Parser Element Checker", "Missing Rules Element", false, ite);
		}
		
		
		if (next) {
			reportSyntaxError("Parser Checker", "Disabling last mentioned Parser", false, ite);
		} else {
			localIDs.put(id, IDType.Parser);
		}
		
		return next;
	}
	
	public static boolean checkIndRuleSetting(Setting rule, String id, int ruleIte, HashMap<String, IDType> localIDs) {
		boolean next = false;
		String ruleID = null;
		createMissingTable("type","id");
		for (Map.Entry<String, String> attribute : rule.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "type":
					updateMissing("type"); //will be verified later
				break;
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Parser Rule Attribute Checker", "Invalid id value \"" + attribute.getValue() + "\"", false, id, ruleIte);
						next = true;
						break;
					} else {
						if (localIDs.containsKey(attribute.getValue()) || SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Parser Rule Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, id, ruleIte);
							next = true;
							break;
						} else {
							ruleID = attribute.getValue();
						}
					}
				break;
			}
		}
		
		if (!hasAll()) {
			reportSyntaxError("Parser Rule Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, id, ruleIte);
			next = true;
		}
		
		if (next) {
			reportSyntaxError("Parser Rule Checker", "Disabling last mentioned Rule", false, id, ruleIte);
		} else {
			localIDs.put(ruleID, IDType.Rule);
		}
		
		return next;
	}
	
	public static boolean checkIndValueSetting(Setting value, String id, int valueIte, HashMap<String, IDType> localIDs, ArrayList<Pair<String, String>> toCheckRefernce) {
		boolean next = false;
		String valueID = null;
		String backReference = null;
		createMissingTable("id", "isKey", "useHeader", "backReference");
		boolean isKey = false;
		boolean isBackReference = false;
		for (Map.Entry<String, String> attribute : value.getAttributes().entrySet()) {
			switch (attribute.getKey()) {
				case "id":
					updateMissing("id");
					if (!matchesRegex(SettingHandler.REGEXID, attribute.getValue())) {
						reportSyntaxError("Constant Value Attribute Checker", "Invalid id Value \"" + attribute.getValue() + "\"", false, id, valueIte);
						next = true;
					} else {
						if (localIDs.containsKey(attribute.getValue()) || SettingHandler.IDS.containsKey(attribute.getValue())) {
							reportSyntaxError("Constant Value Attribute Checker", "Duplicate ID found \"" + attribute.getValue() + "\"", false, id, valueIte);
							next = true;
						} else {
							valueID = attribute.getValue();
						}
					}
				break;
				case "isKey":
					updateMissing("isKey");
					if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
						reportSyntaxError("Constant Value Attribute Checker", "Invalid isKey Value \"" + attribute.getValue() + "\"", false, id, valueIte);
						next = true;
					} else {
						isKey = Boolean.parseBoolean(attribute.getValue());
					}
				break;
				case "useHeader":
					updateMissing("useHeader");
					if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
						reportSyntaxError("Constant Attribute Checker", "Invalid useHeader value \"" + attribute.getValue() + "\"", false, id, valueIte);
						next = true;
					}
				break;
				case "backReference":
					updateMissing("backReference");
					if (!matchesRegex(SettingHandler.REGEXBOOL, attribute.getValue())) {
						reportSyntaxError("Constant Value Attribute Checker", "Invalid backRefernce Value \"" + attribute.getValue() + "\"", false, id, valueIte);
						next = true;
					} else {
						isBackReference = Boolean.parseBoolean(attribute.getValue());
						backReference = value.getValue();
					}
				break;
				default:
					reportSyntaxError("Constant Value Attribute Checker", "Unknown Attribute Value \"" + attribute.getKey() + "\" = \"" + attribute.getValue(), true, id , valueIte);
			}
		}
		if (!hasAll()) {
			reportSyntaxError("Constant Value Attribute Checker", "Missing attribute(s) (" + printMissing() + ")", false, id , valueIte);
			next = true;
		}
		if (isKey && isBackReference) {
			reportSyntaxError("Constant Value Attribute Checker", "A value cannot be a key and a back reference", false, id, valueIte);
			next = true;
		}
		if (isKey) {
			if (value.getValue() == null || value.getValue().isEmpty()) {
				reportSyntaxError("Constant Value Attribute Checker", "An empty value with isKey=\"true\" is not allowed", false, id, valueIte);
				next = true;
			}
		}
		if (isBackReference) {
			if (!matchesRegex(SettingHandler.REGEXID, value.getValue())) {
				reportSyntaxError("Constant Value Attribute Checker", "A back reference Value must be an id", false, id, valueIte);
				next = true;
			} else if (id.equals(backReference)) {
				reportSyntaxError("Constant Value Attribute Checker", "The back reference cannot reference its own constant", false, id, valueIte);
			next = true;
			}
		}
		
		if (next) {
			reportSyntaxError("Constant Value Checker", "Disabling last mentioned Value", false);
		} else {
			localIDs.put(valueID, IDType.Value);
			if (isBackReference) {
				toCheckRefernce.add(new Pair<String, String>(backReference, id));
			}
		}
		
		return next;
	}
	
	public static void createMissingTable(String... keys) {
		HashMap<String, Boolean> table = new HashMap<String, Boolean>();
		for (String key : keys) {
			table.put(key, false);
		}
		SettingHandler.missingTable = table;
	}
	
	public static void updateMissing(String key) {
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
