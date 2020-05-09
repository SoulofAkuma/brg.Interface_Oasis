package group;

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

public class GroupHandler {
	
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
}
