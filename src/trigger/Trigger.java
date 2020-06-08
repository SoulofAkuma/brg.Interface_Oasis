package trigger;

import java.util.ArrayList;

import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;

public class Trigger implements Runnable {
	
	private TriggerType type; //Type of the trigger
	private ArrayList<String> responderIDs = new ArrayList<String>(); //IDs of responders to trigger
	private String triggerID;
	private String triggerName;
	ArrayList<String[]> triggerQueue = new ArrayList<String[]>();
	private boolean trigger = false;
	private boolean runMe = false;
	private int cooldown;
	
	public Trigger(TriggerType type, ArrayList<String> responderIDs, String actionID) {
		this.type = type;
		this.responderIDs.addAll(responderIDs);
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
			ResponderHandler.getResponder(responderID).repond();
		}
	}
	
	private void triggerMe(String[] response) {
		for (String responderID : this.responderIDs) {
			ResponderHandler.getResponder(responderID).repond(response);
		}		
	}
	
	public void trigger() {
		this.trigger = true;
	}
	
	protected void triggerByListener(String[] listenerResult) {
		this.triggerQueue.add(listenerResult);
	}
	
	private void reportTrigger(String reason) {
		String message = "Trigger " + this.triggerID + " \"" + this.triggerName + "\" triggered " + reason;
		Logger.addMessage(MessageType.Information, MessageOrigin.Trigger, message, this.triggerID, null, null, false);
	}

}
