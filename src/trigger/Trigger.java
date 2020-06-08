package trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.Pair;
import group.GroupHandler;
import group.listener.ListenerHandler;
import group.responder.ResponderHandler;
import gui.Logger;
import gui.MessageOrigin;
import gui.MessageType;
import parser.IndexAssigner;
import parser.ParserHandler;

public class Trigger implements Runnable {
	
	private TriggerType type; //Type of the trigger
	private ArrayList<Pair<String, Pair<String, String>>> responderIDs = new ArrayList<Pair<String, Pair<String, String>>>(); //Responder details to trigger from listener (parserID, (groupID, responderID))
	private String triggerID;
	private String triggerName;
	List<Pair<String, String>> triggerQueue = Collections.synchronizedList(new ArrayList<Pair<String, String>>());
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
				int size = this.triggerQueue.size();
				while (this.runMe) {
					if (this.triggerQueue.size() > size) {
						triggerMe(size);
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
		for (Pair<String, Pair<String, String>> grp : this.responderIDs) {
			GroupHandler.getResponderHandler(grp.getKey()).respond(grp.getValue());;
		}
	}
	
	private void triggerMe(int index) {
		HashMap<String, String> parsedHeader = IndexAssigner.transformHeader(this.triggerQueue.get(index).getKey());
		for (Pair<String, Pair<String, String>> grp : this.responderIDs) {
			HashMap<String, String> parsedBody = (grp.getKey() == null) ? new HashMap<String, String>() : ParserHandler.parse(grp.getKey(), this.triggerQueue.get(index).getValue());
			GroupHandler.getResponderHandler(grp.getValue().getKey()).respond(grp.getValue().getValue(), parsedHeader, parsedBody);
		}		
	}
	
	public void trigger() {
		this.trigger = true;
	}
	
	protected void triggerByListener(String header, String body) {
		this.triggerQueue.add(new Pair<String, String>(header, body));
	}
	
	private void reportTrigger(String reason) {
		String message = "Trigger " + this.triggerID + " \"" + this.triggerName + "\" triggered " + reason;
		Logger.addMessage(MessageType.Information, MessageOrigin.Trigger, message, this.triggerID, null, null, false);
	}

}
