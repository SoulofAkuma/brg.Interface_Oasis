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
import settings.SettingHandler;
import gui.Logger;
import gui.MessageOrigin;
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
				reportMessage("Group Parser", "Skipping Group", true);
			}
			ite++; //To find the corrupt setting it is easier for the user if the program counts corrupt settings as well, so every element in the stored groups is counted
		}
	}
	
	public static void close() {
		for (Map.Entry<String, Pair<ListenerHandler, ResponderHandler>> kvp : groups.entrySet()) {
			kvp.getValue().getKey().stopListener();
			kvp.getValue().getValue().stopResponder();
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
	
	private static boolean checkGroupSetting(Setting checkMe, int ite) {
		if (!checkMe.getName().equals("Group")) {
			reportMessage("Group Handler Checker", "Unknown Group \"" + checkMe.getName() + "\"", true);
			return false;
		}
		int count = 0;
		String id = null;
		for (Pair<String, String> attribute : checkMe.getAttributes()) {
			switch (attribute.getKey()) {
				case "name":
					if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", attribute.getValue())) {
						reportMessage("Group Attribute Checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						count++;
					}
				break;
				case "id":
					if (!matchesRegex("[0-9]+", attribute.getValue())) {
						reportMessage("Group Attribute checker", "Invalid name value \"" + attribute.getValue() + "\"", false);
						return false;
					} else {
						id = attribute.getValue();
						count++;
					}
			}
		}
		if (count != 2) {
			reportMessage("Group Attribute checker", (2 - count) + " Missing attribute(s) in " + ite + ". Group", false);
			return false;
		}
		boolean hadListener = false;
		boolean hadResponder = false;
		for (Setting group : checkMe.getSubsettings()) {
			if (group.getName().equals("Listeners")) {
				if (hadListener) {
					reportMessage("Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true);
				}
				hadListener = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					if (!subject.getName().equals("Listener")) {
						reportMessage("Listeners Element Checker", "Unknown Element \"" + subject.getName() + "\"", true);
						continue;
					}
					for (Pair<String, String> attribute : subject.getAttributes()) {
						switch (attribute.getKey()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportMessage("Listener Attribute Checker", "Invalid name value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Listener Attribute Checker", "Invalid port value \"" + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Listener Attribute Checker", "Invalid id value \""  + String.valueOf(subject.getValue()) + "\"", false);
								return false;
							} else {
								attrCount++;
							}
							break;
						default:
							reportMessage("Listener Attribute Checker", "Unknown Listener Attribute \"" + subject.getName() + "\"", true);
						}
					}
					if (attrCount != 3) {
						reportMessage("Listener Attribute Checker", (3 - attrCount) + " Missing attribute(s)", true);
						return false;
					}
				}
			} else if (group.getName().equals("Responders")) {
				if (hadResponder) {
					reportMessage("Group Element Checker", "Duplicate Responder Definition in GroupID " + id, true); //TODO: Assignment of IDs, calling of handlers
				}
				hadResponder = true;
				int attrCount = 0;
				for (Setting subject : group.getSubsettings()) {
					switch (subject.getName()) {
						case "name":
							if (!matchesRegex("[0-9A-Za-z_.()-;,:/!?& ]+", subject.getValue())) {
								reportMessage("Responder Value Checker", "Invalid name value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "port":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Responder Value Checker", "Invalid port value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
						case "id":
							if (!matchesRegex("[0-9]+", subject.getValue())) {
								reportMessage("Responder Value Checker", "Invalid id value", false);
								return false;
							} else {
								attrCount++;
							}
						break;
					default:
						reportMessage("Responder Value Checker", "Unknown Responder Element \"" + subject.getName() + "\"", true);
					}
				}
				if (attrCount != 3) {
					return false;
				}
			} else {
				reportMessage("Master Element Checker", "Unknown Group Element \"" + group.getName() + "\"", true);
			}
		}
		return true;
	}
	
	private static void reportMessage(String source, String message, boolean isWarning) {
		MessageType type = (isWarning) ? MessageType.Warning : MessageType.Error;
		MessageOrigin origin = MessageOrigin.Setup;
		String[] elements = {"GroupID", "GroupName", "Source", "Message"};
		String[] values = {SettingHandler.GROUPHANDLERID, SettingHandler.GROUPHANDLERNAME, source, message};
		String objectMessage = source + " in " + SettingHandler.GROUPHANDLERNAME + " reported " + message;
		Logger.addMessage(type, origin, objectMessage, SettingHandler.GROUPHANDLERID, elements, values, false);
	}
		
	public static boolean matchesRegex(String regex, String subject) {
		if (subject == null || regex == null) {
			return false;
		}
		Matcher matcher = Pattern.compile(regex).matcher(subject);
		return matcher.matches();
	}
}
