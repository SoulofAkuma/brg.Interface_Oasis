package trigger;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.listener.Listener;
import settings.Setting;

public class TriggerHandler {
	
	private static ConcurrentHashMap<String, Trigger> triggers = new ConcurrentHashMap<String, Trigger>(); //List of all the triggers which will be swept through if their group is active
	private static ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<String, Listener>();
	protected static ConcurrentHashMap<String, ArrayList<Pair<String, String>>> listenerReports = new ConcurrentHashMap<String, ArrayList<Pair<String, String>>>();
	
	public static void init(Setting triggerMasterSetting) {
		for (Setting trigger : triggerMasterSetting.getSubsettings()) {
			
		}
	}
	
	public static void report(String listenerID, String header, String body) {
		TriggerHandler.listenerReports.get(listenerID).add(new Pair<String, String>(header, body));
	}
	
	public static void registerListener(String id) {
		TriggerHandler.listenerReports.put(id, new ArrayList<Pair<String, String>>());
	}
}
