package trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.listener.Listener;
import settings.Setting;
import settings.SettingHandler;

public class TriggerHandler {
	
	private static ConcurrentHashMap<String, Trigger> triggers = new ConcurrentHashMap<String, Trigger>(); //List of all the triggers which will be swept through if their group is active
	private static ConcurrentHashMap<String, Thread> triggerThreads = new ConcurrentHashMap<String, Thread>(); //List of all the trigger threads
	private static ConcurrentHashMap<String, Boolean> triggerThreadStates = new ConcurrentHashMap<String, Boolean>(); //List of all trigger thread states (running or not running)
	protected static ConcurrentHashMap<String, List<Pair<String, String>>> listenerReports = new ConcurrentHashMap<String, List<Pair<String, String>>>();
	protected static ConcurrentHashMap<String, List<Pair<String, String>>> responderReports = new ConcurrentHashMap<String, List<Pair<String,String>>>();
	private static Setting triggerMasterSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String TYPENAME = "type";
	private static final String RESPONDERIDSNAME = "responderIDs";
	private static final String TRIGGEREDBYNAME = "triggeredBy";
	private static final String COOLDOWNNAME = "cooldown";
	private static final String SETTINGNAME = "Trigger";
	
	public static void init(Setting triggerMasterSetting) {
		TriggerHandler.triggerMasterSetting = triggerMasterSetting;
		for (Setting triggerSetting : triggerMasterSetting.getSettings(TriggerHandler.SETTINGNAME)) {
			if (!triggerSetting.isEnabled()) {
				continue;
			}
			String id = triggerSetting.getAttribute(TriggerHandler.IDNAME);
			String name = triggerSetting.getAttribute(TriggerHandler.NAMENAME);
			TriggerType type = TriggerType.valueOf(triggerSetting.getAttribute(TriggerHandler.TYPENAME));
			ArrayList<Pair<String, String>> responderIDs = new ArrayList<Pair<String, String>>();
			String[] responderIDArray = triggerSetting.getAttribute(TriggerHandler.RESPONDERIDSNAME).split(",");
			for (int i = 0; i < responderIDArray.length; i += 2) {
				responderIDs.add(new Pair<String, String>(responderIDArray[i], responderIDArray[i + 1]));
			}
			int cooldown = (type == TriggerType.Timer) ? Integer.parseInt(TriggerHandler.COOLDOWNNAME) : 0; 
			ArrayList<String> triggeredBy = new ArrayList<String>(Arrays.asList(triggerSetting.getAttribute(TriggerHandler.TRIGGEREDBYNAME).split(",")));
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
		for (Setting triggerSetting : TriggerHandler.triggerMasterSetting.getSettings(TriggerHandler.SETTINGNAME)) {
			if (!triggerSetting.isEnabled()) {
				continue;
			}
			String id = triggerSetting.getAttribute(TriggerHandler.IDNAME);
			if (TriggerHandler.triggers.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				Trigger trigger = TriggerHandler.triggers.get(id);
				TriggerType type = trigger.getType();
				String name = triggerSetting.getAttribute(TriggerHandler.NAMENAME);
				String responderIDs = responderIDsToString(trigger.getResponderIDs());
				newAttributes.put(TriggerHandler.NAMENAME, name);
				newAttributes.put(TriggerHandler.IDNAME, id);
				newAttributes.put(TriggerHandler.RESPONDERIDSNAME, responderIDs);
				newAttributes.put(TriggerHandler.TYPENAME, type.name());
				if (type == TriggerType.Timer) {
					newAttributes.put(TriggerHandler.COOLDOWNNAME, String.valueOf(trigger.getCooldown()));
				} else if (type == TriggerType.Listener || type == TriggerType.Responder) {
					newAttributes.put(TriggerHandler.TRIGGEREDBYNAME, SettingHandler.alts(trigger.getTriggeredBy()));
				}
				triggerSetting.addReplaceAttributes(newAttributes);
			}
		}
	}
	
	public static void addTrigger(Trigger trigger) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(TriggerHandler.IDNAME, trigger.getTriggerID());
		attributes.put(TriggerHandler.NAMENAME, trigger.getTriggerName());
		attributes.put(TriggerHandler.RESPONDERIDSNAME, responderIDsToString(trigger.getResponderIDs()));
		attributes.put(TriggerHandler.TYPENAME, trigger.getType().name());
		if (trigger.getType() == TriggerType.Listener || trigger.getType() == TriggerType.Responder) {
			attributes.put(TriggerHandler.TRIGGEREDBYNAME, SettingHandler.alts(trigger.getTriggeredBy()));
		} else if (trigger.getType() == TriggerType.Timer) {
			attributes.put(TriggerHandler.COOLDOWNNAME, String.valueOf(trigger.getCooldown()));
		}
		TriggerHandler.triggerMasterSetting.addSetting(TriggerHandler.SETTINGNAME, null, attributes);
		TriggerHandler.triggers.put(trigger.getTriggerID(), trigger);
		TriggerHandler.triggerThreads.put(trigger.getTriggerID(), new Thread(trigger));
		TriggerHandler.triggerThreadStates.put(trigger.getTriggerID(), false);
	}
	
	private static String responderIDsToString(ArrayList<Pair<String, String>> responderIDs) {
		String responderIDsVal = "";
		for (Pair<String, String> responderIDP : responderIDs) {
			responderIDsVal += responderIDP.getKey() + "," + responderIDP.getValue() + ",";
		}
		responderIDsVal = (responderIDsVal.length() > 0) ? responderIDsVal.substring(0, responderIDsVal.length() - 1) : responderIDsVal;
		return responderIDsVal;
	}
	
	public static void reportListener(String listenerID, String header, String body) {
		TriggerHandler.listenerReports.get(listenerID).add(new Pair<String, String>(header, body));
	}
	
	public static void reportResponder(String responderID, String header, String body) {
		TriggerHandler.responderReports.get(responderID).add(new Pair<String, String>(header, body));
	}
	
	public static void registerListener(String id) {
		TriggerHandler.listenerReports.put(id, Collections.synchronizedList(new ArrayList<Pair<String, String>>()));
	}
	
	public static void registerResponder(String id) {
		TriggerHandler.responderReports.put(id, Collections.synchronizedList(new ArrayList<Pair<String,String>>()));
	}
}
