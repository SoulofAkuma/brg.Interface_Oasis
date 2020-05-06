package settings;

import java.net.Socket;
import java.util.ArrayList;

import cc.Pair;
import filehandler.Manager;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class SettingHandler {
	
	private static int fileID;
	private static final String FILENAME = "settings.xml";
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis";
	private static final String BASESETTING = "InterfaceOasis";
	private static Setting masterSetting;
	
	public static final String FILEHANDLERID = "000000";
	public static final String GROUPHANDLERID = "000001";
	public static final String SETTINGHANDLERID = "000002";
	public static final String SETTINGPARSINGID = "000003";
	public static final String FILEHANDLERNAME = "File Handler";
	public static final String GROUPHANDLERNAME = "Group Handler";
	public static final String SETTINGHANDLERNAME = "Setting Handler";
	public static final String SETTINGPARSINGNAME = "Setting Parser";
	
	public static void init() {
		
		fileID = Manager.newFile(Manager.checkPath(PATH) + Manager.SEPERATOR + FILENAME);
		
		if (fileID == -1) {
			reportError("Missing Setting File", "All actions will not be saved");
			return;
		}
		
		masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);
		
		if (masterSetting.reset()) {
			Logger.addMessage(MessageType.Information, MessageOrigin.Settings, "No setting found - Resetting to default", SettingHandler.SETTINGHANDLERID, null, null, false);
			masterSetting.resetSetting(BASESETTING);
		}
		System.out.println("Value \"" + String.valueOf(masterSetting.getSettings("link").get(0).getValue()) + "\" " + masterSetting.getSettings("link").get(0).getValue() == null);
	}
	
	public static void close() {
		Manager.writeFile(fileID, masterSetting.getXML(), false);
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
	
	private static void reportError(String message, String causes) {
		String objectMessage = message + " - " + causes;
		String[] elements = {"GroupID", "GroupName", "Message", "Causes"};
		String[] values = {SettingHandler.SETTINGHANDLERID, SettingHandler.SETTINGHANDLERNAME, message, causes};
		Logger.addMessage(MessageType.Error, MessageOrigin.Settings, objectMessage, SettingHandler.SETTINGHANDLERID, elements, values, true);
	}
}
