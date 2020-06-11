package trigger;

import java.time.Instant;
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
	private ArrayList<String> triggeredBy = new ArrayList<String>(); //The listenerIDs which can trigger the responder
	private boolean trigger = false;
	private boolean runMe = false;
	private int cooldown; //Timer in seconds
	
	public Trigger(TriggerType type, ArrayList<Pair<String, Pair<String, String>>> responderIDs, String actionID) {
		this.type = type;
		this.responderIDs = responderIDs;
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
				ArrayList<Integer> size = new ArrayList<Integer>();
				for (String id : this.triggeredBy) {
					size.add(TriggerHandler.listenerReports.get(id).size());
				}
				while (this.runMe) {
					for (int i = 0; i < this.triggeredBy.size(); i++) {
						if (TriggerHandler.listenerReports.get(this.triggeredBy.get(i)).size() > size.get(i)) {
							triggerMe(TriggerHandler.listenerReports.get(this.triggeredBy.get(i)).get(i));
							size.set(i, size.get(i) + 1);
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
				}
			break;
			case Timer:
				long stUnix = Instant.now().getEpochSecond();
				while (this.runMe) {
					if (Instant.now().getEpochSecond() - stUnix >= this.cooldown) {
						reportTrigger("by internal timer");
						triggerMe();
						stUnix = Instant.now().getEpochSecond();
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
			GroupHandler.getResponderHandler(grp.getValue().getKey()).respond(grp.getValue().getValue(), new HashMap<String, String>(), new HashMap<String, String>());
		}
	}
	
	private void triggerMe(Pair<String, String> params) {
		HashMap<String, String> parsedHeader = IndexAssigner.transformHeader(params.getKey());
		for (Pair<String, Pair<String, String>> grp : this.responderIDs) {
			HashMap<String, String> parsedBody = (grp.getKey() == null) ? new HashMap<String, String>() : ParserHandler.parse(grp.getKey(), params.getValue());
			GroupHandler.getResponderHandler(grp.getValue().getKey()).respond(grp.getValue().getValue(), parsedHeader, parsedBody);
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
