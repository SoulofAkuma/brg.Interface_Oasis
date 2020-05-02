package listener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import cc.Pair;
import settings.Setting;
import connectionhandler.Handler;;

public class ListenerHandler {

	private HashMap<String, Listener> listeners = new HashMap<String, Listener>();
	private ArrayList<Thread> listenerThreads = new ArrayList<Thread>();
	private Setting listenerMasterSetting;
	private String groupID;
	private String groupName;
	private String handlerID;
	
	public ListenerHandler(Setting listenerMasterSetting, String groupID, String groupName) {
		this.listenerMasterSetting = listenerMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
		this.handlerID = listenerMasterSetting.getAttribute("id").getValue();
	}
	
	public void init() {
		for (Setting listenerSetting : this.listenerMasterSetting.getSubsettings()) {
			String name = listenerSetting.getAttribute("name").getValue();
			String port = listenerSetting.getAttribute("port").getValue();
			String listenerID = listenerSetting.getAttribute("id").getValue();
			listeners.put(listenerID, new Listener(port, name, groupID, listenerID));
		}
	}
	
	public void run() {
		
	}
	
	public String getListenerName(String listenerID) {
		return this.listeners.get(listenerID).getName();
	}
	
	public void reportError(String nameVal, String portVal, String listenerID, String cause, String errorMessage) {
		Handler.reportMessage(this.groupID, this.groupName, "ListenerID " + listenerID + " \"" + nameVal + "\":" + portVal, cause, errorMessage, false);
	}
	
	public void reportError(String nameVal, String portVal, String listenerID, String cause) {
		Handler.reportMessage(this.groupID, this.groupName, "ListenerID " + listenerID + " \"" + nameVal + "\":" + portVal, cause, false);
	}	
}
