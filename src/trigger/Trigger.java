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
import indexassigner.IndexAssigner;
import parser.ParserHandler;

public class Trigger implements Runnable {
	
	private TriggerType type; //Type of the trigger
	private List<Pair<String, String>> responderIDs;//Responder details to trigger from listener (parserID, responderID)
	private String triggerID; //id of the trigger
	private String triggerName; //name of the trigger
	private List<String> triggeredBy; //the ids which will be watched by the trigger (Type Responder: ResponderIDs, Type Listener: ListenerIDs)
	private boolean trigger = false; //boolean to be set by a button in the gui to initialize a manual trigger when type is manual 
	private boolean runMe = false; //boolean indicating whether the trigger should run or stop running
	private int cooldown; //Timer in seconds for Type Timer
	
	
	
	public Trigger(TriggerType type, List<Pair<String, String>> responderIDs, String triggerID,
			String triggerName, List<String> triggeredBy, int cooldown) {
		this.type = type;
		this.responderIDs = responderIDs;
		this.triggerID = triggerID;
		this.triggerName = triggerName;
		this.triggeredBy = triggeredBy;
		this.cooldown = cooldown;
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
				ArrayList<Integer> lrSize = new ArrayList<Integer>();
				for (String id : this.triggeredBy) {
					lrSize.add(TriggerHandler.listenerReports.get(id).size());
				}
				while (this.runMe) {
					for (int i = 0; i < this.triggeredBy.size(); i++) {
						if (TriggerHandler.listenerReports.get(this.triggeredBy.get(i)).size() > lrSize.get(i)) {
							lrSize.set(i, lrSize.get(i) + 1);
							triggerMeLis(TriggerHandler.listenerReports.get(this.triggeredBy.get(i)).get(lrSize.get(i) - 1));
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
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
				ArrayList<Integer> rrSize = new ArrayList<Integer>();
				for (String id : this.triggeredBy) {
					rrSize.add(TriggerHandler.responderReports.get(id).size());
				}
				while (this.runMe) {
					for (int i = 0; i < this.triggeredBy.size(); i++) {
						if (TriggerHandler.responderReports.get(this.triggeredBy.get(i)).size() > rrSize.get(i)) {
							triggerMeRes(TriggerHandler.responderReports.get(this.triggeredBy.get(i)).get(rrSize.get(i)));
							rrSize.set(i, rrSize.get(i) + 1);
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
				}
			break;
		}
	}
	
	protected void stopTrigger() {
		this.runMe = false;
	}
	
	private void triggerMe() {
		for (Pair<String, String> grp : this.responderIDs) {
			GroupHandler.getResponderHandler(GroupHandler.rtgID(grp.getValue())).respond(grp.getValue(), new HashMap<String, String>(), new HashMap<String, String>());
		}
	}
	
	private void triggerMeRes(Pair<String, String> params) {
		HashMap<String, String> parsedHeader = IndexAssigner.transformHeaderRes(params.getKey());
		for (Pair<String, String> grp : this.responderIDs) {
			HashMap<String, String> parsedBody = (grp.getKey() == null) ? new HashMap<String, String>() : ParserHandler.parse(grp.getKey(), params.getValue(), parsedHeader);
			GroupHandler.getResponderHandler(GroupHandler.rtgID(grp.getValue())).respond(grp.getValue(), parsedHeader, parsedBody);
		}	
	}
	
	private void triggerMeLis(Pair<String, String> params) {
		HashMap<String, String> parsedHeader = IndexAssigner.transformHeaderReq(params.getKey());
		for (Pair<String, String> grp : this.responderIDs) {
			HashMap<String, String> parsedBody = (grp.getKey() == null) ? new HashMap<String, String>() : ParserHandler.parse(grp.getKey(), params.getValue(), parsedHeader);
			GroupHandler.getResponderHandler(GroupHandler.rtgID(grp.getValue())).respond(grp.getValue(), parsedHeader, parsedBody);
		}		
	}
	
	public void trigger() {
		this.trigger = true;
	}
	
	private void reportTrigger(String reason) {
		String message = "Trigger " + this.triggerID + " \"" + this.triggerName + "\" triggered " + reason;
		Logger.addMessage(MessageType.Information, MessageOrigin.Trigger, message, this.triggerID, null, null, false);
	}

	public TriggerType getType() {
		return type;
	}

	public void setType(TriggerType type) {
		this.type = type;
	}

	public List<Pair<String, String>> getResponderIDs() {
		return responderIDs;
	}

	public void setResponderIDs(ArrayList<Pair<String, String>> responderIDs) {
		this.responderIDs = responderIDs;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public List<String> getTriggeredBy() {
		return triggeredBy;
	}

	public void setTriggeredBy(ArrayList<String> triggeredBy) {
		this.triggeredBy = triggeredBy;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public void setTrigger() {
		this.trigger = true;
	}

}
