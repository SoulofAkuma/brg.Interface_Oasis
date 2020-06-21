package trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.GroupHandler;
import group.listener.Listener;
import gui.ListElement;
import gui.TriggerGUIPanel;
import parser.ParserHandler;
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
			String[] responderIDArray = (triggerSetting.getAttribute(TriggerHandler.RESPONDERIDSNAME).isBlank()) ? new String[] {} : triggerSetting.getAttribute(TriggerHandler.RESPONDERIDSNAME).split(",");
			for (int i = 0; i < responderIDArray.length; i += 2) {
				responderIDs.add(new Pair<String, String>(responderIDArray[i], responderIDArray[i + 1]));
			}
			int cooldown = (type == TriggerType.Timer) ? Integer.parseInt(triggerSetting.getAttribute(TriggerHandler.COOLDOWNNAME)) : 0; 
			ArrayList<String> triggeredBy = ((type == TriggerType.Manual || type == TriggerType.Timer) || triggerSetting.getAttribute(TriggerHandler.TRIGGEREDBYNAME).isBlank()) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(triggerSetting.getAttribute(TriggerHandler.TRIGGEREDBYNAME).split(",")));
			Trigger trigger = new Trigger(type, responderIDs, id, name, triggeredBy, cooldown);
			triggers.put(id, trigger);
			triggerThreads.put(id, new Thread(trigger));
			triggerThreadStates.put(id, false);
		}
	}
	
	public static void runTrigger(String triggerID) {
		if (!TriggerHandler.triggerThreadStates.get(triggerID)) {
			triggerThreadStates.put(triggerID, true);
			triggerThreads.put(triggerID, new Thread(TriggerHandler.triggers.get(triggerID)));
			triggerThreads.get(triggerID).start();
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
				newAttributes.put(TriggerHandler.NAMENAME, trigger.getTriggerName());
				newAttributes.put(TriggerHandler.IDNAME, id);
				newAttributes.put(TriggerHandler.RESPONDERIDSNAME, responderIDsToString(trigger.getResponderIDs()));
				newAttributes.put(TriggerHandler.TYPENAME, trigger.getType().name());
				if (trigger.getType() == TriggerType.Timer) {
					newAttributes.put(TriggerHandler.COOLDOWNNAME, String.valueOf(trigger.getCooldown()));
				} else if (trigger.getType() == TriggerType.Listener || trigger.getType() == TriggerType.Responder) {
					newAttributes.put(TriggerHandler.TRIGGEREDBYNAME, SettingHandler.alts(trigger.getTriggeredBy()));
				}
				triggerSetting.addReplaceAttributes(newAttributes);
			}
		}
	}
	
	public static void addTrigger(Trigger trigger) {
		TriggerHandler.triggers.put(trigger.getTriggerID(), trigger);
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
	
	public static void removeTrigger(String id) {
		if (SettingHandler.removeParent(id, TriggerHandler.IDNAME, TriggerHandler.SETTINGNAME, TriggerHandler.triggerMasterSetting)) {
			TriggerHandler.triggers.remove(id);
			TriggerHandler.triggerThreads.remove(id);
			TriggerHandler.triggerThreadStates.remove(id);
		}
	}
	
	private static String responderIDsToString(List<Pair<String, String>> responderIDs) {
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
	
	public static String getTriggerName(String triggerID) {
		return TriggerHandler.triggers.get(triggerID).getTriggerName();
	}
	
	public static HashMap<String, String> getTriggeredByNameList(String triggerID) {
		List<String> subject = TriggerHandler.triggers.get(triggerID).getTriggeredBy();
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (TriggerHandler.triggers.get(triggerID).getType() == TriggerType.Listener) {
			for (String listenerID : subject) {
				resultMap.put(listenerID, GroupHandler.getListenerHandler(GroupHandler.ltgID(listenerID)).getListenerName(listenerID));
			}
		} else if (TriggerHandler.triggers.get(triggerID).getType() == TriggerType.Responder) {
			for (String responderID : subject) {
				resultMap.put(responderID, GroupHandler.getResponderHandler(GroupHandler.rtgID(responderID)).getResponder(responderID).getName());
			}
		}
		return resultMap;
	}
	
	public static List<Pair<Pair<String, String>, Pair<String, String>>> getRespondersByList(String triggerID) {
		List<Pair<String, String>> subject = TriggerHandler.triggers.get(triggerID).getResponderIDs();
		List<Pair<Pair<String, String>, Pair<String, String>>> resultList = new ArrayList<Pair<Pair<String,String>,Pair<String,String>>>();
		for (Pair<String, String> kvp : subject) {
			Pair<String, String> parserKVP = new Pair<String, String>(null, null);
			Pair<String, String> responderKVP = new Pair<String, String>(null, null);
			parserKVP.setKey(kvp.getKey());
			parserKVP.setValue(ParserHandler.getParserName(kvp.getKey()));
			responderKVP.setKey(kvp.getValue());
			responderKVP.setValue(GroupHandler.getResponderHandler(GroupHandler.rtgID(kvp.getValue())).getResponder(kvp.getValue()).getName());
			resultList.add(new Pair<Pair<String, String>, Pair<String, String>>(parserKVP, responderKVP));
		}
		return resultList;
	}
	
	public static List<TriggerGUIPanel> getTriggerPanels() {
		List<TriggerGUIPanel> panels = Collections.synchronizedList(new ArrayList<TriggerGUIPanel>());
		for (Entry<String, Trigger> kvp : TriggerHandler.triggers.entrySet()) {
			TriggerGUIPanel panel = new TriggerGUIPanel();
			panel.init(kvp.getValue(), TriggerHandler.triggerThreadStates.get(kvp.getKey()));
			panels.add(panel);
		}
		return panels;
	}
	
	public static HashMap<String, String> getTriggerNames() {
		HashMap<String, String> triggers = new HashMap<String, String>();
		for (Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			triggers.put(trigger.getKey(), trigger.getValue().getTriggerName());
		}
		return triggers;
	}
	
	public static ListElement[] getTriggerElements() {
		ArrayList<ListElement> triggers = new ArrayList<ListElement>();
		for (Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			triggers.add(new ListElement(trigger.getKey(), trigger.getValue().getTriggerName()));
		}
		return triggers.toArray(new ListElement[triggers.size()]);
	}

	public static void removeParserRefernces(String id) {
		for (Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			List<Pair<String, String>> responderIDs = trigger.getValue().getResponderIDs();
			for (Iterator<Pair<String, String>> ite = responderIDs.iterator(); ite.hasNext();) {
				Pair<String, String> responderID = ite.next();
				if (responderID.getKey().equals(id)) {
					TriggerHandler.stopTrigger(trigger.getKey());
					ite.remove();
				}
			}
		}
	}

	public static void removeResponderReferences(String id) {
		for (Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			List<Pair<String, String>> responderIDs = trigger.getValue().getResponderIDs();
			for (Iterator<Pair<String, String>> ite = responderIDs.iterator(); ite.hasNext();) {
				Pair<String, String> responderID = ite.next();
				if (responderID.getValue().equals(id)) {
					TriggerHandler.stopTrigger(trigger.getKey());
					ite.remove();
				}
			}
		}
	}

	public static void removeListenerReferences(String id) {
		for (Entry<String, Trigger> trigger : TriggerHandler.triggers.entrySet()) {
			if (trigger.getValue().getType() == TriggerType.Listener) {
				List<String> triggeredBy = trigger.getValue().getTriggeredBy();
				for (Iterator<String> ite = triggeredBy.iterator(); ite.hasNext();) {
					String lisID = ite.next();
					if (lisID.equals(id)) {
						ite.remove();
					}
				}
			}
		}
	}
}

