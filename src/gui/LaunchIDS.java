package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import group.GroupHandler;
import settings.Setting;
import settings.SettingHandler;
import trigger.TriggerHandler;

public class LaunchIDS {
	
	private static Setting launchIDSMasterSetting;
	private static List<String> listenerIDs;
	private static List<String> triggerIDs;
	private static boolean isRunning = false;
	
	private static final String LISTENERSNAME = "Listeners";
	private static final String TRIGGERSNAME = "Triggers";
	private static final String IDSNAME = "ids";
	
	public static void init(Setting launchIDSMasterSetting) {
		LaunchIDS.launchIDSMasterSetting = launchIDSMasterSetting;
		Setting listeners = launchIDSMasterSetting.getSettings(LaunchIDS.LISTENERSNAME).get(0);
		Setting triggers = launchIDSMasterSetting.getSettings(LaunchIDS.TRIGGERSNAME).get(0);
		LaunchIDS.listenerIDs = (listeners.getAttribute("ids").isBlank()) ? Collections.synchronizedList(new ArrayList<String>()): Collections.synchronizedList(new ArrayList<String>(Arrays.asList(listeners.getAttribute("ids").split(","))));
		LaunchIDS.triggerIDs = (triggers.getAttribute("ids").isBlank()) ? Collections.synchronizedList(new ArrayList<String>()) : Collections.synchronizedList(new ArrayList<String>(Arrays.asList(triggers.getAttribute("ids").split(",", 0))));
	}
	
	public static void startAll() {
		if (LaunchIDS.isRunning) {
			return;
		}
		LaunchIDS.isRunning = true;
		for (String listenerID : LaunchIDS.listenerIDs) {
			GroupHandler.getListenerHandler(GroupHandler.ltgID(listenerID)).runListener(listenerID);
		}
		for (String triggerID : LaunchIDS.triggerIDs) {
			TriggerHandler.runTrigger(triggerID);
		}
	}
	
	public static void stopAll() {
		if (!LaunchIDS.isRunning) {
			return;
		}
		LaunchIDS.isRunning = false;
		for (String listenerID : LaunchIDS.listenerIDs) {
			GroupHandler.getListenerHandler(GroupHandler.ltgID(listenerID)).stopListener(listenerID);
		}
		for (String triggerID : LaunchIDS.triggerIDs) {
			TriggerHandler.stopTrigger(triggerID);
		}
	}
	
	public static void close() {
		Setting listeners = LaunchIDS.launchIDSMasterSetting.getSettings(LaunchIDS.LISTENERSNAME).get(0);
		Setting triggers = LaunchIDS.launchIDSMasterSetting.getSettings(LaunchIDS.TRIGGERSNAME).get(0);
		listeners.setAttribute(LaunchIDS.IDSNAME, SettingHandler.alts(LaunchIDS.listenerIDs));
		triggers.setAttribute(LaunchIDS.IDSNAME, SettingHandler.alts(LaunchIDS.triggerIDs));
	}
	
	public static boolean isRunning() {
		return LaunchIDS.isRunning;
	}

	public static List<String> getListenerIDs() {
		return listenerIDs;
	}

	public static List<String> getTriggerIDs() {
		return triggerIDs;
	}
}
