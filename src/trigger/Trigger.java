package trigger;

import java.util.ArrayList;

import group.listener.ListenerHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class Trigger implements Runnable {
	
	private TriggerType type; //Type of the trigger
	private ArrayList<String> responderIDs = new ArrayList<String>(); //IDs of responders to trigger
	private String triggerID;
	private String triggerName;
	private String actionID;
	private boolean trigger = false;
	private boolean runMe = false;
	private int cooldown;
	
	public Trigger(TriggerType type, ArrayList<String> responderIDs, String actionID) {
		this.type = type;
		this.responderIDs.addAll(responderIDs);
		this.actionID = actionID;
	}
	
	@Override
	public void run() {
		this.runMe = true;
		switch (this.type) {
			case Manual:
				while (this.runMe) {
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
				int size = ListenerHandler.getRequest(this.actionID).size();
				while (this.runMe) {
					if (ListenerHandler.getRequest(this.actionID).size() > size) {
						while (ListenerHandler.getRequest(this.actionID).get(size) == null) {
							try {
								Thread.sleep(10); //Wait until the connection handler has finished its job
							} catch (InterruptedException e) {}
						}
						reportTrigger("by Listener " + this.actionID + " \"" + ListenerHandler.getListenerName(this.actionID) + "\"");
						triggerMe(ListenerHandler.getRequest(this.actionID).get(size));
						size++;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			break;
			case Timer:
				int passed = 0;
				while (this.runMe) {
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
			case Responder:
				while (this.runMe) {
					
				}
			break;
		}
	}
	
	private void triggerMe() {
		for (String responderID : this.responderIDs) {
			TriggerHandler.getResponder(responderID).repond();
		}
	}
	
	private void triggerMe(String[] response) {
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
