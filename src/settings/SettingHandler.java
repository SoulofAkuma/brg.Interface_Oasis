package settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.Pair;
import filehandler.Manager;

public class SettingHandler {
	
	private static int fileID;
	private static final String FILENAME = "settings.xml";
	private static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis";
	private static final String BASESETTING = "Interface Oasis";
	private static Setting masterSetting;
	
	public final static char CR  = (char) 0x0D;
	public final static char LF  = (char) 0x0A;
	public final static String CRLF  = "" + CR + LF; 
	public static Socket socket = null;;
	
	public static void handle() {
		
		fileID = Manager.newFile(Manager.checkPath(PATH + Manager.SEPERATOR + FILENAME));
		
		masterSetting = Setting.parseSetting(Manager.readFile(fileID), 1);
		
		if (masterSetting.isCorrupt()) {
			masterSetting.resetSetting(BASESETTING);
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
}
