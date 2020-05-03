package connectionhandler;

import cc.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import listener.ListenerHandler;
import responder.ResponderHandler;
import settings.Setting;
import gui.MessageType;

public class Handler {
	
	private static Map<String, Pair<ListenerHandler, ResponderHandler>> groups = new HashMap<String, Pair<ListenerHandler, ResponderHandler>>();
	private static ArrayList<String> xmlErrors = new ArrayList<String>();
	
	public static void init(Setting handlerMasterSetting) {
		int ite = 0;
		for (Setting handlerGroup : handlerMasterSetting.getSubsettings()) {
			if (checkGroupSetting(handlerGroup, ite)) {
				String id = handlerGroup.getAttribute("id").getValue();
				String name = handlerGroup.getAttribute("name").getValue();
				ListenerHandler listenerHandler = new ListenerHandler(handlerGroup.getSettings("Listener").get(0), id, name);
				ResponderHandler responderHandler = new ResponderHandler(handlerGroup.getSettings("Responder").get(0), id, name);
				listenerHandler.init();
				responderHandler.init();
				groups.put(id, new Pair<ListenerHandler, ResponderHandler>(listenerHandler, responderHandler));
			} else {
				reportMessage("Master Group", "Group Parser", "Skipping Group", true);
			}
			ite++; //To find the corrupt setting it is easier for the user if the program counts corrupt settings as well, so every element in the stored groups is counted
		}
	}
	
	public static Pair<ListenerHandler, ResponderHandler> getGroup(String key) {
		return groups.get(key);
	}
	
	public static ListenerHandler getListenerHandler(String groupID) {
		return groups.get(groupID).getKey();
	}
	
	public static ResponderHandler getResponderHandler(String groupID) {
		return groups.get(groupID).getValue();
	}
	
	public static void runGroup(String id) {
		Pair<ListenerHandler, ResponderHandler> group = groups.get(id);
		group.getKey().run();
		group.getValue().run();
	}
	
	private static boolean checkGroupSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Group")) {
			reportMessage("Master Group", "Master Group Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return false;
		}
		int count = 0;
		String id = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", attribute.getValue())) {
						reportMessage("Master Group", "Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
				case "id":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportMessage("Master Group", "Group Attribute checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						id = attribute.getValue();
						count++;
					}
			}
		}
		if (count != 2) {
			reportMessage("Master Group", "Group Attribute checker", (2 - count) + " Missing attribute(s) in " + ite + ". Group", false);
			return false;
		}
		boolean hadListener = false;
		boolean hadResponder = false;
		for (Setting group : checkMe.getSubsettings()) {
			if (group.getName().equals("Listeners")) {
				if (hadListener) {
					reportMessage("Master Group", "Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true);
				}
				hadListener = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					if (!subject.getName().equals("Listener")) {
						reportMessage("Master Group", "Listeners Element Checker", "Unknown Element \"" + subject.getName() + "\"", true);
						continue;
					}
					for (Pair<String, String> attribute : subject.getAttributes()) {
						switch (attribute.getKey()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportMessage("Master Group", "Listener Attribute Checker", "Invalid name value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Master Group", "Listener Attribute Checker", "Invalid port value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Master Group", "Listener Attribute Checker", "Invalid id value \""  + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						default:
							reportMessage("Master Group", "Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\"", true);
						}
					}
					if (attrCount != 3) {
						reportMessage("Master Group", "Listener Attribute Checker", (3 - attrCount) + " Missing attribute(s)", true);
						return false;
					}
				}
			} else if (group.getName().equals("Responders")) {
				if (hadResponder) {
					reportMessage("Master Group", "Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true); //TODO: Assignment of IDs, calling of handlers
				}
				hadResponder = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					switch (subject.getName()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportMessage("Master Group", "Responder Value Checker", "Invalid name value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Master Group", "Responder Value Checker", "Invalid port value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Master Group", "Responder Value Checker", "Invalid id value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
					default:
						reportMessage("Master Group", "Responder Value Checker", "Unknown Responder Element \"" + subject.getName() + "\"", true);
					}
				}
				if (attrCount != 3) {
					return false;
				}
			} else { 				
				reportMessage("Master Group", "Master Element Checker", "Unknown Group Element \"" + group.getName() + "\"", true);
			}
		}
		return true;
	}
		
	public static boolean matchesRegex(String regex, String subject) {
		if (subject == null || regex == null) {
			return false;
		}
		Matcher matcher = Pattern.compile(regex).matcher(subject);
		return matcher.matches();
	}
}
