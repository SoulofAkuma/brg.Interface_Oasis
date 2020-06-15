package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import settings.Setting;
import settings.SettingHandler;

public class LaunchIDS {
	
	private static Setting launchIDSMasterSetting;
	private static List<String> listenerIDs;
	private static List<String> triggerIDs;
	
	private static final String LISTENERSNAME = "Listeners";
	private static final String TRIGGERSNAME = "Triggers";
	private static final String IDSNAME = "ids";
	
	public static void init(Setting launchIDSMasterSetting) {
		LaunchIDS.launchIDSMasterSetting = launchIDSMasterSetting;
		Setting listeners = launchIDSMasterSetting.getSettings(LaunchIDS.LISTENERSNAME).get(0);
		Setting triggers = launchIDSMasterSetting.getSettings(LaunchIDS.TRIGGERSNAME).get(0);
		LaunchIDS.listenerIDs = Collections.synchronizedList(new ArrayList<String>(Arrays.asList(listeners.getAttribute("ids").split(","))));
		LaunchIDS.triggerIDs = Collections.synchronizedList(new ArrayList<String>(Arrays.asList(triggers.getAttribute("ids").split(","))));
	}
	
	public static void close() {
		Setting listeners = LaunchIDS.launchIDSMasterSetting.getSettings(LaunchIDS.LISTENERSNAME).get(0);
		Setting triggers = LaunchIDS.launchIDSMasterSetting.getSettings(LaunchIDS.TRIGGERSNAME).get(0);
		listeners.setAttribute(LaunchIDS.IDSNAME, SettingHandler.alts(LaunchIDS.listenerIDs));
		triggers.setAttribute(LaunchIDS.IDSNAME, SettingHandler.alts(LaunchIDS.triggerIDs));
	}
}
