package trigger;

import java.util.concurrent.ConcurrentHashMap;

import group.listener.Listener;
import settings.Setting;

public class TriggerHandler {
	
	private static ConcurrentHashMap<String, Trigger> triggers = new ConcurrentHashMap<String, Trigger>(); //List of all the triggers which will be swept through if their group is active
	private static ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<String, Listener>();
	
	public static void init(Setting triggerMasterSetting) {
		for (Setting trigger : triggerMasterSetting.getSubsettings()) {
			
		}
	}
	
	public static void triggerTrigger(String triggerID, String parsedHeader, String parsedBody) {
		TriggerHandler.triggers.get(triggerID).triggerByListener(parsedHeader, parsedBody);
	}
}
