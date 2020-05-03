package listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.Pair;
import settings.Setting;
import connectionhandler.Handler;;

public class ListenerHandler {

	private HashMap<String, Listener> listeners = new HashMap<String, Listener>(); //Listeners created by this handler stored by listenerID, object
	private HashMap<String, Thread> listenerThreads = new HashMap<String, Thread>(); //Threads running the listeners stored by listenerID, thread
	private HashMap<String, Boolean> listenerThreadStatus = new HashMap<String, Boolean>(); //Indicates whether the corresponding listener is running
	private Setting listenerMasterSetting; //Setting in which the listeners of the corresponding group are stored in
	private String groupID; //The id of the group the handler handles the listeners for
	private String groupName; //The name of the group the handler handles the listeners for
	private String handlerID; //The unique id of the handler
	private boolean controllerRunning = false; //Indicates whether the handler has a TimeoutController for its listeners
	
	protected static HashMap<String, Pair<TimeoutController, Thread>> timerController = new HashMap<String, Pair<TimeoutController, Thread>>(); //Threads canceling connectionHandlers whenever they time out (Content-Length not accurate, Wrong formatting, body too long) stored by groupID, thread 
	public static HashMap<String, ArrayList<String[]>> inputs = new HashMap<String, ArrayList<String[]>>(); //Listener received requests stored by listenerID, {request-head, request-body} 
	
	public ListenerHandler(Setting listenerMasterSetting, String groupID, String groupName) {
		this.listenerMasterSetting = listenerMasterSetting;
		this.groupID = groupID;
		this.groupName = groupName;
		this.handlerID = listenerMasterSetting.getAttribute("id").getValue();
		TimeoutController controller = new TimeoutController();
		ListenerHandler.timerController.put(this.handlerID, new Pair<TimeoutController, Thread>(controller, new Thread(controller)));
	}
	
	public void init() {
		for (Setting listenerSetting : this.listenerMasterSetting.getSubsettings()) {
			String name = listenerSetting.getAttribute("name").getValue();
			String port = listenerSetting.getAttribute("port").getValue();
			String listenerID = listenerSetting.getAttribute("id").getValue();
			this.listeners.put(listenerID, new Listener(port, name, groupID, listenerID));
			this.listenerThreads.put(listenerID, new Thread(this.listeners.get(listenerID)));
			this.listenerThreadStatus.put(listenerID, false);
		}
	}
	
	public void runListener() {
		boolean hasListener = false;
		for (Map.Entry<String, Listener> kvp : this.listeners.entrySet()) {
			hasListener = true;
			if (!this.listenerThreadStatus.get(kvp.getKey())) {
				this.listenerThreads.get(kvp.getKey()).start();				
			}
		}
		if (hasListener && !this.controllerRunning) {
			ListenerHandler.timerController.get(this.handlerID).getValue().start();;
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
	
	public String getListenerName(String listenerID) {
		return this.listeners.get(listenerID).getName();
	}
	
	public void changeStatus(String listenerID, boolean status) {
		this.listenerThreadStatus.replace(listenerID, status);
	}
	
	public void reportError(String nameVal, String portVal, String listenerID, String cause, String errorMessage) {
		Handler.reportMessage(this.groupID, this.groupName, "ListenerID " + listenerID + " \"" + nameVal + "\":" + portVal, cause, errorMessage, false);
	}
	
	public void reportError(String nameVal, String portVal, String listenerID, String cause) {
		Handler.reportMessage(this.groupID, this.groupName, "ListenerID " + listenerID + " \"" + nameVal + "\":" + portVal, cause, false);
	}	
}
