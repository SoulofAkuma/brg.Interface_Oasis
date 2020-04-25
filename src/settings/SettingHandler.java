package settings;

import java.util.ArrayList;
import cc.Pair;
import filehandler.Manager;

public class SettingHandler {
	
	private static int fileID;
	private static final String FILENAME = "settings.xml";
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis";
	private static Setting masterSetting;
	
	public static void handle() {
		
		fileID = Manager.newFile(PATH + Manager.SEPERATOR + FILENAME);
		
		masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);

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
	

}
