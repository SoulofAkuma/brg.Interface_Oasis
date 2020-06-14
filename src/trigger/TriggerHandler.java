package trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.listener.Listener;
import settings.Setting;

public class TriggerHandler {
	
	private static ConcurrentHashMap<String, Trigger> triggers = new ConcurrentHashMap<String, Trigger>(); //List of all the triggers which will be swept through if their group is active
	private static ConcurrentHashMap<String, Thread> triggerThreads = new ConcurrentHashMap<String, Thread>(); //List of all the trigger threads
	private static ConcurrentHashMap<String, Boolean> triggerThreadStates = new ConcurrentHashMap<String, Boolean>(); //List of all trigger thread states (running or not running)
	protected static ConcurrentHashMap<String, ArrayList<Pair<String, String>>> listenerReports = new ConcurrentHashMap<String, ArrayList<Pair<String, String>>>();
	protected static ConcurrentHashMap<String, ArrayList<Pair<String, String>>> responderReports = new ConcurrentHashMap<String, ArrayList<Pair<String,String>>>();
	
	public static void init(Setting triggerMasterSetting) {
		for (Setting triggerSetting : triggerMasterSetting.getSubsettings()) {
			if (!triggerSetting.isEnabled()) {
				continue;
			}
			String id = triggerSetting.getAttribute("id");
			String name = triggerSetting.getAttribute("name");
			TriggerType type = TriggerType.valueOf(triggerSetting.getAttribute("type"));
			ArrayList<Pair<String, String>> responderIDs = new ArrayList<Pair<String, String>>();
			String[] responderIDArray = triggerSetting.getAttribute("responderIDs").split(",");
			for (int i = 0; i < responderIDArray.length; i += 2) {
				responderIDs.add(new Pair<String, String>(responderIDArray[i], responderIDArray[i + 1]));
			}
			int cooldown = (type == TriggerType.Timer) ? Integer.parseInt("cooldown") : 0; 
			ArrayList<String> triggeredBy = new ArrayList<String>(Arrays.asList(triggerSetting.getAttribute("triggeredBy").split(",")));
			Trigger trigger = new Trigger(type, responderIDs, id, name, triggeredBy, cooldown);
			triggers.put(id, trigger);
			triggerThreads.put(id, new Thread(trigger));
			triggerThreadStates.put(id, false);
		}
	}
	
	public static void runTrigger(String triggerID) {
		if (!TriggerHandler.triggerThreadStates.get(triggerID)) {
			triggerThreads.get(triggerID).start();
			triggerThreadStates.put(triggerID, true);
		}
	}
	
	public static void stopTrigger(String triggerID) {
		if (TriggerHandler.triggerThreadStates.get(triggerID)) {
			triggers.get(triggerID).stopTrigger();
			try {
				triggerThreads.get(triggerID).join();
			} catch (InterruptedException e) {}
			triggerThreadStates.put(triggerID, false);
		}
	}
	
	public static void close() {
		for (Map.Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			stopTrigger(trigger.getKey());
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
