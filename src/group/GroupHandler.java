package group;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import gui.GroupGUIPanel;
import gui.ListElement;
import gui.Logger;
import settings.Setting;

public class GroupHandler {
	
	private static ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>> groups = new ConcurrentHashMap<String, Pair<ListenerHandler, ResponderHandler>>();
	private static ConcurrentHashMap<String, String> groupNames = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, String> ltg = new ConcurrentHashMap<String, String>(); //Converts listener id to group id
	private static ConcurrentHashMap<String, String> rtg = new ConcurrentHashMap<String, String>(); //Converts responder id to group id
	
	private static TimeoutController controllerObj;
	private static Thread controllerThread;
	
	private static Setting groupHandlerMasterSetting;
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String LISTENERSNAME = "Listeners";
	private static final String RESPONDERNAME = "Responders";
	private static final String SETTINGNAME = "Group";
	
	public static void init(Setting groupHandlerMasterSetting) {
		GroupHandler.groupHandlerMasterSetting = groupHandlerMasterSetting;
		GroupHandler.controllerObj = new TimeoutController();
		GroupHandler.controllerThread = new Thread(GroupHandler.controllerObj);
		GroupHandler.controllerThread.start();
		for (Setting groupSetting : groupHandlerMasterSetting.getSettings(GroupHandler.SETTINGNAME)) {
			if (!groupSetting.isEnabled()) {
				continue;
			}
			String id = groupSetting.getAttribute(GroupHandler.IDNAME);
			String name = groupSetting.getAttribute(GroupHandler.NAMENAME);
			ListenerHandler listenerHandler = new ListenerHandler(groupSetting.getSettings(GroupHandler.LISTENERSNAME).get(0), id, name);
			ResponderHandler responderHandler = new ResponderHandler(groupSetting.getSettings(GroupHandler.RESPONDERNAME).get(0), id, name);
			listenerHandler.init();
			responderHandler.init();
			GroupHandler.groupNames.put(id, name);
			GroupHandler.groups.put(id, new Pair<ListenerHandler, ResponderHandler>(listenerHandler, responderHandler));
		}
	}
	
	public static String addSocketTimeout(Socket socket, int seconds) {
		return GroupHandler.controllerObj.addSocket(socket, seconds);
	}
	
	public static void removeCooldown(String tcID) {
		GroupHandler.controllerObj.removeCooldown(tcID);
	}
	
	public static void close() {
		GroupHandler.controllerObj.stop();
		try {
			GroupHandler.controllerThread.join();
		} catch (InterruptedException e) {
			Logger.reportException(GroupHandler.class.getName(), "close", e);
		}
		for (Map.Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : groups.entrySet()) {
			kvp.getValue().getKey().stopListener();
		}
		for (Setting groupSetting : GroupHandler.groupHandlerMasterSetting.getSettings(GroupHandler.SETTINGNAME)) {
			if (!groupSetting.isEnabled()) {
				continue;
			}
			String id = groupSetting.getAttribute(GroupHandler.IDNAME);
			if (GroupHandler.groups.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				String name = GroupHandler.groupNames.get(id);
				newAttributes.put(GroupHandler.IDNAME, id);
				newAttributes.put(GroupHandler.NAMENAME, name);
				groupSetting.addReplaceAttributes(newAttributes);
				GroupHandler.groups.get(id).getKey().close();
				GroupHandler.groups.get(id).getValue().close();
			}
		}
	}
	
	public static void addGroup(String name, String id) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(GroupHandler.IDNAME, id);
		attributes.put(GroupHandler.NAMENAME, name);
		Setting groupSetting = GroupHandler.groupHandlerMasterSetting.addSetting(GroupHandler.SETTINGNAME, null, attributes);
		Setting listenersSetting = groupSetting.addSetting(GroupHandler.LISTENERSNAME, null, null);
		Setting respondersSetting = groupSetting.addSetting(GroupHandler.RESPONDERNAME, null, null);
		ListenerHandler listenerHandler = new ListenerHandler(listenersSetting, id, name);
		ResponderHandler responderHandler = new ResponderHandler(respondersSetting, id, name);
		listenerHandler.init();
		responderHandler.init();
		GroupHandler.groupNames.put(id, name);
		GroupHandler.groups.put(id, new Pair<ListenerHandler, ResponderHandler>(listenerHandler, responderHandler));
	}
	
	public static void removeGroup(String id) {
		int sIDmatch = -1;
		for (Setting groupSetting : GroupHandler.groupHandlerMasterSetting.getSettings(GroupHandler.SETTINGNAME)) {
			if (groupSetting.isEnabled() && groupSetting.getAttribute(GroupHandler.IDNAME).equals(id)) {
				sIDmatch = groupSetting.getSID();
				break;
			}
		}
		if (sIDmatch != -1) {
			GroupHandler.groups.get(id).getKey().stopListener();
			GroupHandler.groups.remove(id);
			GroupHandler.groupHandlerMasterSetting.removeSetting(sIDmatch);
		}
	}
	
	public static Pair<ListenerHandler, ResponderHandler> getGroup(String key) {
		return groups.get(key);
	}
	
	public static ListenerHandler getListenerHandler(String groupID) {
		return groups.get(groupID).getKey();
	}
	
	public static ResponderHandler getResponderHandler(String groupID) {
		return groups.get(groupID).getValue();
	}
	
	public static void registerListener(String listenerID, String groupID) {
		GroupHandler.ltg.put(listenerID, groupID);
	}
	
	public static void registerResponder(String responderID, String groupID) {
		GroupHandler.rtg.put(responderID, groupID);
	}
	
	public static String ltgID(String listenerID) {
		return GroupHandler.ltg.get(listenerID);
	}
	
	public static String rtgID(String responderID) {
		return GroupHandler.rtg.get(responderID);
	}
	
	public static ListElement[] getResponderElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : GroupHandler.groups.entrySet()) {
			ResponderHandler rh = kvp.getValue().getValue();
			elements.addAll(rh.getResponderElements());
		}
		return elements.toArray(new ListElement[elements.size()]);
	}
	
	public static ListElement[] getListenerElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : GroupHandler.groups.entrySet()) {
			ListenerHandler lh = kvp.getValue().getKey();
			elements.addAll(lh.getListenerElements());
		}
		return elements.toArray(new ListElement[elements.size()]);
	}
	
	public static HashMap<String, String> getListenerNames() {
		HashMap<String, String> listeners = new HashMap<String, String>();
		for (Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : GroupHandler.groups.entrySet()) {
			ListenerHandler lh = kvp.getValue().getKey();
			listeners.putAll(lh.getListenerNames());
		}
		return listeners;
	}
	
	public static HashMap<String, String> getResponderNames() {
		HashMap<String, String> responders = new HashMap<String, String>();
		for (Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : GroupHandler.groups.entrySet()) {
			ResponderHandler rh = kvp.getValue().getValue();
			responders.putAll(rh.getResponderNames());
		}
		return responders;
	}
	
	public static void changeGroupName(String groupID, String name) {
		GroupHandler.groupNames.put(groupID, name);
	}
	
	public static String getGroupName(String groupID) {
		return GroupHandler.groupNames.get(groupID);
	}

	public static List<GroupGUIPanel> getGroupPanels() {
		ArrayList<GroupGUIPanel> panels = new ArrayList<GroupGUIPanel>();
		for (Entry<String, Pair<ListenerHandler, ResponderHandler>> group : GroupHandler.groups.entrySet()) {
			GroupGUIPanel panel = new GroupGUIPanel();
			panel.init(group.getValue(), group.getKey());
			panels.add(panel);
		}
		return panels;
	}
}
