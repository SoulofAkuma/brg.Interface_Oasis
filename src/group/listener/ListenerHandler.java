package group.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.GroupHandler;
import group.TimeoutController;
import gui.ListElement;
import gui.ListenerGUIPanel;
import settings.Setting;
import trigger.TriggerHandler;

public class ListenerHandler {

	private ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<String, Listener>(); //Listeners created by this handler stored by listenerID, object
	private ConcurrentHashMap<String, Thread> listenerThreads = new ConcurrentHashMap<String, Thread>(); //Threads running the listeners stored by listenerID, thread
	private ConcurrentHashMap<String, Boolean> listenerThreadStatus = new ConcurrentHashMap<String, Boolean>(); //Indicates whether the corresponding listener is running
	private Setting listenerMasterSetting; //Setting in which the listeners of the corresponding group are stored in
	private String groupID; //The id of the group the handler handles the listeners for
	private String groupName; //The name of the group the handler handles the listeners for
	
	private static ConcurrentHashMap<String, String> activePorts = new ConcurrentHashMap<String, String>(); //Ports which are currently actively listened to stored by id, port
	
	private static final String IDNAME = "id";
	private static final String NAMENAME = "name";
	private static final String LOGNAME = "log";
	private static final String PORTNAME = "port";
	private static final String SETTINGNAME = "Listener";
	
	public ListenerHandler(Setting listenerMasterSetting, String groupID, String groupName) {
		this.listenerMasterSetting = listenerMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	public void init() {
		for (Setting listenerSetting : this.listenerMasterSetting.getSettings(ListenerHandler.SETTINGNAME)) {
			if (!listenerSetting.isEnabled()) {
				continue;
			}
			String name = listenerSetting.getAttribute(ListenerHandler.NAMENAME);
			String port = listenerSetting.getAttribute(ListenerHandler.PORTNAME);
			String listenerID = listenerSetting.getAttribute(ListenerHandler.IDNAME);
			boolean log = Boolean.parseBoolean(listenerSetting.getAttribute(ListenerHandler.LOGNAME));
			this.listeners.put(listenerID, new Listener(port, name, groupID, this.groupName, listenerID, log));
			this.listenerThreads.put(listenerID, new Thread(this.listeners.get(listenerID)));
			this.listenerThreadStatus.put(listenerID, false);
			TriggerHandler.registerListener(listenerID);
			GroupHandler.registerListener(listenerID, this.groupID);
		}
	}
	
	public void runListener() {
		for (Map.Entry<String, Listener> kvp : this.listeners.entrySet()) {
			if (!this.listenerThreadStatus.get(kvp.getKey())) {
				this.listeners.get(kvp.getKey()).setActive(true);
				listenerThreads.put(kvp.getKey(), new Thread(this.listeners.get(kvp.getKey())));			
				this.listenerThreads.get(kvp.getKey()).start();				
			}
		}
	}
	
	public void runListener(String listenerID) {
		if (!this.listenerThreadStatus.get(listenerID)) {
			this.listenerThreadStatus.put(listenerID, true);
			listenerThreads.put(listenerID, new Thread(this.listeners.get(listenerID)));
			this.listenerThreads.get(listenerID).start();
		}
	}
	
	public void stopListener() {
		for (Map.Entry<String, Thread> kvp : this.listenerThreads.entrySet()) {
			this.listeners.get(kvp.getKey()).setActive(false);
			this.listenerThreadStatus.put(kvp.getKey(), false);
			try {
				kvp.getValue().join();
			} catch (InterruptedException e) {
				//Thread is already interrupted if the exception has been thrown
			}
		}
	}
	
	public void stopListener(String listenerID) {
		this.listeners.get(listenerID).setActive(false);
		this.listenerThreadStatus.put(listenerID, false);
		try {
			this.listenerThreads.get(listenerID).join();
		} catch (InterruptedException e) {
			//Thread is already interrupted if the exception has been thrown
		}
	}
	
	public void changeStatus(String listenerID, boolean status) {
		this.listenerThreadStatus.replace(listenerID, status);
	}
	
	public static void addActivePort(String listenerID, String port) {
		ListenerHandler.activePorts.put(listenerID, port);
	}
	
	public static void removeActivePort(String listenerID) {
		Iterator<Entry<String, String>> ite = ListenerHandler.activePorts.entrySet().iterator();
		Map.Entry<String, String> activePort = null;
		for (; ite.hasNext(); activePort = (Map.Entry<String, String>)ite.next()) {
			if (activePort.getKey().equals(listenerID)) {
				ite.remove();
			}
		}
	}
	
	public static String isListening(String port) {
		for (Map.Entry<String, String> activePort : ListenerHandler.activePorts.entrySet()) {
			if (activePort.getValue().equals(port)) {
				return activePort.getKey();
			}
		}
		return null;
	}
	
	public void close() {
		for (Setting listenerSetting : this.listenerMasterSetting.getSettings(ListenerHandler.SETTINGNAME)) {
			if (!listenerSetting.isEnabled()) {
				continue;
			}
			String id = listenerSetting.getAttribute(ListenerHandler.IDNAME);
			if (this.listeners.containsKey(id)) {
				HashMap<String, String> newAttributes = new HashMap<String, String>();
				Listener listener = this.listeners.get(id);
				newAttributes.put(ListenerHandler.IDNAME, id);
				newAttributes.put(ListenerHandler.NAMENAME, listener.getName());
				newAttributes.put(ListenerHandler.LOGNAME, String.valueOf(listener.getLog()));
				newAttributes.put(ListenerHandler.PORTNAME, String.valueOf(listener.getPort()));
				listenerSetting.addReplaceAttributes(newAttributes);
			}
		}
	}
	
	public void addListener(Listener listener) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(ListenerHandler.IDNAME, listener.getListenerID());
		attributes.put(ListenerHandler.NAMENAME, listener.getName());
		attributes.put(ListenerHandler.LOGNAME, String.valueOf(listener.getLog()));
		attributes.put(ListenerHandler.PORTNAME, String.valueOf(listener.getPort()));
		this.listenerMasterSetting.addSetting(ListenerHandler.SETTINGNAME, null, attributes);
		this.listeners.put(listener.getListenerID(), listener);
		this.listenerThreads.put(listener.getListenerID(), new Thread(listener));
		this.listenerThreadStatus.put(listener.getListenerID(), false);
		TriggerHandler.registerListener(listener.getListenerID());
		GroupHandler.registerListener(listener.getListenerID(), this.groupID);
	}
	
	public void removeListener(String id) {
		int sIDmatch = -1;
		for (Setting listenerSetting : this.listenerMasterSetting.getSettings(ListenerHandler.SETTINGNAME)) {
			if (listenerSetting.isEnabled() && listenerSetting.getAttribute(ListenerHandler.IDNAME).equals(id)) {
				sIDmatch = listenerSetting.getSID();
				break;
			}
		}
		if (sIDmatch != -1) {
			stopListener(id);
			this.listeners.remove(id);
			this.listenerThreads.remove(id);
			this.listenerThreadStatus.remove(id);
			this.listenerMasterSetting.removeSetting(sIDmatch);
			TriggerHandler.removeListenerReferences(id);
		}
	}
	
	public Listener genListener(String portString, String name, String listenerID, boolean log) {
		return new Listener(portString, name, this.groupID, this.groupName, listenerID, log);
	}
	
	public Listener getListener(String id) {
		return (this.listeners.containsKey(id)) ? this.listeners.get(id) : null;
	}
	
	public String getListenerName(String listenerID) {
		return this.listeners.get(listenerID).getName();
	}

	public ArrayList<ListElement> getListenerElements() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Listener> kvp : this.listeners.entrySet()) {
			elements.add(new ListElement(kvp.getKey(), kvp.getValue().getName()));
		}
		return elements;
	}
	
	public ArrayList<ListElement> getListenerElementsDetail() {
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		for (Entry<String, Listener> kvp : this.listeners.entrySet()) {
			String log = (kvp.getValue().getLog()) ? ", log" : "";
			elements.add(new ListElement(kvp.getKey(), kvp.getValue().getName() + " - " + kvp.getValue().getPort() + log));
		}
		return elements;
	}
	
	public HashMap<String, String> getListenerNames() {
		HashMap<String, String> listeners = new HashMap<String, String>();
		for (Entry<String, Listener> listener : this.listeners.entrySet()) {
			listeners.put(listener.getKey(), listener.getValue().getName());
		}
		return listeners;
	}
	
	public List<ListenerGUIPanel> getListenerPanels() {
		ArrayList<ListenerGUIPanel> panels = new ArrayList<ListenerGUIPanel>();
		for (Entry<String, Listener> listener : this.listeners.entrySet()) {
			ListenerGUIPanel panel = new ListenerGUIPanel();
			panel.init(listener.getValue());
			panels.add(panel);
		}
		return panels;
	}
	
	public String getGroupID() {
		return this.groupID;
	}
	
	public String getGroupName() {
		return this.groupName;
	}
}
