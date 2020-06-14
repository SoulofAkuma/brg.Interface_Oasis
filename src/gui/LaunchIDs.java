package gui;

import java.util.ArrayList;
import java.util.Arrays;

import settings.Setting;

public class LaunchIDs {
	
	private static Setting launchIDsMasterSetting;
	private static ArrayList<String> listenerIDs;
	private static ArrayList<String> triggerIDs;
	
	public static void init(Setting launchIDsMasterSetting) {
		LaunchIDs.launchIDsMasterSetting = launchIDsMasterSetting;
		Setting listeners = launchIDsMasterSetting.getSettings("Listeners").get(0);
		Setting triggers = launchIDsMasterSetting.getSettings("Triggers").get(0);
		LaunchIDs.listenerIDs = new ArrayList<String>(Arrays.asList(listeners.getAttribute("ids").split(",")));
		LaunchIDs.triggerIDs = new ArrayList<String>(Arrays.asList(triggers.getAttribute("ids").split(",")));
	}
	
	public static void close() {
		
	}
}
