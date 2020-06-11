package trigger;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.listener.Listener;
import settings.Setting;

public class TriggerHandler {
	
	private static ConcurrentHashMap<String, Trigger> triggers = new ConcurrentHashMap<String, Trigger>(); //List of all the triggers which will be swept through if their group is active
	protected static ConcurrentHashMap<String, ArrayList<Pair<String, String>>> listenerReports = new ConcurrentHashMap<String, ArrayList<Pair<String, String>>>();
	protected static ConcurrentHashMap<String, ArrayList<Pair<String, String>>> responderReports = new ConcurrentHashMap<String, ArrayList<Pair<String,String>>>();
	
	public static void init(Setting triggerMasterSetting) {
		for (Setting trigger : triggerMasterSetting.getSubsettings()) {
			if (!trigger.isEnabled()) {
				continue;
			}
			
		}
	}
	
	public static void reportListener(String listenerID, String header, String body) {
		TriggerHandler.listenerReports.get(listenerID).add(new Pair<String, String>(header, body));
	}
	
	public static void reportResponder(String responderID, String header, String body) {
		TriggerHandler.responderReports.get(responderID).add(new Pair<String, String>(header, body));
	}
	
	public static void registerListener(String id) {
		TriggerHandler.listenerReports.put(id, new ArrayList<Pair<String, String>>());
	}
	
	public static void registerResponder(String id) {
		TriggerHandler.responderReports.put(id, new ArrayList<Pair<String,String>>());
	}
}
