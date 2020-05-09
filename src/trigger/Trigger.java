package trigger;

import java.util.ArrayList;

import group.listener.ListenerHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class Trigger implements Runnable {
	
	private TriggerType type; //Type of the trigger
	private ArrayList<String> responderIDs = new ArrayList<String>(); //IDs of responders to trigger
	private ArrayList<String> parserIDs = new ArrayList<String>(); //Parsers to be applied on the 
	private String triggerID;
	private String triggerName;
	private String groupID;
	private String groupName;
	private String listenerID;
	private boolean trigger = false;
	private boolean crossGroup;
	private boolean runMe = false;
	private int cooldown;
	
	public Trigger(TriggerType type, ArrayList<String> responderIDs, String groupID, String groupName) {
		this.type = type;
		this.responderIDs.addAll(responderIDs);
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	@Override
	public void run() {
		this.runMe = true;
		switch (this.type) {
			case Manual:
				while (runMe) {
					if (trigger) {
						reportTrigger("manually");
						triggerMe();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			break;
			case Listener:
				int size = ListenerHandler.getRequest(this.listenerID).size();
				while (runMe) {
					if (ListenerHandler.getRequest(this.listenerID).size() > size) {
						reportTrigger("by Listener " + this.listenerID + " \"" + ListenerHandler.getListenerName(this.listenerID) + "\"");
						triggerMe(process(ListenerHandler.getRequest(this.listenerID).get(size)));
						size++;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			break;
			case Timer:
				int passed = 0;
				while (runMe) {
					if (this.cooldown == passed) {
						reportTrigger("by internal timer");
						triggerMe();
						passed = 0;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			break;
		}
	}
	
	private String process(String[] request) {
		String result = "";
		
		return result;
	}
	
	private void triggerMe() {
		for (String responderID : this.responderIDs) {
			TriggerHandler.getResponder(responderID).repond();
		}
	}
	
	private void triggerMe(String response) {
		for (String responderID : this.responderIDs) {
			TriggerHandler.getResponder(responderID).repond(response);
		}		
	}
	
	public void trigger() {
		this.trigger = true;
	}
	
	private void reportTrigger(String reason) {
		String message = "Trigger " + this.triggerID + " \"" + this.triggerName + "\" triggered " + reason;
		Logger.addMessage(MessageType.Information, MessageOrigin.Trigger, message, this.triggerID, null, null, false);
	}

}
