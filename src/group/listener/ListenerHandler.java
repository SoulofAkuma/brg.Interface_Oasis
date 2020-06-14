package group.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cc.Pair;
import group.GroupHandler;
import group.TimeoutController;
import settings.Setting;
import trigger.TriggerHandler;

public class ListenerHandler {

	private HashMap<String, Listener> listeners = new HashMap<String, Listener>(); //Listeners created by this handler stored by listenerID, object
	private HashMap<String, Thread> listenerThreads = new HashMap<String, Thread>(); //Threads running the listeners stored by listenerID, thread
	private HashMap<String, Boolean> listenerThreadStatus = new HashMap<String, Boolean>(); //Indicates whether the corresponding listener is running
	private Setting listenerMasterSetting; //Setting in which the listeners of the corresponding group are stored in
	private String groupID; //The id of the group the handler handles the listeners for
	private String groupName; //The name of the group the handler handles the listeners for
	
	private static ConcurrentHashMap<String, String> activePorts = new ConcurrentHashMap<String, String>(); //Ports which are currently actively listened to stored by id, port
	private static ConcurrentHashMap<String, String> idToName = new ConcurrentHashMap<String, String>(); //Names of all listeners stored by id, name
	protected static ConcurrentHashMap<String, ArrayList<String[]>> inputs = new ConcurrentHashMap<String, ArrayList<String[]>>(); //Listener received requests stored by listenerID, {request-head, request-body}
	
	public ListenerHandler(Setting listenerMasterSetting, String groupID, String groupName) {
		this.listenerMasterSetting = listenerMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	public void init() {
		for (Setting listenerSetting : this.listenerMasterSetting.getSubsettings()) {
			if (!listenerSetting.isEnabled()) {
				continue;
			}
			String name = listenerSetting.getAttribute("name");
			String port = listenerSetting.getAttribute("port");
			String listenerID = listenerSetting.getAttribute("id");
			this.listeners.put(listenerID, new Listener(port, name, groupID, listenerID, this.groupName));
			this.listenerThreads.put(listenerID, new Thread(this.listeners.get(listenerID)));
			this.listenerThreadStatus.put(listenerID, false);
			ListenerHandler.idToName.put(listenerID, port);
			TriggerHandler.registerListener(listenerID);
			GroupHandler.registerListener(listenerID, this.groupID);
		}
	}
	
	public void runListener() {
		for (Map.Entry<String, Listener> kvp : this.listeners.entrySet()) {
			if (!this.listenerThreadStatus.get(kvp.getKey())) {
				this.listenerThreads.get(kvp.getKey()).start();				
			}
		}
	}
	
	public void runListener(String listenerID) {
		if (this.listenerThreadStatus.get(listenerID)) {
			listenerThreads.put(listenerID, new Thread(this.listeners.get(listenerID)));			
		}
	}
	
	public void stopListener() {
		for (Map.Entry<String, Thread> kvp : this.listenerThreads.entrySet()) {
			this.listeners.get(kvp.getKey()).setActive(false);
			try {
				kvp.getValue().join();
			} catch (InterruptedException e) {
				//Thread is already interrupted if the exception has been thrown
			}
		}
	}
	
	public void stopListener(String listenerID) {
		this.listeners.get(listenerID).setActive(false);
		try {
			this.listenerThreads.get(listenerID).join();
		} catch (InterruptedException e) {
			//Thread is already interrupted if the exception has been thrown
		}
	}
	
	public static String getListenerName(String listenerID) {
		return ListenerHandler.idToName.get(listenerID);
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
	
	public static ArrayList<String[]> getRequest(String listenerID) {
		return ListenerHandler.inputs.get(listenerID);
	}
}
